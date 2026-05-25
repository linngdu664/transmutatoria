package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.util.GuiSprite;
import com.linngdu664.transmutatoria.client.gui.util.GuiSubSprite;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.client.gui.util.V2I;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
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

import java.util.List;
import java.util.Set;

public class GuiHandler {
    // ===========贴图=============
    private static final GuiSprite SLOTS_SPRITE = new GuiSprite("hud/slots", 189, 61);
    private static final GuiSubSprite NORMAL_SLOT = new GuiSubSprite(SLOTS_SPRITE, 0, 0, 27, 27);
    private static final GuiSprite SLOT_SELECTED = new GuiSprite("hud/slot_selected", 29, 29);
    public static final GuiSprite SIMPLE_FRAME = new GuiSprite("hud/simple_frame", 22, 22);


    // =============源质选择器参数==============
    private static final int FRAME_SIZE = SIMPLE_FRAME.height();
    private static final float RADIUS_RATE_X = 0.4f;
    private static final float RADIUS_RATE_Y = 0.0f;
    // 平滑旋转速度，值越小动画越慢（指数衰减系数，单位: 1/tick）
    private static final float LERP_SPEED = 1.2f;
    // 近大远小缩放范围
    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 1.3f;
    // 深度遮罩最大透明度（12点钟最暗），不达到 0xFF
    private static final int MAX_OVERLAY_ALPHA = 0xc0;

    // 渲染用的连续旋转值，指数衰减插值逼近 unboundedTarget
    private static float smoothRotation = 0;
    // 独立累加的目标值（不依赖 smoothRotation），每步滚轮 ±1
    private static float unboundedTarget = 0;
    // 上一帧组件值，检测变化量
    private static int lastComponentRotation = 0;
    private static boolean initialized = false;

    // todo 如果后续确定椭圆短轴恒为0，可进一步优化
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

        int centerX = (int) (screenW * 0.5f);
        int centerY = (int) (screenH * 0.1f);
        int radiusX = (int) (screenW * 0.5f * RADIUS_RATE_X);
        int radiusY = (int) (screenH * 0.5f * RADIUS_RATE_Y);

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        // 第一阶段：预计算所有槽位的位置和缩放
        int[] slotXs = new int[12];
        int[] slotYs = new int[12];
        float[] scales = new float[12];
        float[] depths = new float[12];

        for (int i = 0; i < 12; i++) {
            float angle = ((i - smoothRotation) * 30.0f - 90.0f) * Mth.DEG_TO_RAD;
            slotXs[i] = (int) (centerX + radiusX * Mth.cos(angle));
            slotYs[i] = (int) (centerY + radiusY * Mth.sin(angle));
            // sin(angle): -1 at 12 o'clock (far), +1 at 6 o'clock (near)
            depths[i] = (Mth.sin(angle) + 1.0f) / 2.0f; // 最近深度为 1，最远深度为 0
            scales[i] = MIN_SCALE + (MAX_SCALE - MIN_SCALE) * depths[i];
        }

        // 第二阶段：直接生成渲染顺序（远→近，二渲三），无需排序
        int centerIdx = Math.floorMod(Math.round(smoothRotation), 12);
        int[] renderOrder = new int[12];
        int ri = 0;
        renderOrder[ri++] = centerIdx; // 12点钟（最远）
        for (int offset = 1; offset <= 5; offset++) {
            renderOrder[ri++] = Math.floorMod(centerIdx + offset, 12);
            renderOrder[ri++] = Math.floorMod(centerIdx - offset, 12);
        }
        renderOrder[ri] = Math.floorMod(centerIdx + 6, 12); // 6点钟（最近）

        // 第三阶段：按生成顺序渲染，每个槽位统一透视变换
        for (int idx = 0; idx < 12; idx++) {
            int i = renderOrder[idx];
            int slotX = slotXs[i];
            int slotY = slotYs[i];
            float scale = scales[i];

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(slotX, slotY);
            guiGraphics.pose().scale(scale, scale);

            // 槽位
            SIMPLE_FRAME.render(guiGraphics, TextureOption.DEFAULT, -FRAME_SIZE / 2, -FRAME_SIZE / 2);

            // 物品
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                guiGraphics.item(stack, -8, -8);
                guiGraphics.itemDecorations(mc.font, stack, -8, -8);
            }

