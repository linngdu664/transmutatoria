package com.linngdu664.transmutatoria.inventory;

import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class TransmutationSigilScrollMenu extends AbstractTransmutationScrollMenu {
    public TransmutationSigilScrollMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, findScrollInHands(playerInventory.player));
    }

    public TransmutationSigilScrollMenu(int containerId, Inventory playerInventory, @NonNull ItemStack scrollStack) {
        super(InitMenuTypes.TRANSMUTATION_SIGIL_SCROLL_MENU.get(), containerId, playerInventory, scrollStack);
    }

    @Override
    protected int addScrollInventory(Container container) {
        addSlot(new AbstractTransmutationScrollMenu.OtherSideSlot(container, 0, SLOT0_X, SLOT0_Y));
        addSlot(new AbstractTransmutationScrollMenu.InputSlot(container, 1, SLOT1_X, SLOT1_Y));
        return 1;
    }
}
