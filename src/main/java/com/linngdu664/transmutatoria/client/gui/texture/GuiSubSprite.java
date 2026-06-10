package com.linngdu664.transmutatoria.client.gui.texture;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public record GuiSubSprite(GuiSprite fullSprite, int xOffset, int yOffset, int width, int height, int u, int v) implements TextureRenderable {
    public GuiSubSprite(GuiSprite fullSprite, int xOffset, int yOffset, int width, int height) {
        this(fullSprite, xOffset, yOffset, width, height, 0, 0);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y, int u, int v) {
        guiGraphics.blitSprite(option.renderPipeline(), fullSprite.identifier(), fullSprite.wholeWidth(), fullSprite.wholeHeight(), fullSprite.u() + xOffset + u, fullSprite.v() + yOffset + v, x, y, width, height, option.color());
    }
}
