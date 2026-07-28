package com.linngdu664.transmutatoria.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.ItemsUtil;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 女仆背包内炼金术士储物盒的事务外快照。只有在整次操作成功后才调用 {@link #apply()}。
 */
final class MaidStorageBoxInventory {
    private final ResourceHandler<ItemResource> backpack;
    private final List<BoxSlot> boxes = new ArrayList<>();

    MaidStorageBoxInventory(EntityMaid maid) {
        this.backpack = maid.getAvailableBackpackInv();
        for (int slot = 0; slot < backpack.size(); slot++) {
            ItemStack stack = ItemUtil.getStack(backpack, slot);
            if (stack.getItem() instanceof AlchemistStorageBoxItem) {
                boxes.add(new BoxSlot(slot, stack));
            }
        }
    }

    ItemStack takeEssence(EssenceMetal essenceMetal) {
        int targetSlot = essenceMetal.ordinal();
        for (BoxSlot box : boxes) {
            ItemStack stored = box.items.get(targetSlot);
            if (stored.getItem() instanceof EssenceMetalItem metal
                    && metal.getEssenceMetal() == essenceMetal) {
                ItemStack result = stored.copyWithCount(1);
                stored.shrink(1);
                box.changed = true;
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * 优先写入与源质状态匹配的储物盒，返回未能放入的部分。
     */
    ItemStack insertEssence(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof EssenceMetalItem metal)) {
            return stack;
        }
        int remaining = stack.getCount();
        int targetSlot = metal.getEssenceMetal().ordinal();
        for (BoxSlot box : boxes) {
            if (!box.stack.is(metal.getBox())) {
                continue;
            }
            ItemStack stored = box.items.get(targetSlot);
            int capacity = stored.isEmpty()
                    ? stack.getMaxStackSize()
                    : stored.getMaxStackSize() - stored.getCount();
            if (capacity <= 0) {
                continue;
            }
            int inserted = Math.min(remaining, capacity);
            if (stored.isEmpty()) {
                box.items.set(targetSlot, stack.copyWithCount(inserted));
            } else {
                stored.grow(inserted);
            }
            box.changed = true;
            remaining -= inserted;
            if (remaining == 0) {
                return ItemStack.EMPTY;
            }
        }
        return stack.copyWithCount(remaining);
    }

    void apply() {
        for (BoxSlot box : boxes) {
            if (!box.changed) {
                continue;
            }
            box.stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(box.items));
            ItemsUtil.setStackInSlot(backpack, box.inventorySlot, box.stack);
        }
    }

    private static final class BoxSlot {
        private final int inventorySlot;
        private final ItemStack stack;
        private final NonNullList<ItemStack> items =
                NonNullList.withSize(AlchemistStorageBoxMenu.CONTAINER_SLOTS, ItemStack.EMPTY);
        private boolean changed;

        private BoxSlot(int inventorySlot, ItemStack original) {
            this.inventorySlot = inventorySlot;
            this.stack = original.copy();
            original.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        }
    }
}
