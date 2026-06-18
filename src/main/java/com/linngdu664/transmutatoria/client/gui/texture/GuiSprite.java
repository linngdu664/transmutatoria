package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.FloatGuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiSprite(Identifier identifier, int wholeWidth, int wholeHeight) implements TextureRenderable {
    public GuiSprite(String path, int wholeWidth, int wholeHeight) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y, int u, int v) {
        guiGraphics.blitSprite(option.renderPipeline(), identifier, wholeWidth, wholeHeight, u, v, x, y, wholeWidth, wholeHeight, option.color());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y, float u, float v) {
        FloatGuiGraphics.blitSprite(guiGraphics, option.renderPipeline(), identifier, wholeWidth, wholeHeight, u, v, x, y, wholeWidth, wholeHeight, option.color());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y, float u, float v, float widthOverride, float heightOverride) {
        FloatGuiGraphics.blitSprite(guiGraphics, option.renderPipeline(), identifier, wholeWidth, wholeHeight, u, v, x, y, widthOverride, heightOverride, option.color());
    }

    @Override
    public int width() {
        return wholeWidth;
    }

    @Override
    public int height() {
        return wholeHeight;
    }
}
