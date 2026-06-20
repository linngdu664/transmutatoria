package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.recipe.TransmutationCrystalCauldronProcess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public final class TransmutationCrystalCauldronEventHandler {
    private static final double INNER_MIN = 2.0 / 16.0;
    private static final double INNER_MAX = 14.0 / 16.0;
    private static final double INNER_FLOOR = 4.0 / 16.0;

    private TransmutationCrystalCauldronEventHandler() {
    }

    @SubscribeEvent
    public static void onItemTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ItemEntity trigger)
                || trigger.level().isClientSide()
                || trigger.isRemoved()
                || !isIngredient(trigger.getItem())) {
            return;
        }
        ServerLevel level = (ServerLevel)trigger.level();
        BlockPos cauldronPos = findCauldron(level, trigger);
        if (cauldronPos == null) {
            return;
        }

        AABB interior = getInterior(cauldronPos);
        List<ItemEntity> items = level.getEntitiesOfClass(
                ItemEntity.class,
                interior,
                entity -> !entity.isRemoved() && isIngredient(entity.getItem())
        );

        int craftCount = getCraftCount(items);
        if (craftCount <= 0) {
            return;
        }

        for (var requirement : TransmutationCrystalCauldronProcess.REQUIREMENTS) {
            consume(items, requirement.item(), requirement.count() * craftCount);
        }
        spawnResults(level, cauldronPos, craftCount * TransmutationCrystalCauldronProcess.RESULT_COUNT);
        playEffects(level, cauldronPos, craftCount);
    }

    private static BlockPos findCauldron(ServerLevel level, ItemEntity itemEntity) {
        BlockPos itemPos = itemEntity.blockPosition();
        if (isValidCauldron(level, itemPos) && itemEntity.getBoundingBox().intersects(getInterior(itemPos))) {
            return itemPos;
        }

        BlockPos below = itemPos.below();
        if (isValidCauldron(level, below) && itemEntity.getBoundingBox().intersects(getInterior(below))) {
            return below;
        }
        return null;
    }

    private static boolean isValidCauldron(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER_CAULDRON)
                && level.getBlockState(pos.below()).is(Blocks.EMERALD_BLOCK);
    }

    private static AABB getInterior(BlockPos pos) {
        return new AABB(
                pos.getX() + INNER_MIN,
                pos.getY() + INNER_FLOOR,
                pos.getZ() + INNER_MIN,
                pos.getX() + INNER_MAX,
                pos.getY() + 1.0,
                pos.getZ() + INNER_MAX
        );
    }

    private static boolean isIngredient(ItemStack stack) {
        for (var requirement : TransmutationCrystalCauldronProcess.REQUIREMENTS) {
            if (stack.is(requirement.item())) {
                return true;
            }
        }
        return false;
    }

    private static int getCraftCount(List<ItemEntity> entities) {
        int craftCount = Integer.MAX_VALUE;
        for (var requirement : TransmutationCrystalCauldronProcess.REQUIREMENTS) {
            int available = 0;
            for (ItemEntity entity : entities) {
                if (entity.getItem().is(requirement.item())) {
                    available += entity.getItem().getCount();
                }
            }
            craftCount = Math.min(craftCount, available / requirement.count());
        }
        return craftCount == Integer.MAX_VALUE ? 0 : craftCount;
    }

    private static void consume(List<ItemEntity> entities, Item item, int count) {
        int remaining = count;
        for (ItemEntity entity : entities) {
            if (remaining <= 0) {
                return;
            }

            ItemStack stack = entity.getItem();
            if (!stack.is(item)) {
                continue;
            }

            int consumed = Math.min(remaining, stack.getCount());
            ItemStack remainder = stack.copyWithCount(stack.getCount() - consumed);
            remaining -= consumed;
            if (remainder.isEmpty()) {
                entity.discard();
            } else {
                entity.setItem(remainder);
            }
        }
    }

    private static void spawnResults(ServerLevel level, BlockPos pos, int count) {
        ItemStack sample = InitItems.TRANSMUTATION_CRYSTAL.toStack();
        int maxStackSize = sample.getMaxStackSize();
        while (count > 0) {
            int stackSize = Math.min(count, maxStackSize);
            ItemStack result = sample.copyWithCount(stackSize);
            ItemEntity output = new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 1.05,
                    pos.getZ() + 0.5,
                    result,
                    0.0,
                    0.12,
                    0.0
            );
            output.setDefaultPickUpDelay();
            level.addFreshEntity(output);
            count -= stackSize;
        }
    }

    private static void playEffects(ServerLevel level, BlockPos pos, int craftCount) {
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.2F);
        level.sendParticles(
                ParticleTypes.ENCHANT,
                pos.getX() + 0.5,
                pos.getY() + 0.75,
                pos.getZ() + 0.5,
                Math.min(12 + craftCount * 2, 48),
                0.3,
                0.2,
                0.3,
                0.1
        );
    }
}
