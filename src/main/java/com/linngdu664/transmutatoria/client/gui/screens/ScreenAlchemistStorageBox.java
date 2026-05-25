package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.util.GuiSprite;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Inventory;

public class ScreenAlchemistStorageBox extends AbstractContainerScreen<AlchemistStorageBoxMenu> {
    private static final Identifier INVENTORY_BG = ArsTransmutatoria.makeMyIdentifier("textures/gui/alchemist_storage_box.png");
    private static final Identifier INVENTORY_BG_NIGREDO = ArsTransmutatoria.makeMyIdentifier("textures/gui/nigredo_alchemist_storage_box.png");
    private static final Identifier INVENTORY_BG_ALBEDO = ArsTransmutatoria.makeMyIdentifier("textures/gui/albedo_alchemist_storage_box.png");
    private static final Identifier INVENTORY_BG_CITRINITAS = ArsTransmutatoria.makeMyIdentifier("textures/gui/citrinitas_alchemist_storage_box.png");
    private static final Identifier HEXAGON_SLOT_HIGHLIGHT_BACK_SPRITE = ArsTransmutatoria.makeMyIdentifier("container/hexagon_slot_highlight_back");
    private static final Identifier HEXAGON_SLOT_HIGHLIGHT_FRONT_SPRITE = ArsTransmutatoria.makeMyIdentifier("container/hexagon_slot_highlight_front");
    private static final GuiSprite TEST_SPRITE = new GuiSprite("container/test", 16, 16);
//    private static final GuiSubSprite TEST = new GuiSubSprite(TEST_SPRITE, 0, 0, 16, 16);

    private final int boxState;

    public ScreenAlchemistStorageBox(AlchemistStorageBoxMenu menu, Inventory playerInventory, Component title) {
        this.boxState = menu.boxState;
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
        Identifier backGround = switch (boxState) {
            case -1 -> INVENTORY_BG_NIGREDO;
            case 1 -> INVENTORY_BG_ALBEDO;
            case 2 -> INVENTORY_BG_CITRINITAS;
            default -> INVENTORY_BG;
        };
        graphics.blit(RenderPipelines.GUI_TEXTURED, backGround, xo, yo + 32, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        TEST_SPRITE.render(graphics, TextureOption.DEFAULT, 100, 100);
    }
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        int color = switch (boxState){
            case -1 -> ARGB.color(200,200,200);
            case 1 -> ARGB.color(230,230,230);
            case 2 -> ARGB.color(240,240,240);
            default -> ARGB.color(220,220,220);
        };
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, color, true);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, color, true);
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
