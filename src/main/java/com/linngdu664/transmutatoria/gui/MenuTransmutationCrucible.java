package com.linngdu664.transmutatoria.gui;

import com.linngdu664.transmutatoria.block.entity.BlockEntityTransmutationCrucible;
import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MenuTransmutationCrucible extends AbstractContainerMenu {
    private final Container container;

    public MenuTransmutationCrucible(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(BlockEntityTransmutationCrucible.SLOT_COUNT));
    }

    public MenuTransmutationCrucible(int containerId, Inventory playerInventory, BlockEntityTransmutationCrucible blockEntity) {
        super(InitMenuTypes.TRANSMUTATION_CRUCIBLE_MENU.get(), containerId);
        this.container = new BlockEntityContainer(blockEntity);
        addSlots(playerInventory);
    }

    private MenuTransmutationCrucible(int containerId, Inventory playerInventory, Container container) {
        super(InitMenuTypes.TRANSMUTATION_CRUCIBLE_MENU.get(), containerId);
        this.container = container;
        addSlots(playerInventory);
    }

    private void addSlots(Inventory playerInventory) {
        addSlot(new Slot(container, 0, 56, 17));
        addSlot(new Slot(container, 1, 56, 53));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < BlockEntityTransmutationCrucible.SLOT_COUNT) {
                if (!moveItemStackTo(stack, BlockEntityTransmutationCrucible.SLOT_COUNT, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, BlockEntityTransmutationCrucible.SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
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
        return container.stillValid(player);
    }

    private static class BlockEntityContainer implements Container {
        private final BlockEntityTransmutationCrucible blockEntity;

        BlockEntityContainer(BlockEntityTransmutationCrucible blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Override
        public int getContainerSize() {
            return BlockEntityTransmutationCrucible.SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            return blockEntity.getItems().stream().allMatch(ItemStack::isEmpty);
        }

        @Override
        public ItemStack getItem(int slot) {
            return blockEntity.getItems().get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            var items = blockEntity.getItems();
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                var result = stack.split(amount);
                blockEntity.notifyChanged();
                return result;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            var items = blockEntity.getItems();
            ItemStack stack = items.get(slot);
            items.set(slot, ItemStack.EMPTY);
            blockEntity.notifyChanged();
            return stack;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            blockEntity.getItems().set(slot, stack);
            blockEntity.notifyChanged();
        }

        @Override
        public void setChanged() {
            blockEntity.notifyChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            var lvl = blockEntity.getLevel();
            return lvl != null
                    && lvl.getBlockEntity(blockEntity.getBlockPos()) == blockEntity
                    && player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5,
                            blockEntity.getBlockPos().getY() + 0.5,
                            blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
        }

        @Override
        public void clearContent() {
            blockEntity.getItems().replaceAll(ignored -> ItemStack.EMPTY);
            blockEntity.notifyChanged();
        }
    }
}
