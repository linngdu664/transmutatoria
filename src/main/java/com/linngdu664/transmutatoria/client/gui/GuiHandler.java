package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.client.gui.util.V2I;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
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

public class GuiHandler {
    private static final TextureOption VIRTUAL_ITEM = TextureOption.withAlpha(48);

    // =============源质选择器参数==============
    private static final int FRAME_SIZE = Textures.SIMPLE_FRAME.height();
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

    // 选中槽位高亮平滑漂移动画
    private static float smoothHighlightX = 0;
    private static float smoothHighlightY = 0;
    private static float targetHighlightX = 0;
    private static float targetHighlightY = 0;
    private static boolean highlightInitialized = false;

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

        float centerX = screenW * 0.5f;
        float centerY = screenH * 0.1f;
        float radiusX = screenW * 0.5f * RADIUS_RATE_X;
        float radiusY = screenH * 0.5f * RADIUS_RATE_Y;

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        // 第一阶段：预计算所有槽位的位置和缩放
        long[] slotXYs = new long[12];
        float[] scales = new float[12];
        float[] depths = new float[12];

        for (int i = 0; i < 12; i++) {
            float angle = ((i - smoothRotation) * 30.0f - 90.0f) * Mth.DEG_TO_RAD;
            int slotX = (int) (centerX + radiusX * Mth.cos(angle));
            int slotY = (int) (centerY + radiusY * Mth.sin(angle));
            slotXYs[i] = packXY(slotX, slotY);
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
            long packed = slotXYs[i];
            float scale = scales[i];

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(getXFromPacked(packed), getYFromPacked(packed));
            guiGraphics.pose().scale(scale, scale);

            // 槽位
            Textures.SIMPLE_FRAME.render(guiGraphics, TextureOption.DEFAULT, -FRAME_SIZE / 2, -FRAME_SIZE / 2);

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

    public static void renderCrucibleCommonHud(GuiGraphicsExtractor guiGraphics, BlockEntity be, DeltaTracker delta) {
        if (be instanceof TransmutationCrucibleBlockEntity crucible) {
            Minecraft mc = Minecraft.getInstance();
            Window window = mc.getWindow();

            // catalyst
            ItemStack catalyst = crucible.getCatalyst();
            V2I pos = Textures.SIMPLE_FRAME.renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.1, 0.2);
            guiGraphics.item(catalyst, pos.x() + 3, pos.y() + 3);

            // input
            Textures.SIMPLE_FRAME.render(guiGraphics, TextureOption.DEFAULT, pos.x() + Textures.SIMPLE_FRAME.width(), pos.y());
            guiGraphics.item(crucible.getInput(), pos.x() + Textures.SIMPLE_FRAME.width() + 3, pos.y() + 3);

            // output
            pos = Textures.SIMPLE_FRAME.renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.1, 0.8);
            guiGraphics.item(crucible.getOutput(), pos.x() + 3, pos.y() + 3);

            // 临时的极性显示
            V2I polarityPos = GuiUtil.v2IRatio(window, 0.8, 0.2);
            guiGraphics.text(mc.font, String.valueOf(crucible.getPolarity()), polarityPos.x(), polarityPos.y(), 0xffffffff);

            // essence slots
            // todo 数字
            if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
                // 源质融合的源质槽位
                List<EssenceMetal> essences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().stream().toList();
                int size = essences.size();

                // 先画普通槽
                int initX = GuiUtil.widthFrameCenter(window, Textures.NORMAL_SLOT.width()) - 10 * (size - 1);
                int initY = GuiUtil.heightFrameRatio(window, 0, 0.6);
                int x = initX;
                int y = initY;
                for (int i = 0; i < size; i++) {
                    Textures.NORMAL_SLOT.render(guiGraphics, TextureOption.DEFAULT, x, y);
                    x += 20;
                    y += ((i & 1) == 0) ? 12 : -12;
                }

                // 再画源质
                x = initX;
                y = initY;
                List<ItemStack> inputEssences = crucible.getInputEssences();
                for (int i = 0; i < size; i++) {
                    ItemStack inputEssence = inputEssences.get(i);
                    if (!inputEssence.isEmpty()) {
                        guiGraphics.item(inputEssence, x + 6, y + 5);
                    } else {
                        essences.get(i).getDefaultTexture().render(guiGraphics, VIRTUAL_ITEM, x + 6, y + 5);
                    }
                    x += 20;
                    y += ((i & 1) == 0) ? 12 : -12;
                }

                // 画高亮槽（平滑漂移）
                int selectedSlot = crucible.getSelectedSlot();
                float targetX = initX - 1 + 20 * selectedSlot;
                float targetY = ((selectedSlot & 1) == 0) ? initY - 1 : initY + 11;
                renderSmoothSelectedSlot(guiGraphics, targetX, targetY, delta);
            } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                // 源质反应的源质槽位
                int y = GuiUtil.heightFrameRatio(window, Textures.NORMAL_SLOT.height(), 0.7);
                // 先画普通槽
                V2I pos1 = Textures.NORMAL_SLOT.renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, y);
                V2I pos2 = Textures.NORMAL_SLOT.renderHorizontalCenter(guiGraphics, TextureOption.DEFAULT, window, y + 24);

                // 再画源质
                List<ItemStack> inputEssences = crucible.isFinish() ? crucible.getOutputEssences() : crucible.getInputEssences();
                guiGraphics.item(inputEssences.get(0), pos1.x() + 6, pos1.y() + 5);
                guiGraphics.item(inputEssences.get(1), pos2.x() + 6, pos2.y() + 5);

                // 画高亮槽（平滑漂移）
                int selectedSlot = crucible.getSelectedSlot();
                float targetX = GuiUtil.widthFrameCenter(window, Textures.SLOT_SELECTED.width());
                float targetY = selectedSlot == 0 ? y - 1 : y + 23;
                renderSmoothSelectedSlot(guiGraphics, targetX, targetY, delta);
            } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
                Textures.ALCHEMY_ARRAYS[Math.floorMod(catalyst.hashCode(), Textures.ALCHEMY_ARRAYS.length)].renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.5, 0.5);
                // 炼金复制/炼金分解的源质槽位
                List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
                if (alchemySlots.isEmpty()) {
                    return;
                }
                int size = alchemySlots.size();

                // 确定 XY 范围
                int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
                for (AbstractAlchemySlot slot : alchemySlots) {
                    minX = Math.min(slot.getX(), minX);
                    maxX = Math.max(slot.getX(), maxX);
                    minY = Math.min(slot.getY(), minY);
                    maxY = Math.max(slot.getY(), maxY);
                }
                V2I origin = GuiUtil.v2IRatio(window, Textures.NORMAL_SLOT.width(), Textures.NORMAL_SLOT.height(), 0.5, 0.6);
                int initX = origin.x() - 10 * (maxX - minX);
                int initY = origin.y() - 6 * (maxY - minY);

                // 先算坐标
                long[] slotXYs = new long[size];
                int i = 0;
                for (AbstractAlchemySlot alchemySlot : alchemySlots) {
                    int x = initX + 20 * (alchemySlot.getX() - minX);
                    int y = initY + 12 * (alchemySlot.getY() - minY);
                    slotXYs[i] = packXY(x, y);
                    i++;
                }

                // 先画普通槽
                i = 0;
                for (AbstractAlchemySlot alchemySlot : alchemySlots) {
                    long packed = slotXYs[i];
                    alchemySlot.getTexture().render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(packed), getYFromPacked(packed));
                    i++;
                }

                // 再画源质
                i = 0;
                List<ItemStack> essencesInCrucible = crucible.isFinish() ? crucible.getOutputEssences() : crucible.getInputEssences();
                for (AbstractAlchemySlot alchemySlot : alchemySlots) {
                    ItemStack essence = essencesInCrucible.get(i);
                    if (!essence.isEmpty()) {
                        guiGraphics.item(essence, getXFromPacked(slotXYs[i]) + 6, getYFromPacked(slotXYs[i]) + 5);
                    } else if (alchemySlot.isShowEssence()) {
                        alchemySlot.getEssenceMetal().getDefaultTexture().render(guiGraphics, VIRTUAL_ITEM, getXFromPacked(slotXYs[i]) + 6, getYFromPacked(slotXYs[i]) + 5);
                    }
                    i++;
                }
                // 画高亮槽（平滑漂移）
                long selectedPacked = slotXYs[crucible.getSelectedSlot()];
                float targetX = getXFromPacked(selectedPacked) - 1;
                float targetY = getYFromPacked(selectedPacked) - 1;
                renderSmoothSelectedSlot(guiGraphics, targetX, targetY, delta);

                // 画可能的箭头
                i = 0;
                int crucibleMagicNumber = crucible.getCrucibleMagicNumber();
                for (AbstractAlchemySlot alchemySlot : alchemySlots) {
                    switch (alchemySlot.getDirection(AbstractAlchemySlot.getSlotMagicNumber(crucibleMagicNumber, i))) {
                        case 0 -> Textures.UP_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 11, getYFromPacked(slotXYs[i]) - 2);
                        case 1 -> Textures.UPRIGHT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 21, getYFromPacked(slotXYs[i]) + 5);
                        case 2 -> Textures.DOWNRIGHT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 21, getYFromPacked(slotXYs[i]) + 17);
                        case 3 -> Textures.DOWN_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 11, getYFromPacked(slotXYs[i]) + 24);
                        case 4 -> Textures.DOWNLEFT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 1, getYFromPacked(slotXYs[i]) + 17);
                        case 5 -> Textures.UPLEFT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(slotXYs[i]) + 1, getYFromPacked(slotXYs[i]) + 5);
                    }
                    i++;
                }
            }
        }
    }

    private static long packXY(int x, int y) {
        return ((long) y << 32) | (x & 0xffffffffL);
    }

    private static int getXFromPacked(long packed) {
        return (int) (packed & 0xffffffffL);
    }

    private static int getYFromPacked(long packed) {
        return (int) (packed >> 32);
    }

    private static void renderSmoothSelectedSlot(GuiGraphicsExtractor guiGraphics, float targetX, float targetY, DeltaTracker delta) {
        if (!highlightInitialized) {
            smoothHighlightX = targetX;
            smoothHighlightY = targetY;
            targetHighlightX = targetX;
            targetHighlightY = targetY;
            highlightInitialized = true;
        }

        targetHighlightX = targetX;
        targetHighlightY = targetY;

        float dt = delta.getGameTimeDeltaTicks();
        float diffX = targetHighlightX - smoothHighlightX;
        float diffY = targetHighlightY - smoothHighlightY;
        if (Math.abs(diffX) > 0.01f || Math.abs(diffY) > 0.01f) {
            float t = 1f - (float) Math.exp(-LERP_SPEED * dt);
            smoothHighlightX += diffX * t;
            smoothHighlightY += diffY * t;
        }

        Textures.SLOT_SELECTED.render(guiGraphics, TextureOption.DEFAULT, Math.round(smoothHighlightX), Math.round(smoothHighlightY));
    }
}
