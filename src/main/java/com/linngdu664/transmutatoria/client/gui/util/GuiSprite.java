package com.linngdu664.transmutatoria.client.gui.util;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public record GuiSprite(Identifier identifier, int wholeWidth, int wholeHeight) implements TextureRenderable {
    public GuiSprite(String path, int wholeWidth, int wholeHeight) {
        this(ArsTransmutatoria.makeMyIdentifier(path), wholeWidth, wholeHeight);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, TextureOption option, int x, int y) {
        guiGraphics.blitSprite(option.renderPipeline(), identifier, x, y, wholeWidth, wholeHeight, option.color());
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
