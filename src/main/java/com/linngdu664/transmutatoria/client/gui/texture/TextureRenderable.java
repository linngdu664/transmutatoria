package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.client.gui.PosUtil;
import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
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

    default V2I renderVerticalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int x) {
        int y = PosUtil.heightFrameCenter(window, height());
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }

    default V2I renderVerticalCenter(GuiGraphicsExtractor guiGraphics, Window window, int x) {
        return renderVerticalCenter(guiGraphics, TextureOption.DEFAULT, window, x);
    }

    default V2I renderHorizontalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int y) {
        int x = PosUtil.widthFrameCenter(window, width());
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }

    default V2I renderHorizontalCenter(GuiGraphicsExtractor guiGraphics, Window window, int y) {
        return renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, y);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, float widthRatio, float heightRatio) {
        return renderRatio(guiGraphics, option, window, widthRatio, heightRatio, 0, 0);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, float widthRatio, float heightRatio) {
        return renderRatio(guiGraphics, TextureOption.DEFAULT, window, widthRatio, heightRatio, 0, 0);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, float widthRatio, float heightRatio, int xOffset, int yOffset) {
        int x = PosUtil.widthFrameRatio(window, width(), widthRatio) + xOffset;
        int y = PosUtil.heightFrameRatio(window, height(), heightRatio) + yOffset;
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, Window window, float widthRatio, float heightRatio, int xOffset, int yOffset) {
        return renderRatio(guiGraphics, TextureOption.DEFAULT, window, widthRatio, heightRatio, xOffset, yOffset);
    }
}
