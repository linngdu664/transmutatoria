package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.GuiUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiTexture(Identifier identifier, int wholeWidth, int wholeHeight) implements TextureRenderable {
    public GuiTexture(String path, int wholeWidth, int wholeHeight) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y, int u, int v) {
        guiGraphics.blit(option.renderPipeline(), identifier, x, y, u, v, wholeWidth, wholeHeight, wholeWidth, wholeHeight, option.color());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, float x, float y, float u, float v, float widthOverride, float heightOverride) {
        GuiUtil.blit(guiGraphics, option.renderPipeline(), identifier, x, y, u, v, widthOverride, heightOverride, widthOverride, heightOverride, wholeWidth, wholeHeight, option.color());
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
