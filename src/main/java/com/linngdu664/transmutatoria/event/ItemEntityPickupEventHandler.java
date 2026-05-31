package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class ItemEntityPickupEventHandler {
    @SubscribeEvent
    public static void onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        if (itemStack.getItem() instanceof EssenceMetalItem item && !itemEntity.hasPickUpDelay()) {
            int remaining = tryStore(event.getPlayer(), item, itemStack.count());
            itemEntity.getItem().setCount(remaining);
        }
    }

    private static int tryStore(Player player, EssenceMetalItem metal, int count) {
        int targetSlot = metal.getEssenceMetal().ordinal();
        AlchemistStorageBoxItem box = metal.getBox();
        int remaining = tryAddToBox(metal, count, targetSlot, player.getOffhandItem(), box);
        if (remaining <= 0) {
            return remaining;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            remaining = tryAddToBox(metal, remaining, targetSlot, inventory.getItem(i), box);
            if (remaining <= 0) {
                return remaining;
            }
        }
        return remaining;
    }

    private static int tryAddToBox(EssenceMetalItem metal, int count, int targetSlot, ItemStack tryStack, AlchemistStorageBoxItem box) {
        if (tryStack.is(box)) {
            ItemContainerContents contents = tryStack.get(DataComponents.CONTAINER);
            if (contents != null) {
                NonNullList<ItemStack> items = NonNullList.withSize(24, ItemStack.EMPTY);
                contents.copyInto(items);
                ItemStack itemStack = items.get(targetSlot);
                int realAdded = Math.min(count, metal.getMaxStackSize(itemStack) - itemStack.getCount());
                if (realAdded > 0) {
                    if (itemStack.isEmpty()) {
                        items.set(targetSlot, new ItemStack(metal, realAdded));
                    } else {
                        itemStack.setCount(itemStack.getCount() + realAdded);
                    }
                    tryStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                }
                return count - realAdded;
            }
        }
        return count;
    }
}
