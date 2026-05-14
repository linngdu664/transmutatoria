package com.linngdu664.transmutatoria.client.renderer.state.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public record FloatColoredQuadRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, Vec2 a, Vec2 b, Vec2 c, Vec2 d, int color) implements GuiElementRenderState {
    @Override
    public void buildVertices(VertexConsumer vertexConsumer) {
        vertexConsumer.addVertexWith2DPose(pose, a.x, a.y).setColor(color);
        vertexConsumer.addVertexWith2DPose(pose, b.x, b.y).setColor(color);
        vertexConsumer.addVertexWith2DPose(pose, c.x, c.y).setColor(color);
        vertexConsumer.addVertexWith2DPose(pose, d.x, d.y).setColor(color);
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return null;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return null;
    }
}
