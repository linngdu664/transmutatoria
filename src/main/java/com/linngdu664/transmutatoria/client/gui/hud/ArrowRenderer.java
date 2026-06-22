package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.ScreenPos;
import com.linngdu664.transmutatoria.client.gui.animation.CrucibleSlotAnimation;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.List;

final class ArrowRenderer {
    private ArrowRenderer() {
    }

    static void render(GuiGraphicsExtractor guiGraphics, long[] xys, List<AbstractAlchemySlot> alchemySlots, int magicNumber, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta, CrucibleSlotAnimation slotAnimation) {
        int pulsedSlotIndex = EssenceSlotRenderer.getActiveEssenceInputPulseSlot(crucible, Math.min(xys.length, alchemySlots.size()));
        for (int i = 0, size = Math.min(alchemySlots.size(), xys.length); i < size; i++) {
            if (i == pulsedSlotIndex) {
                continue;
            }
            float scale = EssenceSlotRenderer.getReactionSlotScale(crucible, delta, i, xys.length, slotAnimation);
            if (scale > 0.001f) {
                drawScaledArrow(guiGraphics, ScreenPos.unpackX(xys[i]), ScreenPos.unpackY(xys[i]), scale, alchemySlots.get(i), magicNumber, i);
            }
        }

        if (pulsedSlotIndex < 0) {
            return;
        }

        long packed = xys[pulsedSlotIndex];
        int x = ScreenPos.unpackX(packed);
        int y = ScreenPos.unpackY(packed);
        float scale = EssenceSlotRenderer.getSelectedSlotClickScale(crucible) * EssenceSlotRenderer.getReactionSlotScale(crucible, delta, pulsedSlotIndex, xys.length, slotAnimation);
        if (scale <= 0.001f) {
            return;
        }
        drawScaledArrow(guiGraphics, x, y, scale, alchemySlots.get(pulsedSlotIndex), magicNumber, pulsedSlotIndex);
    }

    private static void drawScaledArrow(GuiGraphicsExtractor guiGraphics, int x, int y, float scale, AbstractAlchemySlot alchemySlot, int magicNumber, int slotIndex) {
        if (Math.abs(scale - 1.0f) <= 0.001f) {
            drawArrow(guiGraphics, x, y, alchemySlot, magicNumber, slotIndex);
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.NORMAL_SLOT.width() * 0.5f - 0.5f, y + Textures.NORMAL_SLOT.height() * 0.5f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        drawArrow(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, alchemySlot, magicNumber, slotIndex);
        guiGraphics.pose().popMatrix();
    }

    private static void drawArrow(GuiGraphicsExtractor guiGraphics, int x, int y, AbstractAlchemySlot alchemySlot, int magicNumber, int slotIndex) {
        switch (alchemySlot.getShowDirection(AbstractAlchemySlot.getSlotMagicNumber(magicNumber, slotIndex))) {
            case 0 -> Textures.UP_ARROW.render(guiGraphics, x + 11, y - 2);
            case 1 -> Textures.UPRIGHT_ARROW.render(guiGraphics, x + 21, y + 5);
            case 2 -> Textures.DOWNRIGHT_ARROW.render(guiGraphics, x + 21, y + 17);
            case 3 -> Textures.DOWN_ARROW.render(guiGraphics, x + 11, y + 24);
            case 4 -> Textures.DOWNLEFT_ARROW.render(guiGraphics, x + 1, y + 17);
            case 5 -> Textures.UPLEFT_ARROW.render(guiGraphics, x + 1, y + 5);
        }
    }
}
