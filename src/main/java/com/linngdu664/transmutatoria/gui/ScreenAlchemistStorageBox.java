package com.linngdu664.transmutatoria.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class ScreenAlchemistStorageBox extends AbstractContainerScreen<MenuAlchemistStorageBox> {
    private static final Identifier INVENTORY_BG =
            Identifier.withDefaultNamespace("textures/gui/container/crafter.png");

    public ScreenAlchemistStorageBox(MenuAlchemistStorageBox menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 220);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

//        int x = leftPos;
//        int y = topPos;

//        // Slot backgrounds for the 12 clock-position container slots
//        for (int i = 0; i < 12; i++) {
//            double angle = Math.toRadians(i * 30.0 - 90.0);
//            int sx = (int) (x + 88 + 50 * Mth.cos(angle)) - 9;
//            int sy = (int) (y + 85 + 50 * Mth.sin(angle)) - 9;
//            graphics.blit(INVENTORY_BG, sx, sx + 18, sy, sy + 18, 7.0F, 25.0F, 83.0F, 101.0F);
//        }
//
//        // Player inventory + hotbar background
//        // generic_54.png: player inventory section starts at UV y=125, hotbar ends at UV y=222
//        graphics.blit(INVENTORY_BG, x, x + 176, y + 130, y + 222, 0.0F, 176.0F, 125.0F, 222.0F);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_BG, xo, yo+54, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
