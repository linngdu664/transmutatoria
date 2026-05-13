package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import org.jspecify.annotations.Nullable;

public class MenuAlchemistStorageBox extends AbstractContainerMenu {
    private static final int CONTAINER_SLOTS = 12;
    private static final int INV_START = CONTAINER_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_START = INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private static final int TOTAL_SLOTS = HOTBAR_END;

    @Nullable
    private final ItemStack boxStack;

    // Client-side constructor
    public MenuAlchemistStorageBox(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SLOTS), 0, null);
    }

    // Server-side constructor
    public MenuAlchemistStorageBox(int containerId, Inventory playerInventory, ItemStack boxStack, int boxState) {
        this(containerId, playerInventory, new StorageBoxContainer(boxStack), boxState, boxStack);
    }

    private MenuAlchemistStorageBox(int containerId, Inventory playerInventory, Container container, int boxState, @Nullable ItemStack boxStack) {
        super(InitMenuTypes.ALCHEMIST_STORAGE_BOX_MENU.get(), containerId);
        this.boxStack = boxStack;
        addSlots(container, playerInventory, boxState);
        addPlayerInventory(playerInventory);
    }

    private void addSlots(Container container, Inventory playerInventory, int boxState) {
        EssenceMetal[] metals = EssenceMetal.values();

        double centerX = 88;
        double centerY = 85;
        double radius = 50;

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30.0 - 90.0);
            int x = (int) (centerX + radius * Math.cos(angle)) - 9;
            int y = (int) (centerY + radius * Math.sin(angle)) - 9;
            addSlot(new LockedEssenceMetalSlot(container, i, x, y, metals[i], boxState));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 138 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 196));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < CONTAINER_SLOTS) {
                if (!moveItemStackTo(stack, INV_START, TOTAL_SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                boolean moved = false;
                for (int i = 0; i < CONTAINER_SLOTS; i++) {
                    if (slots.get(i).mayPlace(stack)) {
                        if (moveItemStackTo(stack, i, i + 1, false)) {
                            moved = true;
                            break;
                        }
                    }
                }
                if (!moved) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return boxStack == null || !boxStack.isEmpty();
    }

    private static class LockedEssenceMetalSlot extends Slot {
        private final EssenceMetal expectedMetal;
        private final int expectedState;

        LockedEssenceMetalSlot(Container container, int slot, int x, int y, EssenceMetal metal, int state) {
            super(container, slot, x, y);
            this.expectedMetal = metal;
            this.expectedState = state;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.getItem() instanceof ItemEssenceMetal metalItem) {
                return metalItem.getEssenceMetal() == expectedMetal && metalItem.getState() == expectedState;
            }
            return false;
        }
    }

    static class StorageBoxContainer implements Container {
        final ItemStack stack;
        final NonNullList<ItemStack> items;

        StorageBoxContainer(ItemStack stack) {
            this.stack = stack;
            this.items = NonNullList.withSize(CONTAINER_SLOTS, ItemStack.EMPTY);
            ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            contents.copyInto(items);
        }

        private void saveToStack() {
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }

        @Override
        public int getContainerSize() {
            return CONTAINER_SLOTS;
        }

        @Override
        public boolean isEmpty() {
            return items.stream().allMatch(ItemStack::isEmpty);
        }

        @Override
        public ItemStack getItem(int slot) {
            return items.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                ItemStack result = stack.split(amount);
                saveToStack();
                return result;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack stack = items.get(slot);
            items.set(slot, ItemStack.EMPTY);
            saveToStack();
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            saveToStack();
        }

        @Override
        public void setChanged() {
            saveToStack();
        }

        @Override
        public boolean stillValid(Player player) {
            return !stack.isEmpty();
        }

        @Override
        public void clearContent() {
            items.replaceAll(ignored -> ItemStack.EMPTY);
            saveToStack();
        }
    }
}