package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.client.gui.FloatGuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiSubSprite(GuiSprite fullSprite, int xOffset, int yOffset, int width, int height) implements TextureRenderable {
    @Override
    public Identifier identifier() {
        return fullSprite.identifier();
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int x, int y, int u, int v, TextureOption option) {
        guiGraphics.blitSprite(option.renderPipeline(), fullSprite.identifier(), fullSprite.wholeWidth(), fullSprite.wholeHeight(), xOffset + u, yOffset + v, x, y, width, height, option.color());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v, TextureOption option) {
        FloatGuiGraphics.blitSprite(guiGraphics, option.renderPipeline(), fullSprite.identifier(), fullSprite.wholeWidth(), fullSprite.wholeHeight(), xOffset + u, yOffset + v, x, y, width, height, option.color());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, float x, float y, float u, float v, float widthOverride, float heightOverride, TextureOption option) {
        FloatGuiGraphics.blitSprite(guiGraphics, option.renderPipeline(), fullSprite.identifier(), fullSprite.wholeWidth(), fullSprite.wholeHeight(), xOffset + u, yOffset + v, x, y, widthOverride, heightOverride, option.color());
    }
}
