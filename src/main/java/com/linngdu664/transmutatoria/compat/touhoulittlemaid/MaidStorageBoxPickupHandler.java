package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidPickupEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.transaction.Transaction;

final class MaidStorageBoxPickupHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMaidPickup(MaidPickupEvent.ItemResultPre event) {
        EntityMaid maid = event.getMaid();
        ItemEntity itemEntity = event.getEntityItem();
        if (maid.level().isClientSide()
                || !itemEntity.isAlive()
                || itemEntity.hasPickUpDelay()
                || !(itemEntity.getItem().getItem() instanceof EssenceMetalItem)) {
            return;
        }

        ItemStack original = itemEntity.getItem();
        MaidStorageBoxInventory boxes = new MaidStorageBoxInventory(maid);
        ItemStack remaining = boxes.insertEssence(original);
        try (Transaction transaction = Transaction.openRoot()) {
            remaining = ItemsUtil.insertItemStacked(
                    maid.getAvailableBackpackInv(),
                    remaining,
                    false,
                    transaction
            );
            int picked = original.getCount() - remaining.getCount();
            if (picked <= 0) {
                return;
            }
            event.setCanPickup(true);
            event.setCanceled(true);
            if (event.isSimulate()) {
                return;
            }

            transaction.commit();
            boxes.apply();
            maid.take(itemEntity, picked);
            maid.tryPlayMaidPickupSound();
            NeoForge.EVENT_BUS.post(new MaidPickupEvent.ItemResultPost(maid, original.copyWithCount(picked)));
            if (remaining.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(remaining);
            }
        }
    }
}
