package com.linngdu664.transmutatoria.client.renderer.state.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public record FloatGradientQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int colorA, int colorB, int colorC, int colorD, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    public FloatGradientQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int colorA, int colorB, int colorC, int colorD, @Nullable ScreenRectangle scissorArea) {
        this(pipeline, textureSetup, pose, a, b, c, d, colorA, colorB, colorC, colorD, scissorArea, computeBounds(a, b, c, d, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(pose, a.x, a.y).setColor(colorA);
        vertexConsumer.addVertexWith2DPose(pose, b.x, b.y).setColor(colorB);
        vertexConsumer.addVertexWith2DPose(pose, c.x, c.y).setColor(colorC);
        vertexConsumer.addVertexWith2DPose(pose, d.x, d.y).setColor(colorD);
    }

    private static @Nullable ScreenRectangle computeBounds(Vec2 a, Vec2 b, Vec2 c, Vec2 d, Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        int left = Mth.floor(Math.min(Math.min(a.x, b.x), Math.min(c.x, d.x)));
        int top = Mth.floor(Math.min(Math.min(a.y, b.y), Math.min(c.y, d.y)));
        int right = Mth.ceil(Math.max(Math.max(a.x, b.x), Math.max(c.x, d.x)));
        int bottom = Mth.ceil(Math.max(Math.max(a.y, b.y), Math.max(c.y, d.y)));
        ScreenRectangle bounds = new ScreenRectangle(left, top, right - left, bottom - top).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(bounds) : bounds;
    }
}
