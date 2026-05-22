package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class ScreenAlchemistStorageBox extends AbstractContainerScreen<AlchemistStorageBoxMenu> {
    private static final Identifier INVENTORY_BG = ArsTransmutatoria.makeMyIdentifier("textures/gui/alchemist_storage_box.png");
    private static final Identifier HEXAGON_SLOT_HIGHLIGHT_BACK_SPRITE = ArsTransmutatoria.makeMyIdentifier("container/hexagon_slot_highlight_back");
    private static final Identifier HEXAGON_SLOT_HIGHLIGHT_FRONT_SPRITE = ArsTransmutatoria.makeMyIdentifier("container/hexagon_slot_highlight_front");

    public ScreenAlchemistStorageBox(AlchemistStorageBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 220);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 38;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_BG, xo, yo + 32, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractSlotHighlightBack(GuiGraphicsExtractor graphics) {
        if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
            if (this.hoveredSlot instanceof AlchemistStorageBoxMenu.LockedEssenceMetalSlot) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEXAGON_SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            } else {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            }
        }
    }

    @Override
    public void extractSlotHighlightFront(GuiGraphicsExtractor graphics) {
        if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
            if (this.hoveredSlot instanceof AlchemistStorageBoxMenu.LockedEssenceMetalSlot) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, HEXAGON_SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            } else {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
            }
        }
    }
}
