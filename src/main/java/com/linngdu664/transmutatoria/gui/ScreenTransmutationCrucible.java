package com.linngdu664.transmutatoria.gui;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenTransmutationCrucible extends AbstractContainerScreen<MenuTransmutationCrucible> {

    public ScreenTransmutationCrucible(MenuTransmutationCrucible menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }
}
