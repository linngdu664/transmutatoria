package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.block.entity.BlockEntityTransmutationCrucible;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GuiHandler {
    private static final int FRAME_SIZE = 22;
    private static final float RADIUS_RATE_X = 0.78f;
    private static final float RADIUS_RATE_Y = 0.52f;
    // 平滑旋转速度，值越小动画越慢（指数衰减系数，单位: 1/tick）
    private static final float LERP_SPEED = 1.2f;

    // 渲染用的连续旋转值，指数衰减插值逼近 unboundedTarget
    private static float smoothRotation = 0;
    // 独立累加的目标值（不依赖 smoothRotation），每步滚轮 ±1
    private static float unboundedTarget = 0;
    // 上一帧组件值，检测变化量
    private static int lastComponentRotation = 0;
    private static boolean initialized = false;

    public static void renderCrucibleStorageBoxHud(GuiGraphicsExtractor guiGraphics, ItemStack boxStack, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();

        int componentRotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);

        if (!initialized) {
            smoothRotation = componentRotation;
            unboundedTarget = componentRotation;
            lastComponentRotation = componentRotation;
            initialized = true;
        }

        // 检测组件变化，独立累加目标值
        if (componentRotation != lastComponentRotation) {
            int delta2 = componentRotation - lastComponentRotation;
            if (delta2 > 6) delta2 -= 12;
            else if (delta2 < -6) delta2 += 12;
            unboundedTarget += delta2;
            lastComponentRotation = componentRotation;
        }

        // 指数衰减插值：smoothRotation 向 unboundedTarget 平滑逼近
        float dt = delta.getGameTimeDeltaTicks();
        float diff = unboundedTarget - smoothRotation;
        if (Math.abs(diff) > 0.001f) {
            float t = 1f - (float) Math.exp(-LERP_SPEED * dt);
            smoothRotation += diff * t;
            if (Math.abs(smoothRotation - unboundedTarget) < 0.01f) {
                smoothRotation = unboundedTarget;
            }
        }

        // 防止浮点精度漂移：同时平移两者，保持差值不变
        if (Math.abs(smoothRotation) > 12f) {
            float shift = 12f * Math.round(smoothRotation / 12f);
            smoothRotation -= shift;
            unboundedTarget -= shift;
        }

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        int centerX = (int) (screenW * 0.5);
        int centerY = (int) (screenH * 0.3);
        int radiusX = (int) (screenH * 0.5 * RADIUS_RATE_X);
        int radiusY = (int) (screenH * 0.5 * RADIUS_RATE_Y);

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        // 底部槽位（6点钟方向）放大1.3倍作为选中效果
        int bottomSlot = Math.floorMod(6 + Math.round(smoothRotation), 12);

        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians((i - smoothRotation) * 30.0 - 90.0);
            int slotX = (int) (centerX + radiusX * Mth.cos(angle));
            int slotY = (int) (centerY + radiusY * Mth.sin(angle));

            boolean isBottom = i == bottomSlot;
            if (isBottom) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(slotX, slotY);
                guiGraphics.pose().scale(1.3f, 1.3f);
            }

            int frameX = (isBottom ? -FRAME_SIZE / 2 : slotX - FRAME_SIZE / 2);
            int frameY = (isBottom ? -FRAME_SIZE / 2 : slotY - FRAME_SIZE / 2);
            BSFGuiTool.SIMPLE_FRAME_IMG.render(guiGraphics, frameX, frameY);

            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                int itemX = isBottom ? -8 : slotX - 8;
                int itemY = isBottom ? -8 : slotY - 8;
                guiGraphics.item(stack, itemX, itemY);
                guiGraphics.itemDecorations(mc.font, stack, itemX, itemY);
            }

            if (isBottom) {
                guiGraphics.pose().popMatrix();
            }
        }
    }

    public static void renderCrucibleCommonHud(GuiGraphicsExtractor guiGraphics, BlockEntity be) {
        if (be instanceof BlockEntityTransmutationCrucible crucible) {
            Minecraft mc = Minecraft.getInstance();
            // catalyst
            Window window = mc.getWindow();
            V2I pos = BSFGuiTool.SIMPLE_FRAME_IMG.renderRatio(guiGraphics, window, 0.1, 0.2);
            guiGraphics.item(crucible.getCatalyst(), pos.x() + 3, pos.y() + 3);

            // output
            pos = BSFGuiTool.SIMPLE_FRAME_IMG.renderRatio(guiGraphics, window, 0.1, 0.8);
            guiGraphics.item(crucible.getOutput(), pos.x() + 3, pos.y() + 3);

            // input
            pos = BSFGuiTool.SIMPLE_FRAME_IMG.renderRatio(guiGraphics, window, 0.9, 0.8);
            guiGraphics.item(crucible.getInput(), pos.x() + 3, pos.y() + 3);
        }

    }
}
