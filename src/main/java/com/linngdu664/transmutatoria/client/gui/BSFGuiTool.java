package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.util.V2I;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.phys.Vec2;

public class BSFGuiTool {
    /**
     * 渲染进度条
     * @param guiGraphics GuiGraphicsExtractor
     * @param pos         进度条位置(取点左上角)
     * @param frame       进度条长宽
     * @param padding     内外框间隔
     * @param frameColor  外框颜色
     * @param innerColor  内框颜色
     * @param percent     进度条进度(0-1)
     */
    public static void renderProgressBar(GuiGraphicsExtractor guiGraphics, V2I pos, V2I frame, int padding, int frameColor, int innerColor, float percent) {
        guiGraphics.fill(pos.x() + 1, pos.y() + 1, pos.x() + frame.x() - 1, pos.y() + frame.y() - 1, 0x80000000);
        guiGraphics.outline(pos.x(), pos.y(), frame.x(), frame.y(), frameColor);
        int innerW = (int) ((frame.x() - padding - padding) * percent);
        guiGraphics.fill(pos.x() + padding, pos.y() + padding, pos.x() + padding + innerW, pos.y() + frame.y() - padding, innerColor);
    }

    public static void renderAdvancedLine(GuiGraphicsExtractor guiGraphics, Vec2 p1, Vec2 p2, float d, int color, boolean isDown, float padding, int padColor) {
        Vec2 ad = p2.add(p1.negated());
        Vec2 v1 = ad.scale(d / ad.length());
        Vec2 v2 = new Vec2(-v1.y, v1.x);
        if (isDown) {
            Vec2 v2s = v2.scale(padding);
            GuiUtil.fill(guiGraphics, p1, p1.add(v2), p2.add(v2), p2, padColor);
            GuiUtil.fill(guiGraphics, p1, p1.add(v2s), p2.add(v2s), p2, color);
        } else {
            v2 = v2.negated();
            p2 = p2.add(v2.negated());
            Vec2 v2s = v2.scale(1 - padding);
            GuiUtil.fill(guiGraphics, p1.add(v2), p1, p2, p2.add(v2), color);
            GuiUtil.fill(guiGraphics, p1.add(v2s), p1, p2, p2.add(v2s), padColor);
        }
    }

    public static void renderFilledRectangle(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int pColor) {
        GuiUtil.fill(guiGraphics, a.x, a.y, b.x, b.y, pColor);
    }

    /*
    public static void renderOutline(GuiGraphicsExtractor guiGraphics, float x, float y, float width, float height, int color) {
        GuiUtil.fill(guiGraphics, x, y, x + width, y + 1, color);
        GuiUtil.fill(guiGraphics, x, y + height - 1, x + width, y + height, color);
        GuiUtil.fill(guiGraphics, x, y + 1, x + 1, y + height - 1, color);
        GuiUtil.fill(guiGraphics, x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public static void renderOutlineCoordinate(GuiGraphicsExtractor guiGraphics, float x, float y, float x2, float y2, int color) {
        GuiUtil.fill(guiGraphics, x, y, x2, y + 1, color);
        GuiUtil.fill(guiGraphics, x, y2 - 1, x2, y2, color);
        GuiUtil.fill(guiGraphics, x, y + 1, x + 1, y2 - 1, color);
        GuiUtil.fill(guiGraphics, x2 - 1, y + 1, x2, y2 - 1, color);
    }

    public static void renderOutlineCoordinate(GuiGraphicsExtractor guiGraphics, float x, float y, float x2, float y2, int color, float width) {
        GuiUtil.fill(guiGraphics, x, y, x2, y + width, color);
        GuiUtil.fill(guiGraphics, x, y2 - width, x2, y2, color);
        GuiUtil.fill(guiGraphics, x, y + width, x + width, y2 - width, color);
        GuiUtil.fill(guiGraphics, x2 - width, y + width, x2, y2 - width, color);
    }*/
}
