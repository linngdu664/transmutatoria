package com.linngdu664.transmutatoria.client.gui.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public record GuiSubSprite(GuiSprite fullSprite, int xOffset, int yOffset, int width, int height) implements TextureRenderable {
    @Override
    public V2I render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y) {
        guiGraphics.blitSprite(option.renderPipeline(), fullSprite.identifier(), fullSprite.wholeWidth(), fullSprite.wholeHeight(), xOffset, yOffset, x, y, width, height, option.color());
        return new V2I(x, y);
    }
}
