package com.linngdu664.transmutatoria.client.tool;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.texture.GuiSubSprite;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class RomanNumberRenderer {
    private static final int SPACING = 1;

    private RomanNumberRenderer() {
    }

    public static int render(GuiGraphicsExtractor guiGraphics, int number, int x, int y) {
        return render(guiGraphics, TextureOption.DEFAULT, number, x, y);
    }

    public static int render(GuiGraphicsExtractor guiGraphics, TextureOption option, int number, int x, int y) {
        try {
            return renderUnchecked(guiGraphics, option, number, x, y);
        } catch (RuntimeException exception) {
            ArsTransmutatoria.LOGGER.error("Failed to render roman number {}", number, exception);
            return 0;
        }
    }

    public static int width(int number) {
        try {
            return widthUnchecked(number);
        } catch (RuntimeException exception) {
            ArsTransmutatoria.LOGGER.error("Failed to calculate roman number width {}", number, exception);
            return 0;
        }
    }

    private static int renderUnchecked(GuiGraphicsExtractor guiGraphics, TextureOption option, int number, int x, int y) {
        validateNumber(number);

        int cursorX = x;
        String roman = toRomanNumber(number);
        for (int i = 0; i < roman.length(); i++) {
            GuiSubSprite sprite = getSprite(roman.charAt(i));
            sprite.render(guiGraphics, option, cursorX, y);
            cursorX += sprite.width() + SPACING;
        }
        return cursorX - x - SPACING;
    }

    private static int widthUnchecked(int number) {
        validateNumber(number);

        int width = 0;
        String roman = toRomanNumber(number);
        for (int i = 0; i < roman.length(); i++) {
            width += getSprite(roman.charAt(i)).width();
        }
        return width + SPACING * (roman.length() - 1);
    }

    private static void validateNumber(int number) {
        if (number < 1 || number > 39) {
            throw new IllegalArgumentException("Roman number must be in range 1..39 when only I, V and X are available: " + number);
        }
    }

    private static GuiSubSprite getSprite(char romanDigit) {
        return switch (romanDigit) {
            case 'I' -> Textures.ROMAN_I;
            case 'V' -> Textures.ROMAN_V;
            case 'X' -> Textures.ROMAN_X;
            default -> throw new IllegalStateException("Unexpected roman digit: " + romanDigit);
        };
    }

    private static String toRomanNumber(int number) {
        StringBuilder builder = new StringBuilder();
        while (number >= 10) {
            builder.append('X');
            number -= 10;
        }
        if (number == 9) {
            return builder.append("IX").toString();
        }
        if (number >= 5) {
            builder.append('V');
            number -= 5;
        } else if (number == 4) {
            return builder.append("IV").toString();
        }
        while (number > 0) {
            builder.append('I');
            number--;
        }
        return builder.toString();
    }
}
