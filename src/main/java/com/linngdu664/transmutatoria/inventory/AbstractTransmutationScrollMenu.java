package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitAdvancements;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public abstract class AbstractTransmutationScrollMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SLOTS = 2;
    private static final int INV_START = CONTAINER_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_START = INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private static final int TOTAL_SLOTS = HOTBAR_END;

    public static final int SLOT0_X = 26;
    public static final int SLOT0_Y = 52;
    public static final int SLOT1_X = 145;
    public static final int SLOT1_Y = 52;

    @NotNull
    private final ItemStack scrollStack;
    private final Inventory playerInventory;
    private final int inputSlotIndex;

    protected AbstractTransmutationScrollMenu(MenuType menuType, int containerId, Inventory playerInventory, @NonNull ItemStack scrollStack) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.scrollStack = scrollStack;
        this.inputSlotIndex = addScrollInventory(new ScrollContainer(scrollStack));
        addPlayerInventory(playerInventory);
    }

    protected static ItemStack findScrollInHands(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof AbstractTransmutationScrollItem) {
            return stack;
        }
        stack = player.getOffhandItem();
        if (stack.getItem() instanceof AbstractTransmutationScrollItem) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    abstract protected int addScrollInventory(Container container);

    private void addPlayerInventory(Inventory playerInventory) {
//        int x0 = 6;
//        int y0 = 50;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 14 + col * 18, 134 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 14 + col * 18, 192));
        }
    }

//    @Override
//    public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
//        if (slotIndex >= INV_START) {
//            super.clicked(slotIndex, button, input, player);
//            return;
//        }
//        if (slotIndex == inputSlotIndex) {
//            // 输入槽：激活时拿不动，未激活时拦截无效物品
//            if (scrollStack.get(InitDataComponents.RECIPE_CONDITIONS) != null) {
//                return;
//            }
//            ItemStack carried = getCarried();
//            if (carried.isEmpty() || (scrollStack.getItem() instanceof AbstractTransmutationScrollItem scrollItem && scrollItem.getRecipe(player.level(), carried) != null)) {
//                super.clicked(slotIndex, button, input, player);
//            }
//        }
//        // 另一个槽：拿不动
//    }

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
                Slot inputSlot = slots.get(inputSlotIndex);
                if (!inputSlot.hasItem() && moveItemStackTo(stack, inputSlotIndex, inputSlotIndex + 1, false)) {
                    // moved to input slot
                } else if (index < INV_END) {
                    if (!moveItemStackTo(stack, HOTBAR_START, TOTAL_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!moveItemStackTo(stack, INV_START, INV_END, false)) {
                        return ItemStack.EMPTY;
                    }
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
        return !scrollStack.isEmpty();
    }

    protected class InputSlot extends Slot {
        InputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (!isActive()||isActivated() || scrollStack.isEmpty() || !(scrollStack.getItem() instanceof AbstractTransmutationScrollItem scrollItem)) return false;
            return scrollItem.getRecipe(playerInventory.player.level(), stack) != null;
        }

        @Override
        public boolean mayPickup(Player player) {
            return !isActivated();
        }

        @Override
        public boolean isActive() {
            return !this.hasItem();
        }

        @Override
        public boolean allowModification(Player player) {
            return isActive();
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            Player player = playerInventory.player;
            Level level = player.level();
            if (isActivated()) {
                return;
            }
            if (level.isClientSide() || stack.isEmpty() || !(scrollStack.getItem() instanceof AbstractTransmutationScrollItem scrollItem)) {
                super.setByPlayer(stack);
                return;
            }

            // 尝试激活卷轴
            CrucibleRecipe recipe = scrollItem.getRecipe(level, stack);
            if (recipe == null) {
                return; // 配方不匹配，物品留在手上
            }

            ItemStack single = stack.copyWithCount(1);
            stack.shrink(1);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
            }
            set(single);
            int otherSlot = 1 - inputSlotIndex;
            container.setItem(otherSlot, recipe.getOtherSideItemStack());
            scrollItem.activate(level, scrollStack, single, recipe);
            InitAdvancements.award((ServerPlayer) player, InitAdvancements.SCROLL_ACTIVATED);
            broadcastChanges();
        }

        private boolean isActivated() {
            return scrollStack.get(InitDataComponents.RECIPE_CONDITIONS) != null;
        }
    }

    protected static class OtherSideSlot extends Slot {
        OtherSideSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean allowModification(Player player) {
            return false;
        }
    }

    protected static class ScrollContainer implements Container {
        final ItemStack stack;
        final NonNullList<ItemStack> items;

        ScrollContainer(ItemStack stack) {
            this.stack = stack;
            this.items = NonNullList.withSize(CONTAINER_SLOTS, ItemStack.EMPTY);
            ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            contents.copyInto(items);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
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
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            ItemStack s = items.get(slot);
            items.set(slot, ItemStack.EMPTY);
            saveToStack();
            return s;
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
            items.clear();
            saveToStack();
        }
    }
}
