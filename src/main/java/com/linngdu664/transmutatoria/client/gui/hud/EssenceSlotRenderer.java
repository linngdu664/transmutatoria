package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.PosUtil;
import com.linngdu664.transmutatoria.client.gui.ScreenPos;
import com.linngdu664.transmutatoria.client.gui.animation.CrucibleSlotAnimation;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothPoint;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.tool.Easing;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class EssenceSlotRenderer {
    private static final TextureOption VIRTUAL_ITEM = TextureOption.withAlpha(48);
    private static final float SLOT_CLICK_PRESS_TICKS = 3.0f;
    private static final float SLOT_CLICK_RELEASE_TICKS = 7.0f;
    private static final float SLOT_CLICK_MIN_SCALE = 0.78f;

    private EssenceSlotRenderer() {
    }

    // --- position calculation ---

    static long[] calcPosEssenceMetal(Window window, int size) {
        long[] xys = new long[size];
        int x = PosUtil.widthCenter(window, Textures.NORMAL_SLOT.width()) - 10 * (size - 1);
        int y = PosUtil.heightCenter(window, Textures.NORMAL_SLOT.height()) - 6;
        for (int i = 0; i < size; i++) {
            xys[i] = ScreenPos.pack(x, y);
            x += 20;
            y += ((i & 1) == 0) ? 12 : -12;
        }
        return xys;
    }

    static long[] calcPosCrystal(Window window) {
        int x = PosUtil.widthCenter(window, Textures.NORMAL_SLOT.width());
        int y = PosUtil.heightCenter(window, Textures.NORMAL_SLOT.height());
        return new long[]{ScreenPos.pack(x, y - 12), ScreenPos.pack(x, y + 12)};
    }

    static long[] calcPosPhilosophersStone(Window window) {
        long[] xys = new long[24];
        V2I origin = PosUtil.v2IRatio(window, Textures.NORMAL_SLOT.width(), Textures.NORMAL_SLOT.height(), 0.5f, 0.5f);
        int index = appendHollowTriangle(xys, 0, 1, 1, 3, false, origin);
        appendHollowTriangle(xys, index, 0, 0, 6, true, origin);
        return xys;
    }

    private static int appendHollowTriangle(long[] xys, int index, int originQ, int originR, int edgeSteps, boolean removeVertices, V2I screenOrigin) {
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                xys[index++] = packPhilosophersStoneSlot(screenOrigin, originQ + step, originR);
            }
        }
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                xys[index++] = packPhilosophersStoneSlot(screenOrigin, originQ + edgeSteps - step, originR + step);
            }
        }
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                xys[index++] = packPhilosophersStoneSlot(screenOrigin, originQ, originR + edgeSteps - step);
            }
        }
        return index;
    }

    private static long packPhilosophersStoneSlot(V2I screenOrigin, int q, int r) {
        int x = screenOrigin.x() + q * 20 - 40;
        int y = screenOrigin.y() + (2 * r + q) * 12 - 96;
        return ScreenPos.pack(x, y);
    }

    static long[] calcPosScroll(Window window, List<AbstractAlchemySlot> alchemySlots) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (AbstractAlchemySlot slot : alchemySlots) {
            minX = Math.min(slot.getX(), minX);
            maxX = Math.max(slot.getX(), maxX);
            minY = Math.min(slot.getY(), minY);
            maxY = Math.max(slot.getY(), maxY);
        }
        V2I origin = PosUtil.v2IRatio(window, Textures.NORMAL_SLOT.width(), Textures.NORMAL_SLOT.height(), 0.5f, 0.5f);
        int initX = origin.x() - 10 * (maxX - minX);
        int initY = origin.y() - 6 * (maxY - minY);

        long[] xys = new long[alchemySlots.size()];
        int i = 0;
        for (AbstractAlchemySlot alchemySlot : alchemySlots) {
            int x = initX + 20 * (alchemySlot.getX() - minX);
            int y = initY + 12 * (alchemySlot.getY() - minY);
            xys[i++] = ScreenPos.pack(x, y);
        }
        return xys;
    }

    // --- rendering with items and selection ---

    static void drawSlotsWithItemsAndSelection(
            GuiGraphicsExtractor guiGraphics,
            long[] xys,
            TransmutationCrucibleBlockEntity crucible,
            DeltaTracker delta,
            Int2ObjectFunction<TextureRenderable> textureGetter,
            Int2ObjectFunction<Object> itemGetter,
            SmoothPoint selectedSlotHighlight,
            CrucibleSlotAnimation slotAnimation
    ) {
        int pulsedSlotIndex = getActiveEssenceInputPulseSlot(crucible, xys.length);
        for (int i = 0; i < xys.length; i++) {
            if (i == pulsedSlotIndex) {
                continue;
            }
            long packed = xys[i];
            drawScaledSlot(
                    guiGraphics,
                    ScreenPos.unpackX(packed),
                    ScreenPos.unpackY(packed),
                    getReactionSlotScale(crucible, delta, i, xys.length, slotAnimation),
                    textureGetter.get(i),
                    itemGetter.get(i)
            );
        }

        if (pulsedSlotIndex >= 0) {
            drawPulsedSlot(guiGraphics, xys, pulsedSlotIndex, crucible, delta, textureGetter, itemGetter, slotAnimation);
        }
        drawSelectedSlot(guiGraphics, xys, crucible, delta, selectedSlotHighlight, slotAnimation);
    }

    static void drawDisplayOnlySlots(
            GuiGraphicsExtractor guiGraphics,
            long[] xys,
            List<ItemStack> items,
            TransmutationCrucibleBlockEntity crucible,
            DeltaTracker delta,
            CrucibleSlotAnimation slotAnimation
    ) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            drawScaledSlot(
                    guiGraphics,
                    ScreenPos.unpackX(packed),
                    ScreenPos.unpackY(packed),
                    getReactionSlotScale(crucible, delta, i, xys.length, slotAnimation),
                    Textures.NORMAL_SLOT,
                    items.get(i)
            );
        }
    }

    // --- number overlay ---

    static void drawNumbers(GuiGraphicsExtractor guiGraphics, Font font, long[] xys, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta, CrucibleSlotAnimation slotAnimation) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            int slotX = ScreenPos.unpackX(packed);
            int slotY = ScreenPos.unpackY(packed);
            float scale = getReactionSlotScale(crucible, delta, i, xys.length, slotAnimation);
            if (scale <= 0.001f) {
                continue;
            }

            String str = String.valueOf(i + 1);
            if (Math.abs(scale - 1.0f) <= 0.001f) {
                guiGraphics.text(font, str, slotX + 14 - font.width(str) / 2, slotY + 10, 0xffffffff, true);
                continue;
            }

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(slotX + Textures.NORMAL_SLOT.width() * 0.5f - 0.5f, slotY + Textures.NORMAL_SLOT.height() * 0.5f - 0.5f);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.text(font, str, 1 - font.width(str) / 2, -3, 0xffffffff, true);
            guiGraphics.pose().popMatrix();
        }
    }

    // --- internal slot drawing ---

    private static void drawScaledSlot(GuiGraphicsExtractor guiGraphics, int x, int y, float scale, TextureRenderable texture, Object itemDraw) {
        if (scale <= 0.001f) {
            return;
        }

        if (Math.abs(scale - 1.0f) <= 0.001f) {
            drawSlot(guiGraphics, x, y, texture);
            drawItem(guiGraphics, x, y, itemDraw);
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.NORMAL_SLOT.width() * 0.5f - 0.5f, y + Textures.NORMAL_SLOT.height() * 0.5f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        drawSlot(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, texture);
        drawItem(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, itemDraw);
        guiGraphics.pose().popMatrix();
    }

    private static void drawPulsedSlot(
            GuiGraphicsExtractor guiGraphics,
            long[] xys,
            int pulsedSlotIndex,
            TransmutationCrucibleBlockEntity crucible,
            DeltaTracker delta,
            Int2ObjectFunction<TextureRenderable> textureGetter,
            Int2ObjectFunction<Object> itemGetter,
            CrucibleSlotAnimation slotAnimation
    ) {
        long packed = xys[pulsedSlotIndex];
        int x = ScreenPos.unpackX(packed);
        int y = ScreenPos.unpackY(packed);
        float scale = getSelectedSlotClickScale(crucible) * getReactionSlotScale(crucible, delta, pulsedSlotIndex, xys.length, slotAnimation);
        if (scale <= 0.001f) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.NORMAL_SLOT.width() * 0.5f - 0.5f, y + Textures.NORMAL_SLOT.height() * 0.5f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        drawSlot(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, textureGetter.get(pulsedSlotIndex));
        drawItem(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, itemGetter.get(pulsedSlotIndex));
        guiGraphics.pose().popMatrix();
    }

    private static void drawSlot(GuiGraphicsExtractor guiGraphics, int x, int y, TextureRenderable texture) {
        texture.render(guiGraphics, x, y);
    }

    private static void drawItem(GuiGraphicsExtractor guiGraphics, int x, int y, Object itemDraw) {
        if (itemDraw instanceof ItemStack itemStack) {
            guiGraphics.item(itemStack, x + 6, y + 5);
        } else if (itemDraw instanceof TextureRenderable texture) {
            texture.render(guiGraphics, VIRTUAL_ITEM, x + 6, y + 5);
        }
    }

    private static void drawSelectedSlot(GuiGraphicsExtractor guiGraphics, long[] xys, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta, SmoothPoint selectedSlotHighlight, CrucibleSlotAnimation slotAnimation) {
        int selectedSlotIndex = crucible.getSelectedSlot();
        if (selectedSlotIndex < 0 || selectedSlotIndex >= xys.length) {
            return;
        }

        long packed = xys[selectedSlotIndex];
        float targetX = ScreenPos.unpackX(packed) - 1;
        float targetY = ScreenPos.unpackY(packed) - 1;

        selectedSlotHighlight.moveTo(targetX, targetY, delta);

        int x = Math.round(selectedSlotHighlight.x());
        int y = Math.round(selectedSlotHighlight.y());
        float scale = getSelectedSlotClickScale(crucible) * getReactionSlotScale(crucible, delta, selectedSlotIndex, xys.length, slotAnimation);
        if (scale <= 0.001f) {
            return;
        }
        if (Math.abs(scale - 1.0f) <= 0.001f) {
            Textures.SLOT_SELECTED.render(guiGraphics, x, y);
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.SLOT_SELECTED.width() * 0.5f - 0.5f, y + Textures.SLOT_SELECTED.height() * 0.5f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        Textures.SLOT_SELECTED.render(guiGraphics, -Textures.SLOT_SELECTED.width() / 2, -Textures.SLOT_SELECTED.height() / 2);
        guiGraphics.pose().popMatrix();
    }

    // --- pulse animation helpers (package-visible for ArrowRenderer) ---

    static int getActiveEssenceInputPulseSlot(TransmutationCrucibleBlockEntity crucible, int slotCount) {
        int slot = crucible.getEssenceInputPulseSlot();
        if (slot < 0 || slot >= slotCount) {
            return -1;
        }

        float elapsedTicks = (System.currentTimeMillis() - crucible.getEssenceInputPulseStartedAtMillis()) / 50.0f;
        return elapsedTicks >= 0.0f && elapsedTicks < SLOT_CLICK_PRESS_TICKS + SLOT_CLICK_RELEASE_TICKS ? slot : -1;
    }

    static float getSelectedSlotClickScale(TransmutationCrucibleBlockEntity crucible) {
        if (crucible.getEssenceInputPulseSlot() < 0) {
            return 1.0f;
        }

        float elapsedTicks = (System.currentTimeMillis() - crucible.getEssenceInputPulseStartedAtMillis()) / 50.0f;
        if (elapsedTicks < 0.0f || elapsedTicks >= SLOT_CLICK_PRESS_TICKS + SLOT_CLICK_RELEASE_TICKS) {
            return 1.0f;
        }
        if (elapsedTicks <= SLOT_CLICK_PRESS_TICKS) {
            return Easing.CUBIC_OUT.ease(elapsedTicks, 1.0f, SLOT_CLICK_MIN_SCALE - 1.0f, SLOT_CLICK_PRESS_TICKS);
        }

        float releaseTick = elapsedTicks - SLOT_CLICK_PRESS_TICKS;
        return Easing.BACK_OUT.ease(releaseTick, SLOT_CLICK_MIN_SCALE, 1.0f - SLOT_CLICK_MIN_SCALE, SLOT_CLICK_RELEASE_TICKS);
    }

    // todo 未使用的参数 delta？
    static float getReactionSlotScale(TransmutationCrucibleBlockEntity crucible, DeltaTracker delta, int slotIndex, int slotCount, CrucibleSlotAnimation slotAnimation) {
        if (slotAnimation.isRunningFor(crucible) && slotCount > 0) {
            float progress = slotAnimation.processProgress();
            float slotProgress = Mth.clamp(progress * slotCount - slotIndex, 0.0f, 1.0f);
            return 1.0f - Easing.CUBIC_IN.ease(slotProgress, 0.0f, 1.0f, 1.0f);
        }

        return slotAnimation.revealScale();
    }
}
