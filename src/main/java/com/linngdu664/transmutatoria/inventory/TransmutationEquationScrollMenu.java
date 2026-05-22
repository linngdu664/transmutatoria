package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class TransmutationEquationScrollMenu extends AbstractTransmutationScrollMenu {
    public TransmutationEquationScrollMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, findScrollInHands(playerInventory.player));
    }

    public TransmutationEquationScrollMenu(int containerId, Inventory playerInventory, @NonNull ItemStack scrollStack) {
        super(InitMenuTypes.TRANSMUTATION_EQUATION_SCROLL_MENU.get(), containerId, playerInventory, scrollStack);
    }

    @Override
    protected int addScrollInventory(Container container) {
        addSlot(new AbstractTransmutationScrollMenu.InputSlot(container, 0, SLOT0_X, SLOT0_Y));
        addSlot(new AbstractTransmutationScrollMenu.OtherSideSlot(container, 1, SLOT1_X, SLOT1_Y));
        return 0;
    }
}
