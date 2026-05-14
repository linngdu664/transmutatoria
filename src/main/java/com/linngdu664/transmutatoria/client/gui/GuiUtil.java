package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.client.renderer.state.gui.FloatBlitRenderState;
import com.linngdu664.transmutatoria.client.renderer.state.gui.FloatColoredQuadRenderState;
import com.linngdu664.transmutatoria.client.renderer.state.gui.FloatColoredRectangleRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2f;

public class GuiUtil {
    public static int heightFrameCenter(Window window, int height) {
        return heightFrameRatio(window, height, 0.5);
    }

    public static int heightFrameRatio(Window window, int height, double heightRatio) {
        return (int) ((window.getHeight() / window.getGuiScale() - height) * heightRatio);
    }

    public static int heightWinRatio(Window window, double heightRatio) {
        return heightFrameRatio(window, 0, heightRatio);
    }

    public static int widthFrameCenter(Window window, int width) {
        return widthFrameRatio(window, width, 0.5);
    }

    public static int widthFrameRatio(Window window, int width, double widthRatio) {
        return (int) ((window.getWidth() / window.getGuiScale() - width) * widthRatio);
    }

    public static int widthWinRatio(Window window, double widthRatio) {
        return widthFrameRatio(window, 0, widthRatio);
    }

    public static V2I v2IRatio(Window window, int width, int height, double widthRatio, double heightRatio) {
        return v2IRatio(window, width, height, widthRatio, heightRatio, 0, 0);
    }

    public static V2I v2IRatio(Window window, int width, int height, double widthRatio, double heightRatio, int xOffset, int yOffset) {
        return new V2I(widthFrameRatio(window, width, widthRatio) + xOffset, heightFrameRatio(window, height, heightRatio) + yOffset);
    }

    public static V2I v2IRatio(Window window, double widthRatio, double heightRatio) {
        return new V2I((int) (window.getWidth() * widthRatio / window.getGuiScale()), (int) (window.getHeight() * heightRatio / window.getGuiScale()));
    }

    public static boolean isInScreen(Vec2 point, Window window) {
        return point.x > 0 && point.y > 0 && point.x < window.getGuiScaledWidth() && point.y < window.getGuiScaledHeight();
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int pColor) {
        fill(guiGraphics, a.x, a.y, b.x, b.y, pColor);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, float minX, float minY, float maxX, float maxY, int color) {
        fill(guiGraphics, RenderPipelines.GUI, minX, minY, maxX, maxY, color);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, float minX, float minY, float maxX, float maxY, int color) {
        if (minX < maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        if (minY < maxY) {
            float tmp = minY;
            minY = maxY;
            maxY = tmp;
        }
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredRectangleRenderState(renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), minX, minY, maxX, maxY, color, color, guiGraphics.scissorStack.peek()));
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int pColor) {
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredQuadRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), a, b, c, d, pColor));
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, Identifier texture, float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        blit(guiGraphics, RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier texture, float x, float y, float u, float v, float width, float height, float srcWidth, float srcHeight, float textureWidth, float textureHeight) {
        blit(guiGraphics, renderPipeline, texture, x, y, u, v, width, height, srcWidth, srcHeight, textureWidth, textureHeight, -1);
    }

    public static void blit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier texture, float x, float y, float u, float v, float width, float height, float srcWidth, float srcHeight, float textureWidth, float textureHeight, int color) {
        innerBlit(guiGraphics, renderPipeline, texture, x, x + width, y, y + height, u / textureWidth, (u + srcWidth) / textureWidth, v / textureHeight, (v + srcHeight) / textureHeight, color);
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color) {
        AbstractTexture texture = guiGraphics.minecraft.getTextureManager().getTexture(location);
        innerBlit(guiGraphics, renderPipeline, texture.getTextureView(), texture.getSampler(), x0, y0, x1, y1, u0, u1, v0, v1, color);
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
        guiGraphics.guiRenderState.addGuiElement(new FloatBlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, u0, u1, v0, v1, color, guiGraphics.scissorStack.peek()));
    }
}
