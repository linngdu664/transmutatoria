package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.Predicate;

final class MaidAlchemyInventory {
    private MaidAlchemyInventory() {
    }

    static ItemStack takeInput(EntityMaid maid, ItemStack required) {
        return takeLoose(maid.getAvailableBackpackInv(),
                stack -> ItemStack.isSameItemSameComponents(stack, required));
    }

    static ItemStack takeEssence(EntityMaid maid, EssenceMetal required) {
        // 按需求先找女仆背包里的散装源质，再检查炼金术士储物盒。
        ItemStack loose = takeLoose(maid.getAvailableBackpackInv(),
                stack -> stack.getItem() instanceof EssenceMetalItem metal
                        && metal.getEssenceMetal() == required);
        if (!loose.isEmpty()) {
            return loose;
        }

        MaidStorageBoxInventory boxes = new MaidStorageBoxInventory(maid);
        ItemStack stored = boxes.takeEssence(required);
        if (!stored.isEmpty()) {
            boxes.apply();
        }
        return stored;
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
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(inventory, slot);
            if (stack.getItem() instanceof AlchemistStorageBoxItem || !predicate.test(stack)) {
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
