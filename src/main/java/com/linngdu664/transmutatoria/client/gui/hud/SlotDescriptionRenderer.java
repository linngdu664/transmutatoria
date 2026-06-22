package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.SlotType;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.List;

final class SlotDescriptionRenderer {
    private static final int MARGIN = 20;
    private static final int PADDING = 6;
    private static final int MAX_WIDTH = 130;
    private static final int LINE_HEIGHT = 9;
    private static final int TITLE_GAP = 4;
    private static final int BG_COLOR = 0xb0181116;
    private static final int BORDER_COLOR = 0xc0d6b47b;
    private static final int TITLE_COLOR = 0xfff2d79a;
    private static final int TEXT_COLOR = 0xffe2ddd0;
    private static final int HINT_BORDER_COLOR = 0xc0d95757;
    private static final int HINT_TITLE_COLOR = 0xffff7777;
    private static final int HINT_TEXT_COLOR = 0xffffb0b0;

    private SlotDescriptionRenderer() {
    }

    static void render(
            GuiGraphicsExtractor guiGraphics,
            Window window,
            Font font,
            ItemStack catalyst,
            List<AbstractAlchemySlot> alchemySlots,
            TransmutationCrucibleBlockEntity crucible
    ) {
        if (crucible.getTargetTimer() == 0 && !crucible.hasAnyOutput()) {
            if (crucible.getWaterAmount() < TransmutationCrucibleBlockEntity.WATER_PER_REACTION) {
                drawPanel(
                        guiGraphics, window, font,
                        Component.translatable("gui.transmutatoria.crucible_hint.water.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.water.description", TransmutationCrucibleBlockEntity.WATER_PER_REACTION),
                        true
                );
                return;
            }
            if (catalyst.isEmpty()) {
                drawPanel(
                        guiGraphics, window, font,
                        Component.translatable("gui.transmutatoria.crucible_hint.catalyst.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.catalyst.description"),
                        true
                );
                return;
            }
            if (crucible.getInput().isEmpty() && requiresTransformationInput(catalyst)) {
                drawPanel(
                        guiGraphics, window, font,
                        Component.translatable("gui.transmutatoria.crucible_hint.input.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.input.description"),
                        true
                );
                return;
            }
        }

        if (!(catalyst.getItem() instanceof AbstractTransmutationScrollItem) || alchemySlots.isEmpty()) {
            return;
        }

        int selectedSlotIndex = crucible.getSelectedSlot();
        if (selectedSlotIndex < 0 || selectedSlotIndex >= alchemySlots.size()) {
            return;
        }

        AbstractAlchemySlot slot = alchemySlots.get(selectedSlotIndex);
        SlotType slotType = slot.getType();
        if (!slot.isShowType() || slotType == SlotType.NORMAL) {
            return;
        }

        String slotKey = slotType.getSerializedName();
        Component title = Component.translatable("gui.transmutatoria.alchemy_slot." + slotKey);
        Component description = Component.translatable("gui.transmutatoria.alchemy_slot.description." + slotKey);
        drawPanel(guiGraphics, window, font, title, description, false);
    }

    private static boolean requiresTransformationInput(ItemStack catalyst) {
        if (catalyst.is(Items.ENDER_EYE) || catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            return true;
        }
        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            return container.getSlots() > 0 && !container.getStackInSlot(0).isEmpty();
        }
        return false;
    }

    private static void drawPanel(
            GuiGraphicsExtractor guiGraphics,
            Window window,
            Font font,
            Component title,
            Component description,
            boolean isWarning
    ) {
        int availableContentWidth = window.getGuiScaledWidth() - (MARGIN + PADDING) * 2;
        if (availableContentWidth <= 0) {
            return;
        }

        int contentMaxWidth = Math.min(MAX_WIDTH, availableContentWidth);
        List<FormattedCharSequence> descriptionLines = font.split(description, contentMaxWidth);
        int contentWidth = Math.min(font.width(title), contentMaxWidth);
        for (FormattedCharSequence line : descriptionLines) {
            contentWidth = Math.max(contentWidth, font.width(line));
        }

        int panelWidth = contentWidth + PADDING * 2;
        int panelHeight = PADDING * 2 + LINE_HEIGHT + TITLE_GAP + descriptionLines.size() * LINE_HEIGHT;
        int x = window.getGuiScaledWidth() - panelWidth - MARGIN;
        int y = window.getGuiScaledHeight() - panelHeight - MARGIN;

        guiGraphics.fill(x, y, x + panelWidth, y + panelHeight, BG_COLOR);
        int borderColor = isWarning ? HINT_BORDER_COLOR : BORDER_COLOR;
        int titleColor = isWarning ? HINT_TITLE_COLOR : TITLE_COLOR;
        int textColor = isWarning ? HINT_TEXT_COLOR : TEXT_COLOR;
        guiGraphics.outline(x, y, panelWidth, panelHeight, borderColor);
        guiGraphics.text(font, title, x + PADDING, y + PADDING, titleColor, true);

        int lineY = y + PADDING + LINE_HEIGHT + TITLE_GAP;
        for (FormattedCharSequence line : descriptionLines) {
            guiGraphics.text(font, line, x + PADDING, lineY, textColor, true);
            lineY += LINE_HEIGHT;
        }
    }
}
