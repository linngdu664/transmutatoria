package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class GuiHandler {
    private static final int FRAME_SIZE = 22;
    private static final float RADIUS_RATE = 0.65f;

    public static void renderStorageBoxHud(GuiGraphicsExtractor guiGraphics, ItemStack boxStack) {
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int centerX = (int) (screenW * 0.5);
        int centerY = (int) (screenH * 0.5);
        int radius = (int) (screenH * 0.5 * RADIUS_RATE);

        int rotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians((i - rotation) * 30.0 - 90.0);
            int slotX = (int) (centerX + radius * Math.cos(angle));
            int slotY = (int) (centerY + radius * Math.sin(angle));

            int frameX = slotX - FRAME_SIZE / 2;
            int frameY = slotY - FRAME_SIZE / 2;
            BSFGuiTool.SIMPLE_FRAME_IMG.render(guiGraphics, frameX, frameY);

            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                int itemX = slotX - 8;
                int itemY = slotY - 8;
                guiGraphics.item(stack, itemX, itemY);
                guiGraphics.itemDecorations(mc.font, stack, itemX, itemY);
            }
        }
    }
}
