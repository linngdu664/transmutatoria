package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

final class MaidAlchemyInventory {
    private static final int NEARBY_CONTAINER_SEARCH_RADIUS = 2;
    private static final double NEARBY_CONTAINER_SEARCH_RADIUS_SQR =
            NEARBY_CONTAINER_SEARCH_RADIUS * NEARBY_CONTAINER_SEARCH_RADIUS;

    private MaidAlchemyInventory() {
    }

    static ItemStack takeInput(EntityMaid maid, ItemStack required) {
        Predicate<ItemStack> matchesRequired =
                stack -> ItemStack.isSameItemSameComponents(stack, required);
        ItemStack loose = takeLoose(maid.getAvailableBackpackInv(), matchesRequired);
        return loose.isEmpty() ? takeNearbyContainer(maid, matchesRequired) : loose;
    }

    static ItemStack takeEssence(EntityMaid maid, EssenceMetal required) {
        // 按需求先找女仆背包里的散装源质，再检查炼金术士储物盒。
        Predicate<ItemStack> matchesRequired =
                stack -> stack.getItem() instanceof EssenceMetalItem metal
                        && metal.getEssenceMetal() == required;
        ItemStack loose = takeLoose(maid.getAvailableBackpackInv(), matchesRequired);
        if (!loose.isEmpty()) {
            return loose;
        }

        MaidStorageBoxInventory boxes = new MaidStorageBoxInventory(maid);
        ItemStack stored = boxes.takeEssence(required);
        if (!stored.isEmpty()) {
            boxes.apply();
            return stored;
        }
        return takeNearbyContainer(maid, matchesRequired);
    }

    static void giveToMaid(EntityMaid maid, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() instanceof EssenceMetalItem) {
            MaidStorageBoxInventory boxes = new MaidStorageBoxInventory(maid);
            stack = boxes.insertEssence(stack);
            boxes.apply();
        }
        ItemStack remaining = ItemsUtil.insertItemStacked(
                maid.getAvailableBackpackInv(),
                stack,
                false,
                null
        );
        if (!remaining.isEmpty()) {
            maid.spawnAtLocation((net.minecraft.server.level.ServerLevel) maid.level(), remaining);
        }
    }

    static void collectOutputs(EntityMaid maid, TransmutationCrucibleBlockEntity crucible) {
        for (ItemStack stack : crucible.extractAllMaidAlchemyOutputs()) {
            giveToMaid(maid, stack);
        }
    }

    static void dropIntoCrucible(
            Level level,
            BlockPos pos,
            EntityMaid maid,
            ItemStack stack,
            boolean keepSelectedSlot
    ) {
        ItemEntity entity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                stack,
                0,
                0,
                0
        );
        entity.setThrower(maid);
        if (keepSelectedSlot) {
            entity.addTag(TransmutationCrucibleBlockEntity.MAID_ALCHEMY_DROP_TAG);
        }
        level.addFreshEntity(entity);
    }

    private static ItemStack takeLoose(
            ResourceHandler<ItemResource> inventory,
            Predicate<ItemStack> predicate
    ) {
        return takeMatching(inventory, predicate, true);
    }

    /**
     * 背包和随身储物盒都缺料时，按距离搜索女仆三米内已加载的方块容器。
     */
    private static ItemStack takeNearbyContainer(EntityMaid maid, Predicate<ItemStack> predicate) {
        if (!(maid.level() instanceof ServerLevel level)) {
            return ItemStack.EMPTY;
        }

        BlockPos center = maid.blockPosition();
        int minChunkX = SectionPos.blockToSectionCoord(center.getX() - NEARBY_CONTAINER_SEARCH_RADIUS);
        int maxChunkX = SectionPos.blockToSectionCoord(center.getX() + NEARBY_CONTAINER_SEARCH_RADIUS);
        int minChunkZ = SectionPos.blockToSectionCoord(center.getZ() - NEARBY_CONTAINER_SEARCH_RADIUS);
        int maxChunkZ = SectionPos.blockToSectionCoord(center.getZ() + NEARBY_CONTAINER_SEARCH_RADIUS);
        List<BlockPos> candidates = new ArrayList<>();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity.isRemoved()
                            || blockEntity instanceof TransmutationCrucibleBlockEntity) {
                        continue;
                    }
                    BlockPos pos = blockEntity.getBlockPos();
                    if (pos.distToCenterSqr(maid.position()) <= NEARBY_CONTAINER_SEARCH_RADIUS_SQR) {
                        candidates.add(pos.immutable());
                    }
                }
            }
        }

        candidates.sort(Comparator.comparingDouble(pos -> pos.distToCenterSqr(maid.position())));
        for (BlockPos pos : candidates) {
            ItemStack result = takeFromBlockContainer(level, pos, predicate);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack takeFromBlockContainer(
            ServerLevel level,
            BlockPos pos,
            Predicate<ItemStack> predicate
    ) {
        Set<ResourceHandler<ItemResource>> checkedHandlers =
                Collections.newSetFromMap(new IdentityHashMap<>());
        ItemStack result = takeFromUniqueHandler(
                checkedHandlers,
                level.getCapability(Capabilities.Item.BLOCK, pos, null),
                predicate
        );
        if (!result.isEmpty()) {
            return result;
        }
        for (Direction side : Direction.values()) {
            result = takeFromUniqueHandler(
                    checkedHandlers,
                    level.getCapability(Capabilities.Item.BLOCK, pos, side),
                    predicate
            );
            if (!result.isEmpty()) {
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack takeFromUniqueHandler(
            Set<ResourceHandler<ItemResource>> checkedHandlers,
            ResourceHandler<ItemResource> inventory,
            Predicate<ItemStack> predicate
    ) {
        if (inventory == null || !checkedHandlers.add(inventory)) {
            return ItemStack.EMPTY;
        }
        return takeMatching(inventory, predicate, false);
    }

    private static ItemStack takeMatching(
            ResourceHandler<ItemResource> inventory,
            Predicate<ItemStack> predicate,
            boolean skipStorageBoxes
    ) {
        if (inventory == null) {
            return ItemStack.EMPTY;
        }
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(inventory, slot);
            if ((skipStorageBoxes && stack.getItem() instanceof AlchemistStorageBoxItem)
                    || !predicate.test(stack)) {
                continue;
            }
            try (Transaction transaction = Transaction.openRoot()) {
                if (inventory.extract(slot, ItemResource.of(stack), 1, transaction) == 1) {
                    ItemStack result = stack.copyWithCount(1);
                    transaction.commit();
                    return result;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
