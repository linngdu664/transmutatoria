package com.linngdu664.transmutatoria.gui;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitMenuTypes;
import com.linngdu664.transmutatoria.item.ItemTransmutationEquationScroll;
import com.linngdu664.transmutatoria.item.ItemTransmutationSigilScroll;
import com.linngdu664.transmutatoria.recipe.AlchemicalRecipeManager;
import com.linngdu664.transmutatoria.recipe.AlchemicalTransformationRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.Level;

public class MenuTransmutationSigilScroll extends AbstractContainerMenu {
    private static final int CONTAINER_SLOTS = 1;
    private static final int INV_START = CONTAINER_SLOTS;
    private static final int INV_END = INV_START + 27;
    private static final int HOTBAR_START = INV_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private static final int TOTAL_SLOTS = HOTBAR_END;

    public static final int SLOT_X = 80;
    public static final int SLOT_Y = 18;

    @Nullable
    private final ItemStack scrollStack;

    public MenuTransmutationSigilScroll(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CONTAINER_SLOTS), null);
    }

    public MenuTransmutationSigilScroll(int containerId, Inventory playerInventory, ItemStack scrollStack) {
        this(containerId, playerInventory, new ScrollContainer(scrollStack), scrollStack);
    }

    private final Inventory playerInventory;

    private MenuTransmutationSigilScroll(int containerId, Inventory playerInventory, Container container, @Nullable ItemStack scrollStack) {
        super(InitMenuTypes.TRANSMUTATION_SIGIL_SCROLL_MENU.get(), containerId);
        this.playerInventory = playerInventory;
        this.scrollStack = scrollStack;
        addSlot(new InputSlot(container, 0, SLOT_X, SLOT_Y));
        addPlayerInventory(playerInventory);
    }

    private class InputSlot extends Slot {
        InputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return !isActivated();
        }

        @Override
        public boolean mayPickup(Player player) {
            return !isActivated();
        }

        @Override
        public boolean isActive() {
            Slot slot = slots.get(index);
            if (slot.hasItem()) {
                return false;
            }else {
                return true;
            }
        }

        @Override
        public boolean allowModification(Player player) {
            return !isActive();
        }

        private boolean isActivated() {
            if (scrollStack == null) return false;
            Boolean v = scrollStack.get(InitDataComponents.ACTIVATED.get());
            return v != null && v;
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            if (isActivated() || scrollStack == null) {
                super.setByPlayer(stack);
                return;
            }
            Player player = playerInventory.player;
            if (player.level().isClientSide()) {
                super.setByPlayer(stack);
                return;
            }
            if (stack.isEmpty()) {
                super.setByPlayer(stack);
                return;
            }

            // 用单个物品实际激活
            ItemStack single = stack.copyWithCount(1);
            boolean activated;
            if (scrollStack.getItem() instanceof ItemTransmutationSigilScroll) {
                activated = ItemTransmutationSigilScroll.tryActivate(player.level(), scrollStack, single, container);
            } else if (scrollStack.getItem() instanceof ItemTransmutationEquationScroll) {
                activated = ItemTransmutationEquationScroll.tryActivate(player.level(), scrollStack, single, container);
            } else {
                activated = false;
            }

            if (activated) {
                stack.shrink(1);
                if (!stack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
                set(container.getItem(0));
                broadcastChanges();
            }
            // 未激活：不走默认放置，物品留在手上
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Nullable
    public ItemStack getScrollStack() {
        return scrollStack;
    }

    @Override
    public void clicked(int slotIndex, int button, ContainerInput input, Player player) {
        // 拦截输入槽位的点击：无配方物品不放进去，留在鼠标上
        if (slotIndex == 0 && scrollStack != null) {
            Boolean act = scrollStack.get(InitDataComponents.ACTIVATED.get());
            boolean activated = act != null && act;
            if (!activated) {
                ItemStack carried = getCarried();
                if (!carried.isEmpty()) {
                    ItemStack single = carried.copyWithCount(1);
                    boolean matches;
                    if (scrollStack.getItem() instanceof ItemTransmutationSigilScroll) {
                        matches = AlchemicalRecipeManager.findMatchRep(player.level(), single) != null;
                    } else if (scrollStack.getItem() instanceof ItemTransmutationEquationScroll) {
                        matches = AlchemicalRecipeManager.findMatchTrans(player.level(), single) != null;
                    } else {
                        matches = false;
                    }
                    if (!matches) {
                        return; // 不调用 super → 点击无效 → 物品留在手上
                    }
                }
            }
        }
        super.clicked(slotIndex, button, input, player);
    }

    protected boolean moveItemStackToWithMatch(ItemStack itemStack, int startSlot, int endSlot, boolean backwards, Level level) {
        AlchemicalTransformationRecipe recipe = AlchemicalRecipeManager.findMatchTrans(level, itemStack);
        if (recipe == null) {
            return false;
        }
        return moveItemStackTo(itemStack,startSlot,endSlot,backwards);
    }

    @Override
    protected boolean moveItemStackTo(ItemStack itemStack, int startSlot, int endSlot, boolean backwards) {
        boolean anythingChanged = false;


        int destSlot = startSlot;
        if (backwards) {
            destSlot = endSlot - 1;
        }

        if (itemStack.isStackable()) {
            while(!itemStack.isEmpty()) {
                if (backwards) {
                    if (destSlot < startSlot) {
                        break;
                    }
                } else if (destSlot >= endSlot) {
                    break;
                }


                Slot slot = this.slots.get(destSlot);
                ItemStack target = slot.getItem();
                if (slot.isActive() && !target.isEmpty() && ItemStack.isSameItemSameComponents(itemStack, target)) {
                    int totalStack = target.getCount() + itemStack.getCount();
                    int maxStackSize = slot.getMaxStackSize(target);
                    if (totalStack <= maxStackSize) {
                        itemStack.setCount(0);
                        target.setCount(totalStack);
                        slot.setChanged();
                        anythingChanged = true;
                    } else if (target.getCount() < maxStackSize) {
                        itemStack.shrink(maxStackSize - target.getCount());
                        target.setCount(maxStackSize);
                        slot.setChanged();
                        anythingChanged = true;
                    }
                }

                if (backwards) {
                    --destSlot;
                } else {
                    ++destSlot;
                }
            }
        }

        if (!itemStack.isEmpty()) {
            if (backwards) {
                destSlot = endSlot - 1;
            } else {
                destSlot = startSlot;
            }

            while(true) {
                if (backwards) {
                    if (destSlot < startSlot) {
                        break;
                    }
                } else if (destSlot >= endSlot) {
                    break;
                }

                Slot slotx = this.slots.get(destSlot);
                ItemStack targetx = slotx.getItem();
                if (slotx.isActive() && targetx.isEmpty() && slotx.mayPlace(itemStack)) {
                    int maxStackSize = slotx.getMaxStackSize(itemStack);
                    slotx.setByPlayer(itemStack.split(Math.min(itemStack.getCount(), maxStackSize)));
                    slotx.setChanged();
                    anythingChanged = true;
                    break;
                }

                if (backwards) {
                    --destSlot;
                } else {
                    ++destSlot;
                }
            }
        }

        return anythingChanged;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Level level = player.level();
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < CONTAINER_SLOTS) {
                if (!moveItemStackToWithMatch(stack, INV_START, TOTAL_SLOTS, true,level)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!slots.getFirst().hasItem() && moveItemStackToWithMatch(stack, 0, 1, false,level)) {
                    // moved to input slot
                } else if (index < INV_END) {
                    if (!moveItemStackToWithMatch(stack, HOTBAR_START, TOTAL_SLOTS, false,level)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!moveItemStackToWithMatch(stack, INV_START, INV_END, false,level)) {
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
        return scrollStack == null || !scrollStack.isEmpty();
    }

    static class ScrollContainer implements Container {
        final ItemStack stack;
        final NonNullList<ItemStack> items;

        ScrollContainer(ItemStack stack) {
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
            ItemStack s = items.get(slot);
            if (!s.isEmpty()) {
                ItemStack result = s.split(amount);
                saveToStack();
                return result;
            }
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
            items.replaceAll(i -> ItemStack.EMPTY);
            saveToStack();
        }
    }
}
