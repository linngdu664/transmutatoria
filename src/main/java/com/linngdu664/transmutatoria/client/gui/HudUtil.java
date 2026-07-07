package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.renderer.state.gui.FloatGradientQuadRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2f;
import org.joml.Vector2i;

public class HudUtil {
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
    public static void renderProgressBar(GuiGraphicsExtractor guiGraphics, Vector2i pos, Vector2i frame, int padding, int frameColor, int innerColor, float percent) {
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
            FloatGuiGraphics.fill(guiGraphics, p1, p1.add(v2), p2.add(v2), p2, padColor);
            FloatGuiGraphics.fill(guiGraphics, p1, p1.add(v2s), p2.add(v2s), p2, color);
        } else {
            v2 = v2.negated();
            p2 = p2.add(v2.negated());
            Vec2 v2s = v2.scale(1 - padding);
            FloatGuiGraphics.fill(guiGraphics, p1.add(v2), p1, p2, p2.add(v2), color);
            FloatGuiGraphics.fill(guiGraphics, p1.add(v2s), p1, p2, p2.add(v2s), padColor);
        }
    }

    public static void renderFilledRectangle(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int pColor) {
        FloatGuiGraphics.fill(guiGraphics, a.x, a.y, b.x, b.y, pColor);
    }

    public static void renderGradientLine(GuiGraphicsExtractor guiGraphics, Vec2 start, Vec2 end, float thickness, int startColor, int endColor) {
        renderGradientLine(guiGraphics, RenderPipelines.GUI, start, end, thickness, startColor, endColor);
    }

    public static void renderGradientLine(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Vec2 start, Vec2 end, float thickness, int startColor, int endColor) {
        if (thickness <= 0.0F) {
            return;
        }

        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length == 0.0F) {
            return;
        }

        float halfThickness = thickness * 0.5F;
        float normalX = -dy / length * halfThickness;
        float normalY = dx / length * halfThickness;
        Vec2 a = new Vec2(start.x + normalX, start.y + normalY);
        Vec2 b = new Vec2(start.x - normalX, start.y - normalY);
        Vec2 c = new Vec2(end.x - normalX, end.y - normalY);
        Vec2 d = new Vec2(end.x + normalX, end.y + normalY);
        guiGraphics.guiRenderState.addGuiElement(new FloatGradientQuadRenderState(renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), a, b, c, d, startColor, startColor, endColor, endColor, guiGraphics.scissorStack.peek()));
    }

    public static void renderBottomCropped(GuiGraphicsExtractor guiGraphics, TextureRenderable texture, float x, float y, float visibleHeight) {
        float height = Mth.clamp(visibleHeight, 0.0f, texture.height());
        if (height <= 0.001f) return;
        float srcY = texture.height() - height;
        float destY = y + texture.height() - height;
        texture.render(guiGraphics, TextureOption.DEFAULT, x, destY, 0, srcY, texture.width(), height);
    }

    public static void renderTopCropped(GuiGraphicsExtractor guiGraphics, TextureRenderable texture, float x, float y, float visibleHeight) {
        float height = Mth.clamp(visibleHeight, 0.0f, texture.height());
        if (height <= 0.001f) return;
        texture.render(guiGraphics, TextureOption.DEFAULT, x, y, 0, 0, texture.width(), height);
    }

    public static void renderVerticalSlice(GuiGraphicsExtractor guiGraphics, TextureRenderable texture, float x, float y, float yOffset, float visibleHeight) {
        float top = Mth.clamp(yOffset, 0.0f, texture.height());
        float height = Mth.clamp(visibleHeight, 0.0f, texture.height() - top);
        if (height <= 0.001f) return;
        texture.render(guiGraphics, TextureOption.DEFAULT, x, y + top, 0, top, texture.width(), height);
    }

    public static void renderRotatedCentered(GuiGraphicsExtractor guiGraphics, TextureRenderable texture, float centerX, float centerY, float angleDegrees) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(centerX, centerY);
        guiGraphics.pose().rotate(angleDegrees * Mth.DEG_TO_RAD);
        texture.render(guiGraphics, -texture.width() * 0.5f, -texture.height() * 0.5f);
        guiGraphics.pose().popMatrix();
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
