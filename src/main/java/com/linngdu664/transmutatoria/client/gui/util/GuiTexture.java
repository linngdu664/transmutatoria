package com.linngdu664.transmutatoria.client.gui.util;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiTexture(Identifier identifier, int wholeWidth, int wholeHeight) implements TextureRenderable {
    public GuiTexture(String path, int wholeWidth, int wholeHeight) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight);
    }

    @Override
    public V2I render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y) {
        guiGraphics.blit(option.renderPipeline(), identifier, x, y, 0, 0, wholeWidth, wholeHeight, wholeWidth, wholeHeight, option.color());
        return new V2I(x, y);
    }

    @Override
    public int getWidth() {
        return wholeWidth;
    }

    @Override
    public int getHeight() {
        return wholeHeight;
    }
}
