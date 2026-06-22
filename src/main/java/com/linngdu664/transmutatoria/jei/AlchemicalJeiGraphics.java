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

    static final int RECIPE_WIDTH = 166;
    static final int RECIPE_HEIGHT = HEIGHT;
    static final int RECIPE_INPUT_X = 12;
    static final int RECIPE_INPUT_ESSENCE_X = 34;
    static final int RECIPE_CATALYST_X = 82;
    static final int RECIPE_OUTPUT_X = 132;
    static final int RECIPE_SLOT_Y = 29;
    static final int RECIPE_ESSENCE_SLOT_Y = RECIPE_SLOT_Y;

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

    static final Theme FUSION_THEME = new Theme(
            0xFF3B091C,
            0xFFFFE7EE,
            0xFF941943,
            0xFFF8B8CC,
            0xFFEA3C6C,
            0xFFFF5D85,
            0xFF68142F,
            0xFFFFEAF1,
            0xE8FFFFFF
    );

    private AlchemicalJeiGraphics() {
    }

    static void drawBase(GuiGraphicsExtractor graphics, Theme theme, TextureRenderable mark) {
        drawCustomBase(graphics, theme, mark, WIDTH, HEIGHT, CATALYST_X + 8, SLOT_Y + 8, INFO_TOP);
        drawArrows(graphics, theme.arrowColor());
    }

    static void drawRecipeBase(GuiGraphicsExtractor graphics, Theme theme, TextureRenderable mark) {
        int centerY = RECIPE_SLOT_Y + 8;
        drawCustomBase(
                graphics,
                theme,
                mark,
                RECIPE_WIDTH,
                RECIPE_HEIGHT,
                RECIPE_CATALYST_X + 8,
                centerY,
                INFO_TOP
        );
        drawArrow(graphics, 56, 79, centerY, theme.arrowColor());
        drawArrow(graphics, 104, 127, centerY, theme.arrowColor());
    }

    static void drawCustomBase(
            GuiGraphicsExtractor graphics,
            Theme theme,
            TextureRenderable mark,
            int width,
            int height,
            int markCenterX,
            int markCenterY,
            int infoTop
    ) {
        drawPanelBackground(graphics, theme, width, height);
        drawAlchemyMark(graphics, theme, mark, markCenterX, markCenterY);
        drawPanelForeground(graphics, theme, width, height, infoTop);
    }

    static void drawSlotLabels(GuiGraphicsExtractor graphics, Font font, Theme theme) {
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.input"), INPUT_X + 8, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.catalyst"), CATALYST_X + 8, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.output"), OUTPUT_X + 8, 5, theme.headerTextColor());
    }

    static void drawRecipeSlotLabels(GuiGraphicsExtractor graphics, Font font, Theme theme) {
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.input"), 31, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.catalyst"), RECIPE_CATALYST_X + 8, 5, theme.headerTextColor());
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.output"), RECIPE_OUTPUT_X + 8, 5, theme.headerTextColor());
    }

    static void drawCentered(GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y, int color) {
        graphics.text(font, text, centerX - font.width(text) / 2, y, color, false);
    }

    private static void drawPanelBackground(GuiGraphicsExtractor graphics, Theme theme, int width, int height) {
        graphics.fill(0, 0, width, height, theme.outerColor());
        graphics.fill(1, 1, width - 1, height - 1, theme.bodyColor());
    }

    private static void drawPanelForeground(GuiGraphicsExtractor graphics, Theme theme, int width, int height, int infoTop) {
        graphics.fill(2, 2, width - 2, HEADER_BOTTOM, theme.headerColor());
        graphics.fill(2, HEADER_BOTTOM - 1, width - 2, HEADER_BOTTOM, theme.accentColor());
        graphics.fill(2, infoTop, width - 2, height - 2, theme.infoColor());
        graphics.fill(2, infoTop, width - 2, infoTop + 1, theme.accentColor());
    }

    private static void drawAlchemyMark(
            GuiGraphicsExtractor graphics,
            Theme theme,
            TextureRenderable mark,
            int centerX,
            int centerY
    ) {
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
        int centerY = SLOT_Y + 8;
        drawArrow(graphics, 35, 63, centerY, color);
        drawArrow(graphics, 87, 115, centerY, color);
    }

    private static void drawArrow(GuiGraphicsExtractor graphics, int startX, int endX, int centerY, int color) {
        graphics.fill(startX, centerY - 1, endX - 2, centerY + 1, color);
        graphics.fill(endX - 7, centerY - 5, endX - 5, centerY + 5, color);
        graphics.fill(endX - 5, centerY - 3, endX - 3, centerY + 3, color);
        graphics.fill(endX - 3, centerY - 1, endX, centerY + 1, color);
    }

    static void drawArrowBetween(GuiGraphicsExtractor graphics, int startX, int endX, int centerY, int color) {
        drawArrow(graphics, startX, endX, centerY, color);
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
