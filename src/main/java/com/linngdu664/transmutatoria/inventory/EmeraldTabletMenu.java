package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class EmeraldTabletMenu extends AbstractContainerMenu {
    private final ItemStack tabletStack;

    public EmeraldTabletMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ItemStack.EMPTY);
    }

    public EmeraldTabletMenu(int containerId, Inventory playerInventory, ItemStack tabletStack) {
        super(InitMenuTypes.EMERALD_TABLET_MENU.get(), containerId);
        this.tabletStack = tabletStack;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (tabletStack.isEmpty()) {
            return true;
        }
        return tabletStack.is(InitItems.EMERALD_TABLET.get());
    }
}
