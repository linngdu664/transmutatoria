package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.util.GuiSprite;
import com.linngdu664.transmutatoria.client.gui.util.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

/**
 * Gui window utils and floating coords render tools
 * Note: Text and item floating coords should use pose translate and methods in GuiGraphicsExtractor
 */
public class BSFGuiTool {

//    public static final GuiSubSprite SIMPLE_FRAME_IMG = new GuiSubSprite(SIMPLE_FRAME, 0, 0, 22, 22);

    /**
     * 渲染进度条
     *
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

    public static void renderLineTool(GuiGraphicsExtractor guiGraphics, Vec2 p1, Vec2 p2, float d, int color, boolean isDown, float padding, int padColor) {
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
    }


//
//    public record GuiTexture(Identifier texture, int wholeWidth, int wholeHeight) {
//        public GuiTexture(String path, int wholeWidth, int wholeHeight) {
//            this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight);
//        }
//    }
//
//    public static class GuiImage {
//        public GuiTexture guiTexture;
//        public int widthOffset;
//        public int heightOffset;
//        public int width;
//        public int height;
//
//        public GuiImage(GuiTexture texture, int widthOffset, int heightOffset, int width, int height) {
//            this.guiTexture = texture;
//            this.widthOffset = widthOffset;
//            this.heightOffset = heightOffset;
//            this.width = width;
//            this.height = height;
//        }
//
//        public V2I render(GuiGraphicsExtractor guiGraphics, int x, int y) {
//            guiGraphics.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, guiTexture.texture, x, y, widthOffset, heightOffset, width, height, guiTexture.wholeWidth, guiTexture.wholeHeight);
//            return new V2I(x, y);
//        }
//
//        public V2I renderCenterVertically(GuiGraphicsExtractor guiGraphics, Window window, int x) {
//            return render(guiGraphics, x, GuiUtil.heightFrameCenter(window, this.height));
//        }
//
//        public V2I renderCenterHorizontally(GuiGraphicsExtractor guiGraphics, Window window, int y) {
//            return render(guiGraphics, GuiUtil.widthFrameCenter(window, this.width), y);
//        }
//
//        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio) {
//            return renderRatio(guiGraphics, window, widthRatio, heightRatio, 0, 0);
//        }
//
//        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
//            return render(guiGraphics, GuiUtil.widthFrameRatio(window, this.width, widthRatio) + xOffset, GuiUtil.heightFrameRatio(window, this.height, heightRatio) + yOffset);
//        }
//    }
//
//    public static class GuiSprite {
//        public Identifier sprite;
//        public int spriteWidth;
//        public int spriteHeight;
//
//        public GuiSprite(String path, int spriteWidth, int spriteHeight) {
//            this.sprite = ArsTransmutatoria.makeMyIdentifier(path);
//            this.spriteWidth = spriteWidth;
//            this.spriteHeight = spriteHeight;
//        }
//    }
//
//    public static class GuiSpriteImage {
//        public GuiSprite guiSprite;
//        public int textureX;
//        public int textureY;
//        public int width;
//        public int height;
//
//        public GuiSpriteImage(GuiSprite guiSprite, int textureX, int textureY, int width, int height) {
//            this.guiSprite = guiSprite;
//            this.textureX = textureX;
//            this.textureY = textureY;
//            this.width = width;
//            this.height = height;
//        }
//
//        public V2I render(GuiGraphicsExtractor guiGraphics, int x, int y) {
//            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, guiSprite.sprite, guiSprite.spriteWidth, guiSprite.spriteHeight, textureX, textureY, x, y, width, height);
//            return new V2I(x, y);
//        }
//
//        public V2I renderCenterVertically(GuiGraphicsExtractor guiGraphics, Window window, int x) {
//            return render(guiGraphics, x, GuiUtil.heightFrameCenter(window, this.height));
//        }
//
//        public V2I renderCenterHorizontally(GuiGraphicsExtractor guiGraphics, Window window, int y) {
//            return render(guiGraphics, GuiUtil.widthFrameCenter(window, this.width), y);
//        }
//
//        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio) {
//            return renderRatio(guiGraphics, window, widthRatio, heightRatio, 0, 0);
//        }
//
//        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
//            return render(guiGraphics, GuiUtil.widthFrameRatio(window, this.width, widthRatio) + xOffset, GuiUtil.heightFrameRatio(window, this.height, heightRatio) + yOffset);
//        }
//    }
}
