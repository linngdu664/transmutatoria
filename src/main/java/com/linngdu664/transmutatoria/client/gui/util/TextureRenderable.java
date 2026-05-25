package com.linngdu664.transmutatoria.client.gui.util;

import com.linngdu664.transmutatoria.client.gui.GuiUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface TextureRenderable {
    V2I render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y);

    int width();

    int height();

    default V2I renderVerticalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int x) {
        return render(guiGraphics, option, x, GuiUtil.heightFrameCenter(window, height()));
    }

    default V2I renderHorizontalCenter(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, int y) {
        return render(guiGraphics, option, GuiUtil.widthFrameCenter(window, width()), y);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, double widthRatio, double heightRatio) {
        return renderRatio(guiGraphics, option, window, widthRatio, heightRatio, 0, 0);
    }

    default V2I renderRatio(GuiGraphicsExtractor guiGraphics, TextureOption option, Window window, double widthRatio, double heightRatio, int xOffset, int yOffset) {
        return render(guiGraphics, option, GuiUtil.widthFrameRatio(window, width(), widthRatio) + xOffset, GuiUtil.heightFrameRatio(window, height(), heightRatio) + yOffset);
    }
}
