package com.linngdu664.transmutatoria.client.gui.screens;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.GuiUtil;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.inventory.EmeraldTabletMenu;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScreenEmeraldTablet extends AbstractContainerScreen<EmeraldTabletMenu> {
    private static final int IMAGE_SIZE = 256;
    private static final int NODE_SIZE = 28;
    private static final int ESSENCE_SIZE = 16;
    private static final float NODE_RADIUS = 13.5F;
    private static final float NODE_HIT_RADIUS = 15.0F;
    private static final float RELATION_RADIUS = 86.0F;
    private static final float OUTER_RING_RADIUS = 112.0F;
    private static final float RELATION_ENDPOINT_OFFSET_X = 2.0F;
    private static final float RELATION_ENDPOINT_OFFSET_Y = 1.0F;
    private static final int ACTIVE_ALPHA = 235;
    private static final int DIM_ALPHA = 54;
    private static final int RED_RGB = 0xd82323;
    private static final int BLACK_RGB = 0x100909;
    private static final int BLUE_RGB = 0x236fe0;
    private static final int GREEN_RGB = 0x2fbd53;
    private static final int SYMBIOSIS_RGB = 0xcf3030;
    private static final int RELATION_GLOW_RGB = 0xd9f4d0;
    private static final EssenceMetal[] METALS = EssenceMetal.values();
    private static final List<RelationEdge> EDGES = createEdges();
    private static final Map<Integer, Identifier> RELATION_TEXTURES = new HashMap<>();

    public ScreenEmeraldTablet(EmeraldTabletMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, IMAGE_SIZE, IMAGE_SIZE);
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        titleLabelY = 10;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.fill(0, 0, width, height, 0x90000000);

        Vec2 center = center();
        drawTabletDisk(graphics, center);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);
        Vec2 center = center();
        EssenceMetal hovered = hoveredMetal(mouseX, mouseY, center);
        drawRelationTexture(graphics, hovered);
        drawNodes(graphics, center, hovered, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, 0xffd9f4d0, true);
    }

    private Vec2 center() {
        return new Vec2(leftPos + imageWidth * 0.5F, topPos + imageHeight * 0.54F);
    }

    private void drawTabletDisk(GuiGraphicsExtractor graphics, Vec2 center) {
        drawRing(graphics, center, OUTER_RING_RADIUS, 2.0F, 0xcc3f7f53);
        drawRing(graphics, center, OUTER_RING_RADIUS - 9.0F, 1.0F, 0x8065b86e);
        drawRing(graphics, center, RELATION_RADIUS + NODE_RADIUS + 8.0F, 1.0F, 0x703f7f53);
        drawRing(graphics, center, 38.0F, 1.0F, 0x504f9f5e);

        for (int i = 0; i < 12; i++) {
            float angle = Mth.TWO_PI * i / 12.0F - Mth.HALF_PI;
            Vec2 inner = pointOnCircle(center, angle, 42.0F);
            Vec2 outer = pointOnCircle(center, angle, OUTER_RING_RADIUS - 15.0F);
            GuiUtil.gradientLine(graphics, inner, outer, 0.8F, 0x204f9f5e, 0x805ba768);
        }
    }

    private void drawRelationTexture(GuiGraphicsExtractor graphics, EssenceMetal hovered) {
        int textureKey = hovered == null ? -1 : hovered.ordinal();
        Identifier textureId = RELATION_TEXTURES.computeIfAbsent(textureKey, ScreenEmeraldTablet::registerRelationTexture);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                textureId,
                leftPos,
                topPos,
                0.0F,
                0.0F,
                IMAGE_SIZE,
                IMAGE_SIZE,
                IMAGE_SIZE,
                IMAGE_SIZE
        );
    }

    private static Identifier registerRelationTexture(int textureKey) {
        String path = textureKey < 0 ? "dynamic/emerald_tablet_relations/all" : "dynamic/emerald_tablet_relations/" + textureKey;
        Identifier textureId = ArsTransmutatoria.makeMyIdentifier(path);
        NativeImage image = new NativeImage(IMAGE_SIZE, IMAGE_SIZE, true);
        Vec2 center = new Vec2(IMAGE_SIZE * 0.5F, IMAGE_SIZE * 0.54F);
        EssenceMetal hovered = textureKey < 0 ? null : METALS[textureKey];

        if (hovered == null) {
            for (RelationEdge edge : EDGES) {
                drawRasterRelation(image, center, edge, false);
            }
        } else {
            for (RelationEdge edge : EDGES) {
                if (edge.isConnectedTo(hovered)) {
                    drawRasterRelation(image, center, edge, true);
                }
            }
        }

        Minecraft.getInstance().getTextureManager().register(
                textureId,
                new DynamicTexture(() -> "Emerald Tablet relation graph " + textureKey, image)
        );
        return textureId;
    }

    private static void drawRasterRelation(NativeImage image, Vec2 center, RelationEdge edge, boolean active) {
        Vec2 from = metalCenterStatic(center, edge.from());
        Vec2 to = metalCenterStatic(center, edge.to());
        float thickness = active ? 4.0F : 2.2F;
        int alpha = active ? 245 : 105;

        if (edge.kind() == RelationKind.SYMBIOSIS) {
            Vec2 start = offsetRelationPoint(edgePoint(from, to, NODE_RADIUS + 2.0F));
            Vec2 end = offsetRelationPoint(edgePoint(to, from, NODE_RADIUS + 2.0F));
            drawRasterGlow(image, start, end, thickness, alpha);
            int red = argb(alpha, SYMBIOSIS_RGB);
            int green = argb(alpha, GREEN_RGB);
            Vec2 middle = lerp(start, end, 0.5F);
            drawRasterLine(image, start, middle, thickness, red, green);
            drawRasterLine(image, middle, end, thickness, green, red);
            return;
        }

        Vec2 start = offsetRelationPoint(edgePoint(from, to, NODE_RADIUS + 2.0F));
        Vec2 end = offsetRelationPoint(edgePoint(to, from, NODE_RADIUS + 4.0F));
        int black = argb(alpha, BLACK_RGB);
        if (edge.kind() == RelationKind.MUTUAL_RESTRAINED) {
            int blue = argb(alpha, BLUE_RGB);
            Vec2 middle = lerp(start, end, 0.5F);
            drawRasterGlow(image, start, end, thickness, alpha);
            drawRasterLine(image, start, middle, thickness + 1.2F, black, blue);
            drawRasterLine(image, middle, end, thickness + 1.2F, blue, black);
            return;
        }

        int red = argb(alpha, RED_RGB);
        drawRasterGlow(image, start, end, thickness, alpha);

        if (edge.kind() == RelationKind.DOUBLE_RESTRAIN) {
            int blue = argb(alpha, BLUE_RGB);
            Vec2 middle = lerp(start, end, 0.5F);
            drawRasterLine(image, start, middle, thickness, red, black);
            drawRasterLine(image, middle, end, thickness, black, blue);
        } else {
            drawRasterLine(image, start, end, thickness, red, black);
        }
    }

    private static void drawRasterGlow(NativeImage image, Vec2 start, Vec2 end, float thickness, int alpha) {
        int glow = argb(activeGlowAlpha(alpha), RELATION_GLOW_RGB);
        drawRasterLine(image, start, end, thickness + 2.6F, glow, glow);
    }

    private static int activeGlowAlpha(int alpha) {
        return Math.max(40, alpha / 3);
    }

    private static void drawRasterLine(NativeImage image, Vec2 start, Vec2 end, float thickness, int startColor, int endColor) {
        Vec2 delta = end.add(start.negated());
        float length = delta.length();
        if (length <= 0.0F || thickness <= 0.0F) {
            return;
        }

        int steps = Math.max(1, Mth.ceil(length * 1.6F));
        for (int i = 0; i <= steps; i++) {
            float t = (float)i / steps;
            Vec2 point = lerp(start, end, t);
            drawBrush(image, point.x, point.y, thickness * 0.5F, lerpColor(startColor, endColor, t));
        }
    }

    private static void drawBrush(NativeImage image, float centerX, float centerY, float radius, int color) {
        int minX = Mth.floor(centerX - radius);
        int maxX = Mth.ceil(centerX + radius);
        int minY = Mth.floor(centerY - radius);
        int maxY = Mth.ceil(centerY + radius);
        float radiusSq = radius * radius;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                float dx = x + 0.5F - centerX;
                float dy = y + 0.5F - centerY;
                float distSq = dx * dx + dy * dy;
                if (distSq <= radiusSq) {
                    float edgeFade = Mth.clamp((radiusSq - distSq) / Math.max(1.0F, radiusSq * 0.45F), 0.0F, 1.0F);
                    blendPixel(image, x, y, colorWithAlphaMultiplier(color, edgeFade));
                }
            }
        }
    }

    private static void blendPixel(NativeImage image, int x, int y, int source) {
        if (x < 0 || y < 0 || x >= IMAGE_SIZE || y >= IMAGE_SIZE) {
            return;
        }

        int sourceAlpha = (source >>> 24) & 0xff;
        if (sourceAlpha <= 0) {
            return;
        }

        int target = image.getPixel(x, y);
        int targetAlpha = (target >>> 24) & 0xff;
        int outAlpha = sourceAlpha + targetAlpha * (255 - sourceAlpha) / 255;
        if (outAlpha <= 0) {
            return;
        }

        int sourceRed = (source >>> 16) & 0xff;
        int sourceGreen = (source >>> 8) & 0xff;
        int sourceBlue = source & 0xff;
        int targetRed = (target >>> 16) & 0xff;
        int targetGreen = (target >>> 8) & 0xff;
        int targetBlue = target & 0xff;
        int targetWeight = targetAlpha * (255 - sourceAlpha) / 255;
        int outRed = (sourceRed * sourceAlpha + targetRed * targetWeight) / outAlpha;
        int outGreen = (sourceGreen * sourceAlpha + targetGreen * targetWeight) / outAlpha;
        int outBlue = (sourceBlue * sourceAlpha + targetBlue * targetWeight) / outAlpha;
        image.setPixel(x, y, (outAlpha << 24) | (outRed << 16) | (outGreen << 8) | outBlue);
    }

    private static int colorWithAlphaMultiplier(int color, float multiplier) {
        int alpha = Mth.clamp(Math.round(((color >>> 24) & 0xff) * multiplier), 0, 255);
        return (alpha << 24) | (color & 0x00ffffff);
    }

    private static int lerpColor(int startColor, int endColor, float amount) {
        int a = Mth.lerpInt(amount, (startColor >>> 24) & 0xff, (endColor >>> 24) & 0xff);
        int r = Mth.lerpInt(amount, (startColor >>> 16) & 0xff, (endColor >>> 16) & 0xff);
        int g = Mth.lerpInt(amount, (startColor >>> 8) & 0xff, (endColor >>> 8) & 0xff);
        int b = Mth.lerpInt(amount, startColor & 0xff, endColor & 0xff);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void drawRelations(GuiGraphicsExtractor graphics, Vec2 center, EssenceMetal hovered) {
        if (hovered != null) {
            for (RelationEdge edge : EDGES) {
                if (!edge.isConnectedTo(hovered)) {
                    drawRelation(graphics, center, edge, false);
                }
            }
            for (RelationEdge edge : EDGES) {
                if (edge.isConnectedTo(hovered)) {
                    drawRelation(graphics, center, edge, true);
                }
            }
            return;
        }

        for (RelationEdge edge : EDGES) {
            drawRelation(graphics, center, edge, true);
        }
    }

    private void drawRelation(GuiGraphicsExtractor graphics, Vec2 center, RelationEdge edge, boolean active) {
        int alpha = active ? ACTIVE_ALPHA : DIM_ALPHA;
        float thickness = active ? 2.8F : 1.6F;

        Vec2 from = metalCenter(center, edge.from());
        Vec2 to = metalCenter(center, edge.to());
        if (edge.kind() == RelationKind.SYMBIOSIS) {
            drawSymbiosis(graphics, from, to, thickness, alpha);
        } else if (edge.kind() == RelationKind.MUTUAL_RESTRAINED) {
            drawMutualRestrained(graphics, from, to, thickness, alpha);
        } else {
            drawRestrainLine(graphics, from, to, thickness, alpha, edge.kind() == RelationKind.DOUBLE_RESTRAIN);
        }
    }

    private void drawSymbiosis(GuiGraphicsExtractor graphics, Vec2 a, Vec2 b, float thickness, int alpha) {
        Vec2 start = offsetRelationPoint(edgePoint(a, b, NODE_RADIUS + 2.0F));
        Vec2 end = offsetRelationPoint(edgePoint(b, a, NODE_RADIUS + 2.0F));
        int red = argb(alpha, SYMBIOSIS_RGB);
        int green = argb(alpha, GREEN_RGB);
        Vec2 middle = lerp(start, end, 0.5F);
        drawRelationGlow(graphics, start, end, thickness, alpha);
        GuiUtil.gradientLine(graphics, start, middle, thickness, red, green);
        GuiUtil.gradientLine(graphics, middle, end, thickness, green, red);
    }

    private void drawMutualRestrained(GuiGraphicsExtractor graphics, Vec2 a, Vec2 b, float thickness, int alpha) {
        Vec2 start = offsetRelationPoint(edgePoint(a, b, NODE_RADIUS + 2.0F));
        Vec2 end = offsetRelationPoint(edgePoint(b, a, NODE_RADIUS + 3.0F));
        int black = argb(alpha, BLACK_RGB);
        int blue = argb(alpha, BLUE_RGB);
        Vec2 middle = lerp(start, end, 0.5F);
        drawRelationGlow(graphics, start, end, thickness, alpha);
        GuiUtil.gradientLine(graphics, start, middle, thickness + 1.2F, black, blue);
        GuiUtil.gradientLine(graphics, middle, end, thickness + 1.2F, blue, black);
    }

    private void drawRestrainLine(GuiGraphicsExtractor graphics, Vec2 restrainer, Vec2 restrained, float thickness, int alpha, boolean doubled) {
        Vec2 start = offsetRelationPoint(edgePoint(restrainer, restrained, NODE_RADIUS + 2.0F));
        Vec2 end = offsetRelationPoint(edgePoint(restrained, restrainer, NODE_RADIUS + 3.0F));
        int red = argb(alpha, RED_RGB);
        int black = argb(alpha, BLACK_RGB);
        drawRelationGlow(graphics, start, end, thickness, alpha);

        if (doubled) {
            int blue = argb(alpha, BLUE_RGB);
            Vec2 middle = lerp(start, end, 0.5F);
            GuiUtil.gradientLine(graphics, start, middle, thickness, red, black);
            GuiUtil.gradientLine(graphics, middle, end, thickness, black, blue);
        } else {
            GuiUtil.gradientLine(graphics, start, end, thickness, red, black);
        }
    }

    private void drawRelationGlow(GuiGraphicsExtractor graphics, Vec2 start, Vec2 end, float thickness, int alpha) {
        int glow = argb(Math.max(22, alpha / 4), RELATION_GLOW_RGB);
        GuiUtil.gradientLine(graphics, start, end, thickness + 1.6F, glow, glow);
    }

    private void drawNodes(GuiGraphicsExtractor graphics, Vec2 center, EssenceMetal hovered, int mouseX, int mouseY) {
        for (EssenceMetal metal : METALS) {
            Vec2 nodeCenter = metalCenter(center, metal);
            boolean active = hovered == null || metal == hovered || isRelated(metal, hovered);
            int alpha = active ? 255 : DIM_ALPHA;
            int nodeX = Math.round(nodeCenter.x - NODE_SIZE * 0.5F);
            int nodeY = Math.round(nodeCenter.y - NODE_SIZE * 0.5F);
            int iconX = Math.round(nodeCenter.x - ESSENCE_SIZE * 0.5F);
            int iconY = Math.round(nodeCenter.y - ESSENCE_SIZE * 0.5F);

            Textures.EMERALD_TABLET_ESSENCE_NODE.render(graphics, TextureOption.withAlpha(alpha), nodeX, nodeY);
            if (metal == hovered) {
                Textures.SLOT_SELECTED.render(graphics, nodeX, nodeY);
            }
            metal.getDefaultTexture().render(graphics, TextureOption.withAlpha(alpha), iconX + 2, iconY + 1);
        }

        if (hovered != null) {
            graphics.setComponentTooltipForNextFrame(
                    font,
                    List.of(Component.translatable("item.transmutatoria." + hovered.getKey())),
                    mouseX,
                    mouseY
            );
        }
    }

    private EssenceMetal hoveredMetal(int mouseX, int mouseY, Vec2 center) {
        Vec2 mouse = new Vec2(mouseX, mouseY);
        for (EssenceMetal metal : METALS) {
            if (mouse.add(metalCenter(center, metal).negated()).length() <= NODE_HIT_RADIUS) {
                return metal;
            }
        }
        return null;
    }

    private boolean isRelated(EssenceMetal metal, EssenceMetal hovered) {
        if (hovered == null) {
            return true;
        }
        return metal.getRelationTo(hovered) != EssenceMetal.Relation.NEUTRAL
                || hovered.getRelationTo(metal) != EssenceMetal.Relation.NEUTRAL;
    }

    private Vec2 metalCenter(Vec2 center, EssenceMetal metal) {
        return metalCenterStatic(center, metal);
    }

    private static Vec2 metalCenterStatic(Vec2 center, EssenceMetal metal) {
        int index = metal.ordinal();
        float angle = Mth.TWO_PI * index / METALS.length - Mth.HALF_PI;
        return pointOnCircle(center, angle, RELATION_RADIUS);
    }

    private static Vec2 pointOnCircle(Vec2 center, float angle, float radius) {
        return new Vec2(center.x + Mth.cos(angle) * radius, center.y + Mth.sin(angle) * radius);
    }

    private static Vec2 edgePoint(Vec2 origin, Vec2 toward, float distance) {
        Vec2 delta = toward.add(origin.negated());
        float length = delta.length();
        if (length <= 0.0F) {
            return origin;
        }
        return origin.add(delta.scale(distance / length));
    }

    private static Vec2 offsetRelationPoint(Vec2 point) {
        return point.add(new Vec2(RELATION_ENDPOINT_OFFSET_X, RELATION_ENDPOINT_OFFSET_Y));
    }

    private static Vec2 lerp(Vec2 a, Vec2 b, float amount) {
        return a.add(b.add(a.negated()).scale(amount));
    }

    private static void drawRing(GuiGraphicsExtractor graphics, Vec2 center, float radius, float thickness, int color) {
        int segments = 96;
        Vec2 previous = pointOnCircle(center, 0.0F, radius);
        for (int i = 1; i <= segments; i++) {
            float angle = Mth.TWO_PI * i / segments;
            Vec2 current = pointOnCircle(center, angle, radius);
            GuiUtil.gradientLine(graphics, previous, current, thickness, color, color);
            previous = current;
        }
    }

    private static int argb(int alpha, int rgb) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0x00ffffff);
    }

    private static List<RelationEdge> createEdges() {
        List<RelationEdge> edges = new ArrayList<>();
        for (EssenceMetal from : METALS) {
            for (EssenceMetal to : METALS) {
                if (from == to) {
                    continue;
                }

                EssenceMetal.Relation relation = from.getRelationTo(to);
                switch (relation) {
                    case RESTRAIN -> edges.add(new RelationEdge(from, to, RelationKind.RESTRAIN));
                    case DOUBLE_RESTRAIN -> edges.add(new RelationEdge(from, to, RelationKind.DOUBLE_RESTRAIN));
                    case MUTUAL_RESTRAINED -> {
                        if (from.ordinal() < to.ordinal()) {
                            edges.add(new RelationEdge(from, to, RelationKind.MUTUAL_RESTRAINED));
                        }
                    }
                    case SYMBIOSIS -> {
                        if (from.ordinal() < to.ordinal()) {
                            edges.add(new RelationEdge(from, to, RelationKind.SYMBIOSIS));
                        }
                    }
                }
            }
        }
        return List.copyOf(edges);
    }

    private enum RelationKind {
        RESTRAIN,
        DOUBLE_RESTRAIN,
        MUTUAL_RESTRAINED,
        SYMBIOSIS
    }

    private record RelationEdge(EssenceMetal from, EssenceMetal to, RelationKind kind) {
        boolean isConnectedTo(EssenceMetal metal) {
            return from == metal || to == metal;
        }
    }
}
