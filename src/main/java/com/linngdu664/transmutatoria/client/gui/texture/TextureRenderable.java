package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.client.gui.GuiUtil;
import com.linngdu664.transmutatoria.client.gui.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface TextureRenderable {
    void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y);

    int width();

    int height();

    default V2I renderVerticalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int x) {
        int y = GuiUtil.heightFrameCenter(window, height());
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }

    default V2I renderHorizontalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int y) {
        int x = GuiUtil.widthFrameCenter(window, width());
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, double widthRatio, double heightRatio) {
        return renderRatio(guiGraphics, option, window, widthRatio, heightRatio, 0, 0);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
        int x = GuiUtil.widthFrameRatio(window, width(), widthRatio) + xOffset;
        int y = GuiUtil.heightFrameRatio(window, height(), heightRatio) + yOffset;
        render(guiGraphics, option, x, y);
        return new V2I(x, y);
    }
}
