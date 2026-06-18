package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.client.renderer.state.gui.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection;
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2f;

/**
 * Gui floating coords render tools
 * Note: Text and item floating coords should use pose translate and methods in GuiGraphicsExtractor
 */
public class FloatGuiGraphics {
    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int pColor) {
        fill(guiGraphics, a.x, a.y, b.x, b.y, pColor);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, float x0, float y0, float x1, float y1, int color) {
        fill(guiGraphics, RenderPipelines.GUI, x0, y0, x1, y1, color);
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, float x0, float y0, float x1, float y1, int color) {
        if (x0 < x1) {
            float tmp = x0;
            x0 = x1;
            x1 = tmp;
        }
        if (y0 < y1) {
            float tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredRectangleRenderState(renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, color, color, guiGraphics.scissorStack.peek()));
    }

    public static void fill(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int pColor) {
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredQuadRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), a, b, c, d, pColor, guiGraphics.scissorStack.peek()));
    }

    public static void fillGradient(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, int colorTop, int colorBottom) {
        fillGradient(guiGraphics, a.x, a.y, b.x, b.y, colorTop, colorBottom);
    }

    public static void fillGradient(GuiGraphicsExtractor guiGraphics, float x0, float y0, float x1, float y1, int colorTop, int colorBottom) {
        fillGradient(guiGraphics, RenderPipelines.GUI, x0, y0, x1, y1, colorTop, colorBottom);
    }

    public static void fillGradient(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, float x0, float y0, float x1, float y1, int colorTop, int colorBottom) {
        if (x0 < x1) {
            float tmp = x0;
            x0 = x1;
            x1 = tmp;
        }
        if (y0 < y1) {
            float tmp = y0;
            y0 = y1;
            y1 = tmp;
        }
        guiGraphics.guiRenderState.addGuiElement(new FloatColoredRectangleRenderState(renderPipeline, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, colorTop, colorBottom, guiGraphics.scissorStack.peek()));
    }

    public static void fillGradient(GuiGraphicsExtractor guiGraphics, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int colorA, int colorB, int colorC, int colorD) {
        guiGraphics.guiRenderState.addGuiElement(new FloatGradientQuadRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(guiGraphics.pose()), a, b, c, d, colorA, colorB, colorC, colorD, guiGraphics.scissorStack.peek()));
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

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x, float y, float width, float height) {
        blitSprite(guiGraphics, renderPipeline, location, x, y, width, height, -1);
    }

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x, float y, float width, float height, float alpha) {
        blitSprite(guiGraphics, renderPipeline, location, x, y, width, height, ARGB.white(alpha));
    }

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x, float y, float width, float height, int color) {
        TextureAtlasSprite sprite = guiGraphics.guiSprites.getSprite(location);
        GuiSpriteScaling scaling = getSpriteScaling(sprite);
        switch (scaling) {
            case GuiSpriteScaling.Stretch stretch ->
                    blitSprite(guiGraphics, renderPipeline, sprite, x, y, width, height, color);
            case GuiSpriteScaling.Tile tile ->
                    blitTiledSprite(guiGraphics, renderPipeline, sprite, x, y, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height(), color);
            case GuiSpriteScaling.NineSlice nineSlice ->
                    blitNineSlicedSprite(guiGraphics, renderPipeline, sprite, nineSlice, x, y, width, height, color);
            default -> {}
        }
    }

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float spriteWidth, float spriteHeight, float textureX, float textureY, float x, float y, float width, float height) {
        blitSprite(guiGraphics, renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, -1);
    }

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float spriteWidth, float spriteHeight, float textureX, float textureY, float x, float y, float width, float height, int color) {
        TextureAtlasSprite sprite = guiGraphics.guiSprites.getSprite(location);
        GuiSpriteScaling scaling = getSpriteScaling(sprite);
        if (scaling instanceof GuiSpriteScaling.Stretch) {
            blitSprite(guiGraphics, renderPipeline, sprite, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, color);
        } else {
            guiGraphics.enableScissor(Mth.floor(x), Mth.floor(y), Mth.ceil(x + width), Mth.ceil(y + height));
            blitSprite(guiGraphics, renderPipeline, location, x - textureX, y - textureY, spriteWidth, spriteHeight, color);
            guiGraphics.disableScissor();
        }
    }

    public static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, TextureAtlasSprite sprite, float x, float y, float width, float height, int color) {
        if (width != 0 && height != 0) {
            innerBlit(guiGraphics, renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), color);
        }
    }

    private static void blitSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, TextureAtlasSprite sprite, float spriteWidth, float spriteHeight, float textureX, float textureY, float x, float y, float width, float height, int color) {
        if (width != 0 && height != 0) {
            innerBlit(guiGraphics, renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU(textureX / spriteWidth), sprite.getU((textureX + width) / spriteWidth), sprite.getV(textureY / spriteHeight), sprite.getV((textureY + height) / spriteHeight), color);
        }
    }

    private static void blitNineSlicedSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, TextureAtlasSprite sprite, GuiSpriteScaling.NineSlice nineSlice, float x, float y, float width, float height, int color) {
        GuiSpriteScaling.NineSlice.Border border = nineSlice.border();
        float borderLeft = Math.min(border.left(), width * 0.5f);
        float borderRight = Math.min(border.right(), width * 0.5f);
        float borderTop = Math.min(border.top(), height * 0.5f);
        float borderBottom = Math.min(border.bottom(), height * 0.5f);
        if (width == nineSlice.width() && height == nineSlice.height()) {
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height, color);
        } else if (height == nineSlice.height()) {
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, height, color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x + borderLeft, y, width - borderRight - borderLeft, height, borderLeft, 0, nineSlice.width() - borderRight - borderLeft, nineSlice.height(), nineSlice.width(), nineSlice.height(), color);
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, 0, x + width - borderRight, y, borderRight, height, color);
        } else if (width == nineSlice.width()) {
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, borderTop, color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x, y + borderTop, width, height - borderBottom - borderTop, 0, borderTop, nineSlice.width(), nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - borderBottom, x, y + height - borderBottom, width, borderBottom, color);
        } else {
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, borderLeft, borderTop, color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x + borderLeft, y, width - borderRight - borderLeft, borderTop, borderLeft, 0, nineSlice.width() - borderRight - borderLeft, borderTop, nineSlice.width(), nineSlice.height(), color);
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, 0, x + width - borderRight, y, borderRight, borderTop, color);
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - borderBottom, x, y + height - borderBottom, borderLeft, borderBottom, color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x + borderLeft, y + height - borderBottom, width - borderRight - borderLeft, borderBottom, borderLeft, nineSlice.height() - borderBottom, nineSlice.width() - borderRight - borderLeft, borderBottom, nineSlice.width(), nineSlice.height(), color);
            blitSprite(guiGraphics, renderPipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - borderRight, nineSlice.height() - borderBottom, x + width - borderRight, y + height - borderBottom, borderRight, borderBottom, color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x, y + borderTop, borderLeft, height - borderBottom - borderTop, 0, borderTop, borderLeft, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x + borderLeft, y + borderTop, width - borderRight - borderLeft, height - borderBottom - borderTop, borderLeft, borderTop, nineSlice.width() - borderRight - borderLeft, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
            blitNineSliceInnerSegment(guiGraphics, renderPipeline, nineSlice, sprite, x + width - borderRight, y + borderTop, borderRight, height - borderBottom - borderTop, nineSlice.width() - borderRight, borderTop, borderRight, nineSlice.height() - borderBottom - borderTop, nineSlice.width(), nineSlice.height(), color);
        }
    }

    private static void blitNineSliceInnerSegment(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, GuiSpriteScaling.NineSlice nineSlice, TextureAtlasSprite sprite, float x, float y, float width, float height, float textureX, float textureY, float textureWidth, float textureHeight, float spriteWidth, float spriteHeight, int color) {
        if (width > 0 && height > 0) {
            if (nineSlice.stretchInner()) {
                innerBlit(guiGraphics, renderPipeline, sprite.atlasLocation(), x, x + width, y, y + height, sprite.getU(textureX / spriteWidth), sprite.getU((textureX + textureWidth) / spriteWidth), sprite.getV(textureY / spriteHeight), sprite.getV((textureY + textureHeight) / spriteHeight), color);
            } else {
                blitTiledSprite(guiGraphics, renderPipeline, sprite, x, y, width, height, textureX, textureY, textureWidth, textureHeight, spriteWidth, spriteHeight, color);
            }
        }
    }

    private static void blitTiledSprite(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, TextureAtlasSprite sprite, float x, float y, float width, float height, float textureX, float textureY, float tileWidth, float tileHeight, float spriteWidth, float spriteHeight, int color) {
        if (width > 0 && height > 0) {
            if (tileWidth <= 0 || tileHeight <= 0) {
                throw new IllegalArgumentException("Tile size must be positive, got " + tileWidth + "x" + tileHeight);
            }
            AbstractTexture spriteTexture = guiGraphics.minecraft.getTextureManager().getTexture(sprite.atlasLocation());
            GpuTextureView texture = spriteTexture.getTextureView();
            innerTiledBlit(guiGraphics, renderPipeline, texture, spriteTexture.getSampler(), tileWidth, tileHeight, x, y, x + width, y + height, sprite.getU(textureX / spriteWidth), sprite.getU((textureX + tileWidth) / spriteWidth), sprite.getV(textureY / spriteHeight), sprite.getV((textureY + tileHeight) / spriteHeight), color);
        }
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline renderPipeline, Identifier location, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1, int color) {
        AbstractTexture texture = guiGraphics.minecraft.getTextureManager().getTexture(location);
        innerBlit(guiGraphics, renderPipeline, texture.getTextureView(), texture.getSampler(), x0, y0, x1, y1, u0, u1, v0, v1, color);
    }

    private static void innerBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
        guiGraphics.guiRenderState.addGuiElement(new FloatBlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(guiGraphics.pose()), x0, y0, x1, y1, u0, u1, v0, v1, color, guiGraphics.scissorStack.peek()));
    }

    private static void innerTiledBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, float tileWidth, float tileHeight, float x0, float y0, float x1, float y1, float u0, float u1, float v0, float v1, int color) {
        guiGraphics.guiRenderState.addGuiElement(new FloatTiledBlitRenderState(pipeline, TextureSetup.singleTexture(textureView, sampler), new Matrix3x2f(guiGraphics.pose()), tileWidth, tileHeight, x0, y0, x1, y1, u0, u1, v0, v1, color, guiGraphics.scissorStack.peek()));
    }

    private static GuiSpriteScaling getSpriteScaling(TextureAtlasSprite sprite) {
        return (sprite.contents().getAdditionalMetadata(GuiMetadataSection.TYPE).orElse(GuiMetadataSection.DEFAULT)).scaling();
    }
}
