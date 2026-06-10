package com.linngdu664.transmutatoria.client.gui.texture;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiTexture(Identifier identifier, int wholeWidth, int wholeHeight, int u, int v) implements TextureRenderable {
    public GuiTexture(String path, int wholeWidth, int wholeHeight) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight, 0, 0);
    }

    public GuiTexture(String path, int wholeWidth, int wholeHeight, int u, int v) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight, u, v);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y, int u, int v) {
        guiGraphics.blit(option.renderPipeline(), identifier, x, y, u, v, wholeWidth, wholeHeight, wholeWidth, wholeHeight, option.color());
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
