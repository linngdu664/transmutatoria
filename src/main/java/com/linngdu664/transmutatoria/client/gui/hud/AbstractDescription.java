package com.linngdu664.transmutatoria.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public abstract class AbstractDescription implements HudComponent {
    private static final int PADDING = 6;
    private static final int MAX_WIDTH = 132;
    private static final int LINE_HEIGHT = 9;
    private static final int TITLE_GAP = 4;
    private static final int BG_COLOR = 0xb0181116;
    private static final int BORDER_COLOR = 0xc0d6b47b;
    private static final int TITLE_COLOR = 0xfff2d79a;
    private static final int TEXT_COLOR = 0xffe2ddd0;
    private static final int HINT_BORDER_COLOR = 0xc0d95757;
    private static final int HINT_TITLE_COLOR = 0xffff7777;
    private static final int HINT_TEXT_COLOR = 0xffffb0b0;

    private boolean isWarning;
    private Component title;
    private List<FormattedCharSequence> descriptionLines;
    private int x;
    private int y;
    private int panelWidth;
    private int panelHeight;

    abstract protected int calcX(int panelWidth);
    abstract protected int calcY(int panelHeight);

    @Override
    public final void render(GuiGraphicsExtractor guiGraphics) {
        if (title == null && descriptionLines == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        guiGraphics.fill(x, y, x + panelWidth, y + panelHeight, BG_COLOR);
        int borderColor = isWarning ? HINT_BORDER_COLOR : BORDER_COLOR;
        int titleColor = isWarning ? HINT_TITLE_COLOR : TITLE_COLOR;
        int textColor = isWarning ? HINT_TEXT_COLOR : TEXT_COLOR;
        guiGraphics.outline(x, y, panelWidth, panelHeight, borderColor);

        int lineY = y + PADDING;
        if (title != null) {
            guiGraphics.text(font, title, x + PADDING, y + PADDING, titleColor, true);
            lineY += LINE_HEIGHT + TITLE_GAP;
        }

        if (descriptionLines != null) {
            for (FormattedCharSequence line : descriptionLines) {
                guiGraphics.text(font, line, x + PADDING, lineY, textColor, true);
                lineY += LINE_HEIGHT;
            }
        }
    }

    protected final void setState(Component title, Component description, boolean isWarning) {
        this.title = title;
        this.isWarning = isWarning;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int contentWidth = title != null ? Math.min(font.width(title), MAX_WIDTH) : 0;
        if (description != null) {
            descriptionLines = font.split(description, MAX_WIDTH);
            for (FormattedCharSequence line : descriptionLines) {
                contentWidth = Math.max(contentWidth, font.width(line));
            }
        } else {
            descriptionLines = null;
        }

        if (title == null && description == null) {
            return;
        }

        panelWidth = contentWidth + PADDING * 2;
        panelHeight = PADDING * 2;
        if (title != null) {
            panelHeight += LINE_HEIGHT + TITLE_GAP;
        }
        if (description != null) {
            panelHeight += descriptionLines.size() * LINE_HEIGHT;
        }

        x = calcX(panelWidth);
        y = calcY(panelHeight);
    }
}
