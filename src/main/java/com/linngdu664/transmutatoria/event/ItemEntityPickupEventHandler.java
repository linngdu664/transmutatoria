package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.util.TriState;
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
            Player player = event.getPlayer();
            int originalCount = itemStack.getCount();
            int remainingCount = tryStore(player, item, originalCount);
            int orgCount = originalCount - remainingCount;
            player.take(itemEntity, orgCount);  // 调用原版轮子发包，给客户端放音效和动画
            player.awardStat(Stats.ITEM_PICKED_UP.get(item), orgCount);
            player.onItemPickup(itemEntity);
            if (remainingCount == 0) {
                event.setCanPickup(TriState.FALSE);
                itemEntity.discard();
            }
            itemStack.setCount(remainingCount);
        }
    }

    private static int tryStore(Player player, EssenceMetalItem metal, int count) {
        int targetSlot = metal.getEssenceMetal().ordinal();
        AlchemistStorageBoxItem box = metal.getBox();
        int remaining = tryAddToBoxSlot(metal, count, targetSlot, player.getOffhandItem(), box);
        if (remaining <= 0) {
            return remaining;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            remaining = tryAddToBoxSlot(metal, remaining, targetSlot, inventory.getItem(i), box);
            if (remaining <= 0) {
                return remaining;
            }
        }
        return remaining;
    }

    private static int tryAddToBoxSlot(EssenceMetalItem metal, int count, int targetSlot, ItemStack boxStack, AlchemistStorageBoxItem boxItem) {
        if (boxStack.is(boxItem)) {
            ItemContainerContents contents = boxStack.get(DataComponents.CONTAINER);
            if (contents != null) {
                NonNullList<ItemStack> items = NonNullList.withSize(24, ItemStack.EMPTY);
                contents.copyInto(items);
                ItemStack itemStack = items.get(targetSlot);
                boolean changed = false;
                if (itemStack.isEmpty()) {
                    itemStack = metal.getDefaultInstance();
                    items.set(targetSlot, itemStack);
                    count--;
                    changed = true;
                }
                int realAdded = Math.min(count, itemStack.getMaxStackSize() - itemStack.getCount());
                if (realAdded > 0) {
                    itemStack.setCount(itemStack.getCount() + realAdded);
                    changed = true;
                }
                if (changed) {
                    boxStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
                }
                return count - realAdded;
            }
        }
        return count;
    }
}
