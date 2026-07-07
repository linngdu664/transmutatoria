package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public class AlchemyRecipeGeneratorMenu extends AbstractContainerMenu {
    public static final int SAMPLE_SLOTS = 2;
    private static final int PLAYER_INVENTORY_START = SAMPLE_SLOTS;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int TOTAL_SLOTS = HOTBAR_START + 9;

    public static final int INPUT_SLOT_X = 51;
    public static final int OUTPUT_SLOT_X = 205;
    public static final int SAMPLE_SLOT_Y = 31;

    private final Container samples;
    private final Kind kind;
    private final DataSlot saveRevision = DataSlot.standalone();
    private final DataSlot saveResult = DataSlot.standalone();

    public AlchemyRecipeGeneratorMenu(int containerId, Inventory inventory, Kind kind) {
        this(containerId, inventory, new SimpleContainer(SAMPLE_SLOTS), kind);
    }

    private AlchemyRecipeGeneratorMenu(int containerId, Inventory inventory, Container samples, Kind kind) {
        super(menuType(kind), containerId);
        this.samples = samples;
        this.kind = kind;
        checkContainerSize(samples, SAMPLE_SLOTS);

        addSlot(new SampleSlot(samples, 0, INPUT_SLOT_X, SAMPLE_SLOT_Y));
        addSlot(new SampleSlot(samples, 1, OUTPUT_SLOT_X, SAMPLE_SLOT_Y));
        addDataSlot(saveRevision);
        addDataSlot(saveResult);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 55 + column * 18, 157 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 55 + column * 18, 215));
        }
    }

    private static MenuType<AlchemyRecipeGeneratorMenu> menuType(Kind kind) {
        return kind == Kind.REPLICATION
                ? InitMenuTypes.ALCHEMICAL_REPLICATION_GENERATOR_MENU.get()
                : InitMenuTypes.ALCHEMICAL_TRANSFORMATION_GENERATOR_MENU.get();
    }

    public Kind kind() {
        return kind;
    }

    public ItemStack inputSample() {
        return samples.getItem(0);
    }

    public ItemStack outputSample() {
        return samples.getItem(1);
    }

    public ItemStack tagSample() {
        return samples.getItem(kind == Kind.REPLICATION ? 1 : 0);
    }

    public void clearSamples() {
        samples.setItem(0, ItemStack.EMPTY);
        samples.setItem(1, ItemStack.EMPTY);
        broadcastChanges();
    }

    public void setSaveResult(boolean success) {
        saveResult.set(success ? 1 : -1);
        saveRevision.set(saveRevision.get() + 1);
        broadcastChanges();
    }

    public int saveRevision() {
        return saveRevision.get();
    }

    public boolean lastSaveSucceeded() {
        return saveResult.get() > 0;
    }

    @Override
    public void clicked(int slotIndex, int buttonNum, ContainerInput input, Player player) {
        if (slotIndex >= 0 && slotIndex < SAMPLE_SLOTS
                && (input == ContainerInput.PICKUP || input == ContainerInput.QUICK_MOVE)) {
            ItemStack sample = input == ContainerInput.QUICK_MOVE ? ItemStack.EMPTY : getCarried();
            samples.setItem(slotIndex, sample.isEmpty() ? ItemStack.EMPTY : sample.copyWithCount(1));
            broadcastChanges();
            return;
        }
        super.clicked(slotIndex, buttonNum, input, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }
        if (index < SAMPLE_SLOTS) {
            samples.setItem(index, ItemStack.EMPTY);
            broadcastChanges();
            return ItemStack.EMPTY;
        }

        ItemStack stack = slots.get(index).getItem();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int target = samples.getItem(0).isEmpty() ? 0 : samples.getItem(1).isEmpty() ? 1 : -1;
        if (target < 0) {
            return ItemStack.EMPTY;
        }
        samples.setItem(target, stack.copyWithCount(1));
        broadcastChanges();
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static class SampleSlot extends Slot {
        SampleSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return false;
        }
    }

    public enum Kind {
        REPLICATION,
        TRANSFORMATION
    }
}
