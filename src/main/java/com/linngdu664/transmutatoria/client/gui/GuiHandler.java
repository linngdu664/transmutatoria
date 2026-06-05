package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.util.TextureOption;
import com.linngdu664.transmutatoria.client.gui.util.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.util.V2I;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SafeInstance;
import com.mojang.blaze3d.platform.Window;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
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
    private static float targetHighlightX = 0;  // todo 字段可以被转换为一个局部变量？
    private static float targetHighlightY = 0;
    private static boolean highlightInitialized = false;

    // HUD入场动画
    private static float animationProgress = 0.0f;
    private static boolean animInitialized = false;

    public static void updateHudAnimation(boolean isVisible, DeltaTracker delta) {
        if (!animInitialized) {
            animationProgress = isVisible ? 1.0f : 0.0f;
            animInitialized = true;
            return;
        }
        float target = isVisible ? 1.0f : 0.0f;
        float diff = target - animationProgress;
        if (Math.abs(diff) > 0.001f) {
            float dt = delta.getGameTimeDeltaTicks();
            float t = 1f - (float) Math.exp(-LERP_SPEED * dt);
            animationProgress += diff * t;
            if (Math.abs(animationProgress - target) < 0.005f) {
                animationProgress = target;
            }
        }
    }

    // todo 如果后续确定椭圆短轴恒为0，可进一步优化
    public static void renderCrucibleStorageBoxHud(GuiGraphicsExtractor guiGraphics, ItemStack boxStack, DeltaTracker delta) {
        Minecraft mc = SafeInstance.getMC();

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
            depths[i] = (Mth.sin(angle) + 1.0f) / 2.0f; // 因此最近深度为 1，最远深度为 0
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
            Minecraft mc = SafeInstance.getMC();
            Window window = mc.getWindow();

            // 入场缩放动画
            if (animationProgress < 0.995f) {
                int sw = window.getGuiScaledWidth();
                int sh = window.getGuiScaledHeight();
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(sw / 2f, sh / 2f);
                guiGraphics.pose().scale(animationProgress, animationProgress);
                guiGraphics.pose().translate(-sw / 2f, -sh / 2f);
            }

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
            V2I polarityPos = GuiUtil.v2IRatio(window, 0.75, 0.2);
            guiGraphics.text(mc.font, "Polarity: " + crucible.getPolarity(), polarityPos.x(), polarityPos.y(), 0xffffffff);
            // 临时的进度显示
            guiGraphics.text(mc.font, "Progress: " + crucible.getProcessTimer() + "/" + crucible.getTargetTimer(), polarityPos.x(), polarityPos.y() + 9, 0xffffffff);

            drawBackground(guiGraphics, window, catalyst);

            long[] xys = null;
            if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
                // 源质融合的源质槽位
                List<EssenceMetal> essences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().stream().toList();
                List<ItemStack> inputEssences = crucible.getInputEssences();
                xys = calcPosEssenceMetal(window, essences.size());
                drawEssenceSlots(guiGraphics, xys, _ -> Textures.NORMAL_SLOT);
                drawEssences(guiGraphics, xys, slotIdx -> {
                    ItemStack inputEssence = inputEssences.get(slotIdx);
                    return inputEssence.isEmpty() ? essences.get(slotIdx).getDefaultTexture() : inputEssence;
                });
                drawSelectedSlot(guiGraphics, xys, crucible.getSelectedSlot(), delta);
            } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                // 源质反应的源质槽位
                List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
                xys = calcPosCrystal(window);
                drawEssenceSlots(guiGraphics, xys, _ -> Textures.NORMAL_SLOT);
                drawEssences(guiGraphics, xys, essencesInCrucible::get);
                drawSelectedSlot(guiGraphics, xys, crucible.getSelectedSlot(), delta);
            } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
                // 炼金复制/炼金分解的源质槽位
                List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
                if (alchemySlots.isEmpty()) return; // 预防措施，即使有东西搞炸了，也不要把客户端崩了
                List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();

                xys = calcPosScroll(window, alchemySlots);
                drawEssenceSlots(guiGraphics, xys, slotIdx -> alchemySlots.get(slotIdx).getTexture());
                drawEssences(guiGraphics, xys, slotIdx -> {
                    ItemStack essence = essencesInCrucible.get(slotIdx);
                    if (!essence.isEmpty()) return essence;
                    AbstractAlchemySlot alchemySlot = alchemySlots.get(slotIdx);
                    return alchemySlot.isShowEssence() ? alchemySlot.getEssenceMetal().getDefaultTexture() : null;
                });
                drawSelectedSlot(guiGraphics, xys, crucible.getSelectedSlot(), delta);

                // 画可能的箭头
                int crucibleMagicNumber = crucible.getCrucibleMagicNumber();
                for (int i = 0, size = alchemySlots.size(); i < size; i++) {
                    switch (alchemySlots.get(i).getDirection(AbstractAlchemySlot.getSlotMagicNumber(crucibleMagicNumber, i))) {
                        case 0 -> Textures.UP_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 11, getYFromPacked(xys[i]) - 2);
                        case 1 -> Textures.UPRIGHT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 21, getYFromPacked(xys[i]) + 5);
                        case 2 -> Textures.DOWNRIGHT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 21, getYFromPacked(xys[i]) + 17);
                        case 3 -> Textures.DOWN_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 11, getYFromPacked(xys[i]) + 24);
                        case 4 -> Textures.DOWNLEFT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 1, getYFromPacked(xys[i]) + 17);
                        case 5 -> Textures.UPLEFT_ARROW.render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(xys[i]) + 1, getYFromPacked(xys[i]) + 5);
                    }
                }
            }

            // 画数字
            Player player = mc.player;
            if (xys != null && player != null && player.isShiftKeyDown()) {
                drawNumbers(guiGraphics, mc.font, xys);
            }

            if (animationProgress < 0.995f) {
                guiGraphics.pose().popMatrix();
            }
        }
    }

    private static long[] calcPosEssenceMetal(Window window, int size) {
        long[] xys = new long[size];
        int x = GuiUtil.widthFrameCenter(window, Textures.NORMAL_SLOT.width()) - 10 * (size - 1);
        int y = GuiUtil.heightFrameRatio(window, 0, 0.6);
        for (int i = 0; i < size; i++) {
            xys[i] = packXY(x, y);
            x += 20;
            y += ((i & 1) == 0) ? 12 : -12;
        }
        return xys;
    }

    private static long[] calcPosCrystal(Window window) {
        int x = GuiUtil.widthFrameCenter(window, Textures.NORMAL_SLOT.width());
        int y = GuiUtil.heightFrameRatio(window, Textures.NORMAL_SLOT.height(), 0.6);
        return new long[]{packXY(x, y), packXY(x, y + 24)};
    }

    private static long[] calcPosScroll(Window window, List<AbstractAlchemySlot> alchemySlots) {
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

        // 计算并平移坐标
        long[] xys = new long[alchemySlots.size()];
        int i = 0;
        for (AbstractAlchemySlot alchemySlot : alchemySlots) {
            int x = initX + 20 * (alchemySlot.getX() - minX);
            int y = initY + 12 * (alchemySlot.getY() - minY);
            xys[i] = packXY(x, y);
            i++;
        }
        return xys;
    }
    private static void drawBackground(GuiGraphicsExtractor guiGraphics,Window window,ItemStack catalyst) {
        Textures.ALCHEMY_ARRAYS[Math.floorMod(catalyst.hashCode(), Textures.ALCHEMY_ARRAYS.length)].renderRatio(guiGraphics, TextureOption.DEFAULT, window, 0.5, 0.5);
    }


    private static void drawEssenceSlots(GuiGraphicsExtractor guiGraphics, long[] xys, Int2ObjectFunction<TextureRenderable> textureGetter) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            textureGetter.get(i).render(guiGraphics, TextureOption.DEFAULT, getXFromPacked(packed), getYFromPacked(packed));
        }
    }

    private static void drawEssences(GuiGraphicsExtractor guiGraphics, long[] xys, Int2ObjectFunction<Object> itemGetter) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            Object itemDraw = itemGetter.get(i);
            if (itemDraw instanceof ItemStack itemStack) {
                guiGraphics.item(itemStack, getXFromPacked(packed) + 6, getYFromPacked(packed) + 5);
            } else if (itemDraw instanceof TextureRenderable texture) {
                texture.render(guiGraphics, VIRTUAL_ITEM, getXFromPacked(packed) + 6, getYFromPacked(packed) + 5);
            }
        }
    }

    private static void drawSelectedSlot(GuiGraphicsExtractor guiGraphics, long[] xys, int selectedSlotIndex, DeltaTracker delta) {
        long packed = xys[selectedSlotIndex];
        float targetX = getXFromPacked(packed) - 1;
        float targetY = getYFromPacked(packed) - 1;

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

    private static void drawNumbers(GuiGraphicsExtractor guiGraphics, Font font, long[] xys) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            String str = String.valueOf(i + 1);
            guiGraphics.text(font, str, getXFromPacked(packed) + 14 - font.width(str) / 2, getYFromPacked(packed) + 10, 0xffffffff, true);
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
}
