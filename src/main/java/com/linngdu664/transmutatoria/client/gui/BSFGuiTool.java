package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
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
    public static final GuiTexture SIMPLE_FRAME = new GuiTexture("textures/gui/simple_frame.png", 22, 22);
    public static final GuiImage SIMPLE_FRAME_IMG = new GuiImage(SIMPLE_FRAME, 0,0,22, 22);
//    public static final GuiTexture SNOWBALL_FRAME = new GuiTexture("textures/gui/snowball_frame.png", 23, 62);
//    public static final GuiImage SNOWBALL_SLOT_FRAME_GUI = new GuiImage(SNOWBALL_FRAME, 0, 0, 23, 62);
//    public static final GuiTexture TWEAKER_FRAME = new GuiTexture("textures/gui/tweaker_frame.png", 114, 106);
//    public static final GuiImage TWEAKER_LOCATOR_GUI = new GuiImage(TWEAKER_FRAME, 1, 0, 22, 82);
//    public static final GuiImage TWEAKER_STATUS_GUI = new GuiImage(TWEAKER_FRAME, 24, 0, 22, 102);
//    public static final GuiImage TWEAKER_SELECTOR_GUI = new GuiImage(TWEAKER_FRAME, 0, 82, 24, 24);
//    public static final GuiImage GOLEM_LOCATOR_GUI = new GuiImage(TWEAKER_FRAME, 47, 0, 22, 82);
//    public static final GuiImage GOLEM_STATUS_GUI = new GuiImage(TWEAKER_FRAME, 70, 0, 22, 102);
//    public static final GuiImage GOLEM_SELECTOR_GUI = new GuiImage(TWEAKER_FRAME, 46, 82, 24, 24);
//    public static final GuiImage SETTER_ARROW_GUI = new GuiImage(TWEAKER_FRAME, 92, 1, 8, 20);
//    public static final GuiImage ADVANCE_MODE_GUI = new GuiImage(TWEAKER_FRAME, 92, 60, 22, 22);
//    public static final GuiImage EQUIPMENT_SLOT_FRAME_GUI = new GuiImage(TWEAKER_FRAME, 92, 84, 22, 22);

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
/*
    public static Vec2 renderHackBox(GuiGraphicsExtractor guiGraphics, CoordinateConverter converter, Window window, LivingEntity livingEntity, int frameColor, float particleTick) {
        AABB aabb = livingEntity.getBoundingBox();
        // todo: lerp bounding box
        List<Vec2> vec2List = converter.convert(List.of(
                new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
                new Vec3(aabb.minX, aabb.maxY, aabb.minZ),
                new Vec3(aabb.minX, aabb.maxY, aabb.maxZ),
                new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.minZ),
                new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ)
        ), guiGraphics.guiWidth(), guiGraphics.guiHeight());
        float minX = vec2List.getFirst().x;
        float minY = vec2List.getFirst().y;
        float maxX = minX;
        float maxY = minY;
        for (Vec2 vec2 : vec2List) {
            if (vec2.x < minX) {
                minX = vec2.x;
            } else if (vec2.x > maxX) {
                maxX = vec2.x;
            }
            if (vec2.y < minY) {
                minY = vec2.y;
            } else if (vec2.y > maxY) {
                maxY = vec2.y;
            }
        }
        Vec2 upperLeftCorner = new Vec2(minX, minY);
        Vec2 lowerRightCorner = new Vec2(maxX, maxY);
        if (!(isInScreen(upperLeftCorner, window) && isInScreen(lowerRightCorner, window))) {
            return null;
        }
        renderOutlineCoordinate(guiGraphics, upperLeftCorner.x, upperLeftCorner.y, lowerRightCorner.x, lowerRightCorner.y, frameColor, 0.5f);
        return new Vec2((upperLeftCorner.x + lowerRightCorner.x) / 2, lowerRightCorner.y);
    }*/

    /*
     * 渲染装备介绍
     *
     * @param guiGraphics
     * @param equipPoint    装备映射到屏幕上的位置
     * @param framePoint    装备框显示位置
     * @param lineXDistance 斜线水平长度
     * @param color         线颜色
     * @param itemStack     装备
     * @param font          字体
     * @param msg           装备描述
     */
    /*public static void renderEquipIntroduced(GuiGraphicsExtractor guiGraphics, Vec2 equipPoint, Vec2 framePoint, int lineXDistance, int color, ItemStack itemStack, Font font, Component msg) {
        Vec2 linkPoint = new Vec2(framePoint.x + EQUIPMENT_SLOT_FRAME_GUI.width, framePoint.y + (float) EQUIPMENT_SLOT_FRAME_GUI.height / 2);
        Vec2 xPoint = new Vec2(equipPoint.x - lineXDistance, linkPoint.y);
        if (xPoint.x < linkPoint.x) {
            xPoint = linkPoint;
        }
        float d = 2;
        renderLineTool(guiGraphics, xPoint.add(new Vec2(0, xPoint.y < equipPoint.y ? 0 : d)), equipPoint, d, color, xPoint.y < equipPoint.y, 0.3f, 0xff000000);
        renderLineTool(guiGraphics, linkPoint, xPoint, d, color, true, 0.3f, 0xff000000);
        renderFillSquareTool(guiGraphics, equipPoint.add(new Vec2(-2f, -1f)), equipPoint.add(new Vec2(2f, 3f)), 0xff000000);
        renderFillSquareTool(guiGraphics, equipPoint.add(new Vec2(-1f, 0)), equipPoint.add(new Vec2(1f, 2f)), color);
        EQUIPMENT_SLOT_FRAME_GUI.render(guiGraphics, (int) framePoint.x, (int) framePoint.y);
        guiGraphics.renderItem(itemStack, (int) (framePoint.x + 3), (int) (framePoint.y + 3));
        FormattedCharSequence formattedcharsequence = msg.getVisualOrderText();
        guiGraphics.drawString(font, formattedcharsequence, framePoint.x - font.width(formattedcharsequence), framePoint.y + 7, color, true);
    }*/

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

    public static class GuiTexture {
        public Identifier texture;
        public int wholeWidth;
        public int wholeHeight;

        public GuiTexture(String path, int holeWidth, int holeHeight) {
            this.texture = ArsTransmutatoria.makeMyIdentifier(path);
            this.wholeWidth = holeWidth;
            this.wholeHeight = holeHeight;
        }
    }

    public static class GuiImage {
        public GuiTexture guiTexture;
        public int widthOffset;
        public int heightOffset;
        public int width;
        public int height;

        public GuiImage(GuiTexture texture, int widthOffset, int heightOffset, int width, int height) {
            this.guiTexture = texture;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;
            this.width = width;
            this.height = height;
        }

        public V2I render(GuiGraphicsExtractor guiGraphics, int x, int y) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, guiTexture.texture, x, y, widthOffset, heightOffset, width, height, guiTexture.wholeWidth, guiTexture.wholeHeight);
            return new V2I(x, y);
        }

        public V2I renderCenterVertically(GuiGraphicsExtractor guiGraphics, Window window, int x) {
            return render(guiGraphics, x, GuiUtil.heightFrameCenter(window, this.height));
        }

        public V2I renderCenterHorizontally(GuiGraphicsExtractor guiGraphics, Window window, int y) {
            return render(guiGraphics, GuiUtil.widthFrameCenter(window, this.width), y);
        }

        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio) {
            return renderRatio(guiGraphics, window, widthRatio, heightRatio, 0, 0);
        }

        public V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
            return render(guiGraphics, GuiUtil.widthFrameRatio(window, this.width, widthRatio) + xOffset, GuiUtil.heightFrameRatio(window, this.height, heightRatio) + yOffset);
        }
    }

//    public static class VarObj {
//        public Component tLocatorComponent;
//        public Component sLocatorComponent;
//        public Component tStatusComponent;
//        public Component sStatusComponent;
//        public BSFGuiTool.V2I locateV2I;
//        public BSFGuiTool.V2I statusV2I;
//    }
}
