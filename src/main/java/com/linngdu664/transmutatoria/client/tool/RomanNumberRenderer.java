package com.linngdu664.transmutatoria.client.tool;

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
        if (number < 1 || number > 39) return 0;
        int cursorX = x;
        String roman = toRomanNumber(number);
        for (int i = 0; i < roman.length(); i++) {
            GuiSubSprite sprite = getSprite(roman.charAt(i));
            sprite.render(guiGraphics, option, cursorX, y);
            cursorX += sprite.width() + SPACING;
        }
        return cursorX - x - SPACING;
    }

    public static int width(int number) {
        if (number < 1 || number > 39) return 0;
        int width = 0;
        String roman = toRomanNumber(number);
        for (int i = 0; i < roman.length(); i++) {
            width += getSprite(roman.charAt(i)).width();
        }
        return width + SPACING * (roman.length() - 1);
    }

    private static GuiSubSprite getSprite(char romanDigit) {
        return switch (romanDigit) {
            case 'I' -> Textures.ROMAN_I;
            case 'V' -> Textures.ROMAN_V;
            case 'X' -> Textures.ROMAN_X;
            default -> throw new IllegalStateException("Unexpected roman digit: " + romanDigit);
        };
    }

    /** Returns the Roman numeral text without drawing any digit sprites. */
    public static String toRomanNumber(int number) {
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
