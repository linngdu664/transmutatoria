package com.linngdu664.transmutatoria.client.gui.texture;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public interface TextureRenderable {
    void render(GuiGraphicsExtractor guiGraphics, int x, int y, int u, int v, TextureOption option);

    void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v, TextureOption option);

    void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v, float widthOverride, float heightOverride, TextureOption option);

    int width();

    int height();

    Identifier identifier();

    default void render(GuiGraphicsExtractor guiGraphics, int x, int y) {
        render(guiGraphics, x, y, 0, 0, TextureOption.DEFAULT);
    }

    default void render(GuiGraphicsExtractor guiGraphics, int x, int y, TextureOption option) {
        render(guiGraphics, x, y, 0, 0, option);
    }

    default void render(GuiGraphicsExtractor guiGraphics, int x, int y, int u, int v) {
        render(guiGraphics, x, y, u, v, TextureOption.DEFAULT);
    }

    default void render(GuiGraphicsExtractor guiGraphics, float x, float y) {
        render(guiGraphics, x, y, 0, 0, TextureOption.DEFAULT);
    }

    default void render(GuiGraphicsExtractor guiGraphics, float x, float y, TextureOption option) {
        render(guiGraphics, x, y, 0, 0, option);
    }

    default void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v) {
        render(guiGraphics, x, y, u, v, TextureOption.DEFAULT);
    }
}
