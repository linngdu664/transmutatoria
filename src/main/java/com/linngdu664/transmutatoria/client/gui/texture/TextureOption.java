package com.linngdu664.transmutatoria.client.gui.texture;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;

public record TextureOption(RenderPipeline renderPipeline, int color) {
    public static TextureOption DEFAULT = new TextureOption(RenderPipelines.GUI_TEXTURED, -1);
    public static TextureOption PREMULTIPLIED_ALPHA = new TextureOption(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, -1);

    public static TextureOption withAlpha(int alpha) {
        return new TextureOption(RenderPipelines.GUI_TEXTURED, (alpha << 24) | 0xffffff);
    }
}
