package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class GuiHandler {
    private static final Identifier SIMPLE_FRAME =
            Identifier.fromNamespaceAndPath(ArsTransmutatoria.MODID, "textures/gui/simple_frame.png");
    private static final int FRAME_SIZE = 22;
    private static final int RADIUS = 50;

    public static void renderStorageBoxHud(GuiGraphicsExtractor guiGraphics, ItemStack boxStack) {

        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int centerX = (int) (screenW * 0.22);
        int centerY = (int) (screenH * 0.45);

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        guiGraphics.pose().pushMatrix();

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30.0 - 90.0);
            int slotX = (int) (centerX + RADIUS * Math.cos(angle));
            int slotY = (int) (centerY + RADIUS * Math.sin(angle));

            int frameX = slotX - FRAME_SIZE / 2;
            int frameY = slotY - FRAME_SIZE / 2;
            BSFGuiTool.SIMPLE_FRAME_IMG.render(guiGraphics,frameX,frameY);
//            guiGraphics.blitSprite(RenderPipelines.GUI, SIMPLE_FRAME, frameX, frameY, FRAME_SIZE, FRAME_SIZE);
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                int itemX = slotX - 8;
                int itemY = slotY - 8;
                guiGraphics.item(stack, itemX, itemY);
                guiGraphics.itemDecorations(mc.font, stack, itemX, itemY);
            }
        }

        guiGraphics.pose().popMatrix();
    }
}
