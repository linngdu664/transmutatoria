package com.linngdu664.transmutatoria.client.gui.texture;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public interface TextureRenderable {
    void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y, int u, int v);

    void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y, float u, float v);

    void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y, float u, float v, float widthOverride, float heightOverride);

    int width();

    int height();

    Identifier identifier();

    default void render(GuiGraphicsExtractor guiGraphics, int x, int y) {
        render(guiGraphics, TextureOption.DEFAULT, x, y, 0, 0);
    }

    default void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y) {
        render(guiGraphics, option, x, y, 0, 0);
    }

    default void render(GuiGraphicsExtractor guiGraphics, int x, int y, int u, int v) {
        render(guiGraphics, TextureOption.DEFAULT, x, y, u, v);
    }

    default void render(GuiGraphicsExtractor guiGraphics, float x, float y) {
        render(guiGraphics, TextureOption.DEFAULT, x, y, 0, 0);
    }

    default void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y) {
        render(guiGraphics, option, x, y, 0, 0);
    }

    default void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v) {
        render(guiGraphics, TextureOption.DEFAULT, x, y, u, v);
    }
}