            // 蒙版
            int overlayAlpha = (int) (MAX_OVERLAY_ALPHA * (1 - depths[i]));
            guiGraphics.fill(-FRAME_SIZE / 2, -FRAME_SIZE / 2, FRAME_SIZE / 2, FRAME_SIZE / 2, overlayAlpha << 24);

            guiGraphics.pose().popMatrix();
        }
    }

    public static void renderCrucibleCommonHud(GuiGraphicsExtractor guiGraphics, BlockEntity be) {
        if (be instanceof TransmutationCrucibleBlockEntity crucible) {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            // catalyst
            ItemStack catalyst = crucible.getCatalyst();
            V2I pos = SIMPLE_FRAME.renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.1, 0.2);
            guiGraphics.item(catalyst, pos.x() + 3, pos.y() + 3);

            // input
            pos = SIMPLE_FRAME.render(guiGraphics, TextureOption.DEFAULT, pos.x() + SIMPLE_FRAME.width(), pos.y());
            guiGraphics.item(crucible.getInput(), pos.x() + 3, pos.y() + 3);

            // output
            pos = SIMPLE_FRAME.renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.1, 0.8);
            guiGraphics.item(crucible.getOutput(), pos.x() + 3, pos.y() + 3);

            // essence slots
            if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
                // 源质融合的源质槽位
                Set<EssenceMetal> essences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains();
                int size = essences.size();

                // 先画普通槽
                int initX = GuiUtil.widthFrameCenter(window, NORMAL_SLOT.width()) - 10 * (size - 1);
                int initY = GuiUtil.heightFrameRatio(window, 0, 0.6);
                int x = initX;
                int y = initY;
                for (int i = 0; i < size; i++) {
                    NORMAL_SLOT.render(guiGraphics, TextureOption.DEFAULT, x, y);
                    x += 20;
                    y += ((i & 1) == 0) ? 12 : -12;
                }

                // 画高亮槽
                int selectedSlot = crucible.getSelectedSlot();
                SLOT_SELECTED.render(guiGraphics, TextureOption.DEFAULT, initX - 1 + 20 * selectedSlot, ((selectedSlot & 1) == 0) ? initY - 1 : initY + 11);

                // 再画源质
                x = initX;
                y = initY;
                List<ItemStack> inputEssences = crucible.getInputEssences();
                for (int i = 0; i < size; i++) {
                    ItemStack inputEssence = inputEssences.get(i);
                    if (!inputEssence.isEmpty()) {
                        guiGraphics.item(inputEssence, x + 6, y + 5);
                    } else {
                        // todo 画个虚的物品贴图上去
                    }
                    x += 20;
                    y += ((i & 1) == 0) ? 12 : -12;
                }
            } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                // 源质反应的源质槽位
                int y = GuiUtil.heightFrameRatio(window, NORMAL_SLOT.height(), 0.7);
                // 先画普通槽
                V2I pos1 = NORMAL_SLOT.renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, y);
                V2I pos2 = NORMAL_SLOT.renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, y + 24);

                // 画高亮槽
                int selectedSlot = crucible.getSelectedSlot();
                SLOT_SELECTED.renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, selectedSlot == 0 ? y - 1 : y + 23);

                // 再画源质
                List<ItemStack> inputEssences = crucible.isFinish() ? crucible.getOutputEssences() : crucible.getInputEssences();
                guiGraphics.item(inputEssences.get(0), pos1.x() + 6, pos1.y() + 5);
                guiGraphics.item(inputEssences.get(1), pos2.x() + 6, pos2.y() + 5);
            } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
                // 炼金复制/炼金分解的源质槽位
                // todo 重头戏
            }
        }
    }
}
