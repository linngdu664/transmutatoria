package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitMenuTypes;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jspecify.annotations.Nullable;

public class AlchemistStorageBoxMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SLOTS = 12;
    private static final int INV_START = CONTAINER_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_START = INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private static final int TOTAL_SLOTS = HOTBAR_END;

    private final Container container;
    private final @Nullable InteractionHand openingHand;
    public final int boxState;

    // Client-side constructor
    public AlchemistStorageBoxMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SLOTS), 0, null);
    }

    // Client-side constructor with boxState
    public AlchemistStorageBoxMenu(int containerId, Inventory playerInventory, int boxState) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SLOTS), boxState, null);
    }

    // Server-side constructor
    public AlchemistStorageBoxMenu(int containerId, Inventory playerInventory, ItemStack boxStack, int boxState) {
        this(containerId, playerInventory, boxStack, boxState, null);
    }

    public AlchemistStorageBoxMenu(
            int containerId,
            Inventory playerInventory,
            ItemStack boxStack,
            int boxState,
            InteractionHand openingHand
    ) {
        this(containerId, playerInventory, new StorageBoxContainer(boxStack, boxState), boxState, openingHand);
    }

    public AlchemistStorageBoxMenu(int containerId, Inventory playerInventory, Container container, int boxState) {
        this(containerId, playerInventory, container, boxState, null);
    }

    private AlchemistStorageBoxMenu(
            int containerId,
            Inventory playerInventory,
            Container container,
            int boxState,
            @Nullable InteractionHand openingHand
    ) {
        super(getMenuType(boxState), containerId);
        checkContainerSize(container, CONTAINER_SLOTS);
        this.container = container;
        this.openingHand = openingHand;
        this.boxState = boxState;
        container.startOpen(playerInventory.player);
        addSlots(container, playerInventory, boxState);
        addPlayerInventory(playerInventory);
    }

    public static AlchemistStorageBoxMenu fromNetwork(
            int containerId,
            Inventory playerInventory,
            @Nullable RegistryFriendlyByteBuf data,
            int boxState
    ) {
        InteractionHand openingHand = data == null ? null : data.readEnum(InteractionHand.class);
        return new AlchemistStorageBoxMenu(
                containerId,
                playerInventory,
                new SimpleContainer(CONTAINER_SLOTS),
                boxState,
                openingHand);
    }

    private void addSlots(Container container, Inventory playerInventory, int boxState) {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                addSlot(new LockedEssenceMetalSlot(container, j*3+i, 14+i*12+j*36, 56+i*21, boxState));
            }
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

    private static MenuType<AlchemistStorageBoxMenu> getMenuType(int boxState) {
        return switch (boxState) {
            case -1 -> InitMenuTypes.NIGREDO_ALCHEMIST_STORAGE_BOX_MENU.get();
            case 1 -> InitMenuTypes.ALBEDO_ALCHEMIST_STORAGE_BOX_MENU.get();
            case 2 -> InitMenuTypes.CITRINITAS_ALCHEMIST_STORAGE_BOX_MENU.get();
            default -> InitMenuTypes.ALCHEMIST_STORAGE_BOX_MENU.get();
        };
    }

    public static boolean canPlaceItem(int slot, ItemStack stack, int boxState) {
        if (slot < 0 || slot >= CONTAINER_SLOTS || !(stack.getItem() instanceof EssenceMetalItem metalItem)) {
            return false;
        }
        return metalItem.getEssenceMetal() == EssenceMetal.values()[slot] && metalItem.getState() == boxState;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    public boolean isContainer(Container container) {
        return this.container == container;
    }

    public @Nullable InteractionHand getOpeningHand() {
        return openingHand;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    public static class LockedEssenceMetalSlot extends Slot {
        private final int storageSlot;
        private final int expectedState;

        LockedEssenceMetalSlot(Container container, int slot, int x, int y, int state) {
            super(container, slot, x, y);
            this.storageSlot = slot;
            this.expectedState = state;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return canPlaceItem(storageSlot, stack, expectedState);
        }
    }

    static class StorageBoxContainer implements Container {
        final ItemStack stack;
        final NonNullList<ItemStack> items;
        final int boxState;

        StorageBoxContainer(ItemStack stack, int boxState) {
            this.stack = stack;
            this.boxState = boxState;
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
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return AlchemistStorageBoxMenu.canPlaceItem(slot, stack, boxState);
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
