package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.client.gui.texture.GuiTexture;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/** Shared geometry and theming for the three-slot alchemical JEI layouts. */
final class AlchemicalJeiGraphics {
    static final int WIDTH = 148;
    static final int HEIGHT = 76;
    static final int INPUT_X = 12;
    static final int CATALYST_X = 66;
    static final int OUTPUT_X = 120;
    static final int SLOT_Y = 29;
    static final int HEADER_BOTTOM = 16;
    static final int INFO_TOP = 57;

    /** Display size for every alchemical background, independent of its source texture resolution. */
    static final int ALCHEMY_BACKGROUND_SIZE = 100;

    static final Theme PARCHMENT_THEME = new Theme(
            0xFF3B281B,
            0xFFF2E2C4,
            0xFF5B3922,
            0xFFE4CDA6,
            0xFFC7964E,
            0xFFE1B75F,
            0xFF4A2F21,
            0xFFFFE3A6,
            0x96FFFFFF
    );

    static final Theme DECOMPOSITION_THEME = new Theme(
            0xFF261735,
            0xFFEEE7F5,
            0xFF4A285F,
            0xFFD8C5E8,
            0xFFA66CC8,
            0xFFBF8BE0,
            0xFF442654,
            0xFFF3E5FA,
            0xC8FFFFFF
    );

    static final Theme CHAOS_THEME = new Theme(
            0xFF351616,
            0xFFF3E5E2,
            0xFF682B27,
            0xFFE8CAC4,
            0xFFC65E50,
            0xFFE17A68,
            0xFF54211D,
            0xFFFFE9E3,
            0xC8FFFFFF
    );

    private AlchemicalJeiGraphics() {
    }

    static void drawBase(GuiGraphicsExtractor graphics, Theme theme, TextureRenderable mark) {
        drawPanelBackground(graphics, theme);
        drawAlchemyMark(graphics, theme, mark);
        drawPanelForeground(graphics, theme);
        drawArrows(graphics, theme.arrowColor());
    }

    static void drawSlotLabels(GuiGraphicsExtractor graphics, Font font, Theme theme) {
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.input"), INPUT_X + 8, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.catalyst"), CATALYST_X + 8, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.output"), OUTPUT_X + 8, 5, theme.headerTextColor());
    }

    static void drawCentered(GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y, int color) {
        graphics.text(font, text, centerX - font.width(text) / 2, y, color, false);
    }

    private static void drawPanelBackground(GuiGraphicsExtractor graphics, Theme theme) {
        graphics.fill(0, 0, WIDTH, HEIGHT, theme.outerColor());
        graphics.fill(1, 1, WIDTH - 1, HEIGHT - 1, theme.bodyColor());
    }

    private static void drawPanelForeground(GuiGraphicsExtractor graphics, Theme theme) {
        graphics.fill(2, 2, WIDTH - 2, HEADER_BOTTOM, theme.headerColor());
        graphics.fill(2, HEADER_BOTTOM - 1, WIDTH - 2, HEADER_BOTTOM, theme.accentColor());
        graphics.fill(2, INFO_TOP, WIDTH - 2, HEIGHT - 2, theme.infoColor());
        graphics.fill(2, INFO_TOP, WIDTH - 2, INFO_TOP + 1, theme.accentColor());
    }

    private static void drawAlchemyMark(GuiGraphicsExtractor graphics, Theme theme, TextureRenderable mark) {
        int centerX = CATALYST_X + 8;
        int centerY = SLOT_Y + 8;
        if (mark instanceof GuiTexture texture) {
            graphics.blit(
                    TextureOption.DEFAULT.renderPipeline(),
                    texture.identifier(),
                    centerX - ALCHEMY_BACKGROUND_SIZE / 2,
                    centerY - ALCHEMY_BACKGROUND_SIZE / 2,
                    0,
                    0,
                    ALCHEMY_BACKGROUND_SIZE,
                    ALCHEMY_BACKGROUND_SIZE,
                    texture.width(),
                    texture.height(),
                    texture.width(),
                    texture.height(),
                    theme.markColor()
            );
            return;
        }
        graphics.blitSprite(
                TextureOption.DEFAULT.renderPipeline(),
                mark.identifier(),
                centerX - ALCHEMY_BACKGROUND_SIZE / 2,
                centerY - ALCHEMY_BACKGROUND_SIZE / 2,
                ALCHEMY_BACKGROUND_SIZE,
                ALCHEMY_BACKGROUND_SIZE,
                theme.markColor()
        );
    }

    private static void drawArrows(GuiGraphicsExtractor graphics, int color) {
        drawArrow(graphics, 35, 63, color);
        drawArrow(graphics, 87, 115, color);
    }

    private static void drawArrow(GuiGraphicsExtractor graphics, int startX, int endX, int color) {
        graphics.fill(startX, 36, endX - 2, 38, color);
        graphics.fill(endX - 7, 32, endX - 5, 42, color);
        graphics.fill(endX - 5, 34, endX - 3, 40, color);
        graphics.fill(endX - 3, 36, endX, 38, color);
    }

    record Theme(
            int outerColor,
            int bodyColor,
            int headerColor,
            int infoColor,
            int accentColor,
            int arrowColor,
            int textColor,
            int headerTextColor,
            int markColor
    ) {
    }
}
