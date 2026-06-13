package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.texture.GuiTexture;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.tool.Easing;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.SafeInstance;
import com.linngdu664.transmutatoria.util.V2I;
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

    private static final StorageBoxHudStyle STORAGE_BOX_STYLE = new StorageBoxHudStyle(
            Textures.SIMPLE_FRAME.height(),
            0.4f,
            0.0f,
            0.5f,
            1.3f,
            0xc0
    );
    private static final RingRotationState storageBoxRotation = new RingRotationState();
    private static final SmoothPoint selectedSlotHighlight = new SmoothPoint();
    private static final SmoothValue hudIntro = new SmoothValue();
    private static final SmoothValue dashboardPolarity = new SmoothValue();

    private static final float LERP_SPEED = 1.2f;
    private static final float EASE_DURATION_TICKS = 6.931472f / LERP_SPEED;
    private static final float SLOT_CLICK_PRESS_TICKS = 3.0f;
    private static final float SLOT_CLICK_RELEASE_TICKS = 7.0f;
    private static final float SLOT_CLICK_MIN_SCALE = 0.78f;
    private static final float DASHBOARD_MAX_POLARITY = 50.0f;
    private static final float DASHBOARD_MAX_POINTER_DEGREES = 90.0f;

    public static void updateHudAnimation(boolean isVisible, DeltaTracker delta) {
        float target = isVisible ? 1.0f : 0.0f;
        hudIntro.moveTo(target, delta, 0.005f);
    }

    // todo 如果后续确定椭圆短轴恒为0，可进一步优化
    public static void renderCrucibleStorageBoxHud(GuiGraphicsExtractor guiGraphics, ItemStack boxStack, DeltaTracker delta) {
        Minecraft mc = SafeInstance.getMC();

        int componentRotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
        float smoothRotation = storageBoxRotation.update(componentRotation, delta);

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        float centerX = screenW * 0.5f;
        float centerY = screenH * 0.1f;
        float radiusX = screenW * 0.5f * STORAGE_BOX_STYLE.radiusRateX();
        float radiusY = screenH * 0.5f * STORAGE_BOX_STYLE.radiusRateY();
        int frameSize = STORAGE_BOX_STYLE.frameSize();

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
            scales[i] = STORAGE_BOX_STYLE.minScale()
                    + (STORAGE_BOX_STYLE.maxScale() - STORAGE_BOX_STYLE.minScale()) * depths[i];
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
            Textures.SIMPLE_FRAME.render(guiGraphics, -frameSize / 2, -frameSize / 2);

            // 物品
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                guiGraphics.item(stack, -8, -8);
                guiGraphics.itemDecorations(mc.font, stack, -8, -8);
            }

            // 蒙版
            int overlayAlpha = (int) (STORAGE_BOX_STYLE.maxOverlayAlpha() * (1 - depths[i]));
            guiGraphics.fill(
                    -frameSize / 2,
                    -frameSize / 2,
                    frameSize / 2,
                    frameSize / 2,
                    overlayAlpha << 24
            );

            guiGraphics.pose().popMatrix();
        }
    }

    public static void renderCrucibleCommonHud(GuiGraphicsExtractor guiGraphics, BlockEntity be, DeltaTracker delta) {
        if (be instanceof TransmutationCrucibleBlockEntity crucible) {
            Minecraft mc = SafeInstance.getMC();
            Window window = mc.getWindow();

            // 入场缩放动画
            if (hudIntro.value() < 0.995f) {
                int sw = window.getGuiScaledWidth();
                int sh = window.getGuiScaledHeight();
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(sw / 2f, sh / 2f);
                guiGraphics.pose().scale(hudIntro.value(), hudIntro.value());
                guiGraphics.pose().translate(-sw / 2f, -sh / 2f);
            }

            // catalyst
            ItemStack catalyst = crucible.getCatalyst();
            V2I pos = Textures.SIMPLE_FRAME.renderRatio(guiGraphics, window, 0.1, 0.2);
            guiGraphics.item(catalyst, pos.x() + 3, pos.y() + 3);

            // input
            Textures.SIMPLE_FRAME.render(guiGraphics, pos.x() + Textures.SIMPLE_FRAME.width(), pos.y());
            guiGraphics.item(crucible.getInput(), pos.x() + Textures.SIMPLE_FRAME.width() + 3, pos.y() + 3);

            // output
            pos = Textures.SIMPLE_FRAME.renderRatio(guiGraphics, window, 0.1, 0.8);
            guiGraphics.item(crucible.getOutput(), pos.x() + 3, pos.y() + 3);

            // 临时的极性显示
            drawDashboard(guiGraphics, window, crucible, catalyst, delta);

            drawBackground(guiGraphics, window, catalyst);

            long[] xys = null;
            if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
                // 源质融合的源质槽位
                List<EssenceMetal> essences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().stream().toList();
                List<ItemStack> inputEssences = crucible.getInputEssences();
                xys = calcPosEssenceMetal(window, essences.size());
                drawEssenceSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, _ -> Textures.NORMAL_SLOT, slotIdx -> {
                    ItemStack inputEssence = inputEssences.get(slotIdx);
                    return inputEssence.isEmpty() ? essences.get(slotIdx).getDefaultTexture() : inputEssence;
                });
            } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                // 源质反应的源质槽位
                List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
                xys = calcPosCrystal(window);
                drawEssenceSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, _ -> Textures.NORMAL_SLOT, essencesInCrucible::get);
            } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
                // 炼金复制/炼金分解的源质槽位
                List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
                if (alchemySlots.isEmpty()) return; // Prevent malformed client-side scroll data from crashing the HUD.
                List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();

                xys = calcPosScroll(window, alchemySlots);
                drawEssenceSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, slotIdx -> alchemySlots.get(slotIdx).getTexture(), slotIdx -> {
                    ItemStack essence = essencesInCrucible.get(slotIdx);
                    if (!essence.isEmpty()) return essence;
                    AbstractAlchemySlot alchemySlot = alchemySlots.get(slotIdx);
                    return alchemySlot.isShowEssence() ? alchemySlot.getEssenceMetal().getDefaultTexture() : null;
                });
                drawArrow(guiGraphics, xys, alchemySlots, catalyst.getOrDefault(InitDataComponents.MAGIC_NUMBER, 0), crucible);
            }

            // 画数字
            Player player = mc.player;
            if (xys != null && player != null && player.isShiftKeyDown()) {
                drawNumbers(guiGraphics, mc.font, xys);
            }

            if (hudIntro.value() < 0.995f) {
                guiGraphics.pose().popMatrix();
            }
        }
    }

    private static void drawDashboard(GuiGraphicsExtractor guiGraphics, Window window, TransmutationCrucibleBlockEntity crucible, ItemStack catalyst, DeltaTracker delta) {
        V2I center = GuiUtil.v2IRatio(window, 0.85, 0.2);
        float centerX = center.x();
        float centerY = center.y();

        Textures.DASHBOARD_HOURGLASS_BG.render(
                guiGraphics,
                Math.round(centerX - Textures.DASHBOARD_HOURGLASS_BG.width() / 2.0f),
                Math.round(centerY - Textures.DASHBOARD_HOURGLASS_BG.height() / 2.0f)
        );
        drawHourglassLiquid(guiGraphics, centerX, centerY, getScrollExpireRemainingRatio(catalyst, delta));

        Textures.DASHBOARD_BG.render(
                guiGraphics,
                Math.round(centerX - Textures.DASHBOARD_BG.width() / 2.0f),
                Math.round(centerY - Textures.DASHBOARD_BG.height() / 2.0f)
        );

        RecipeConditions conditions = catalyst.getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
        renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, polarityToDegrees(conditions.minPolarity()));
        renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, polarityToDegrees(conditions.maxPolarity()));

        dashboardPolarity.moveTo(crucible.getPolarity(), delta, 0.05f);
        renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER, centerX, centerY, polarityToDegrees(dashboardPolarity.value()));
    }

    private static void drawHourglassLiquid(GuiGraphicsExtractor guiGraphics, float centerX, float centerY, float remainingRatio) {
        if (remainingRatio < 0.0f) {
            return;
        }

        float clampedRemaining = Mth.clamp(remainingRatio, 0.0f, 1.0f);
        float upX = centerX - Textures.DASHBOARD_HOURGLASS_UP.width() / 2.0f;
        float upY = centerY - Textures.DASHBOARD_HOURGLASS_UP.height();
        float downX = centerX - Textures.DASHBOARD_HOURGLASS_DOWN.width() / 2.0f;
        float downY = centerY;

        renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_UP, upX, upY, Textures.DASHBOARD_HOURGLASS_UP.height() * clampedRemaining);
        renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_DOWN, downX, downY, Textures.DASHBOARD_HOURGLASS_DOWN.height() * (1.0f - clampedRemaining));
    }

    private static void renderBottomCropped(GuiGraphicsExtractor guiGraphics, GuiTexture texture, float x, float y, float visibleHeight) {
        float height = Mth.clamp(visibleHeight, 0.0f, texture.height());
        if (height <= 0.001f) {
            return;
        }

        float srcY = texture.v() + texture.height() - height;
        float destY = y + texture.height() - height;
        GuiUtil.blit(
                guiGraphics,
                TextureOption.DEFAULT.renderPipeline(),
                texture.identifier(),
                x,
                destY,
                texture.u(),
                srcY,
                texture.width(),
                height,
                texture.width(),
                height,
                texture.width(),
                texture.height()
        );
    }

    private static void renderRotatedCentered(GuiGraphicsExtractor guiGraphics, GuiTexture texture, float centerX, float centerY, float angleDegrees) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(centerX, centerY);
        guiGraphics.pose().rotate(angleDegrees * Mth.DEG_TO_RAD);
        GuiUtil.blit(
                guiGraphics,
                texture.identifier(),
                -texture.width() / 2.0f,
                -texture.height() / 2.0f,
                texture.u(),
                texture.v(),
                texture.width(),
                texture.height(),
                texture.width(),
                texture.height()
        );
        guiGraphics.pose().popMatrix();
    }

    private static float polarityToDegrees(float polarity) {
        return Mth.clamp(polarity / DASHBOARD_MAX_POLARITY, -1.0f, 1.0f) * DASHBOARD_MAX_POINTER_DEGREES;
    }

    private static float getScrollExpireRemainingRatio(ItemStack catalyst, DeltaTracker delta) {
        ExpireInfo expireInfo = catalyst.get(InitDataComponents.EXPIRE_INFO);
        Minecraft mc = SafeInstance.getMC();
        if (expireInfo == null || expireInfo.period() <= 0 || mc.level == null) {
            return -1.0f;
        }

        double clockTime = mc.level.getOverworldClockTime() + delta.getGameTimeDeltaPartialTick(false);
        long nextExpire = catalyst.getOrDefault(
                InitDataComponents.NEXT_EXPIRE,
                getNextExpireForClock((long) Math.floor(clockTime), expireInfo)
        );

        if (nextExpire < clockTime) {
            long periodsBehind = (long) Math.ceil((clockTime - nextExpire) / expireInfo.period());
            nextExpire += Math.max(1L, periodsBehind) * (long) expireInfo.period();
        }

        return (float) Mth.clamp((nextExpire - clockTime) / expireInfo.period(), 0.0, 1.0);
    }

    private static long getNextExpireForClock(long clockTime, ExpireInfo expireInfo) {
        long currentOffset = clockTime % expireInfo.period();
        long currentPeriod = clockTime / expireInfo.period();
        return (currentPeriod + (currentOffset >= expireInfo.offset() ? 1 : 0)) * (long) expireInfo.period() + expireInfo.offset();
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
        Textures.ALCHEMY_ARRAYS[Math.floorMod(catalyst.hashCode(), Textures.ALCHEMY_ARRAYS.length)].renderRatio(guiGraphics, window, 0.5, 0.5);
    }

    private static void drawEssenceSlotsWithItemsAndSelection(
            GuiGraphicsExtractor guiGraphics,
            long[] xys,
            TransmutationCrucibleBlockEntity crucible,
            DeltaTracker delta,
            Int2ObjectFunction<TextureRenderable> textureGetter,
            Int2ObjectFunction<Object> itemGetter
    ) {
        int pulsedSlotIndex = getActiveEssenceInputPulseSlot(crucible, xys.length);
        for (int i = 0; i < xys.length; i++) {
            if (i == pulsedSlotIndex) {
                continue;
            }
            long packed = xys[i];
            drawEssenceSlot(guiGraphics, getXFromPacked(packed), getYFromPacked(packed), textureGetter.get(i));
            drawEssence(guiGraphics, getXFromPacked(packed), getYFromPacked(packed), itemGetter.get(i));
        }

        if (pulsedSlotIndex >= 0) {
            drawPulsedEssenceSlot(guiGraphics, xys, pulsedSlotIndex, crucible, textureGetter, itemGetter);
        }
        drawSelectedSlot(guiGraphics, xys, crucible, delta);
    }

    private static void drawEssenceSlot(GuiGraphicsExtractor guiGraphics, int x, int y, TextureRenderable texture) {
        texture.render(guiGraphics, x, y);
    }

    private static void drawEssence(GuiGraphicsExtractor guiGraphics, int x, int y, Object itemDraw) {
        if (itemDraw instanceof ItemStack itemStack) {
            guiGraphics.item(itemStack, x + 6, y + 5);
        } else if (itemDraw instanceof TextureRenderable texture) {
            texture.render(guiGraphics, VIRTUAL_ITEM, x + 6, y + 5);
        }
    }

    private static void drawPulsedEssenceSlot(
            GuiGraphicsExtractor guiGraphics,
            long[] xys,
            int pulsedSlotIndex,
            TransmutationCrucibleBlockEntity crucible,
            Int2ObjectFunction<TextureRenderable> textureGetter,
            Int2ObjectFunction<Object> itemGetter
    ) {
        long packed = xys[pulsedSlotIndex];
        int x = getXFromPacked(packed);
        int y = getYFromPacked(packed);
        float scale = getSelectedSlotClickScale(crucible);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.NORMAL_SLOT.width() / 2.0f - 0.5f, y + Textures.NORMAL_SLOT.height() / 2.0f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        drawEssenceSlot(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, textureGetter.get(pulsedSlotIndex));
        drawEssence(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, itemGetter.get(pulsedSlotIndex));
        guiGraphics.pose().popMatrix();
    }

    private static void drawSelectedSlot(GuiGraphicsExtractor guiGraphics, long[] xys, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        int selectedSlotIndex = crucible.getSelectedSlot();
        if (selectedSlotIndex < 0 || selectedSlotIndex >= xys.length) {
            return;
        }

        long packed = xys[selectedSlotIndex];
        float targetX = getXFromPacked(packed) - 1;
        float targetY = getYFromPacked(packed) - 1;

        selectedSlotHighlight.moveTo(targetX, targetY, delta);

        int x = Math.round(selectedSlotHighlight.x());
        int y = Math.round(selectedSlotHighlight.y());
        float scale = getSelectedSlotClickScale(crucible);
        if (Math.abs(scale - 1.0f) <= 0.001f) {
            Textures.SLOT_SELECTED.render(guiGraphics, x, y);
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.SLOT_SELECTED.width() / 2.0f - 0.5f, y + Textures.SLOT_SELECTED.height() / 2.0f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        Textures.SLOT_SELECTED.render(guiGraphics, -Textures.SLOT_SELECTED.width() / 2, -Textures.SLOT_SELECTED.height() / 2);
        guiGraphics.pose().popMatrix();
    }

    private static int getActiveEssenceInputPulseSlot(TransmutationCrucibleBlockEntity crucible, int slotCount) {
        int slot = crucible.getEssenceInputPulseSlot();
        if (slot < 0 || slot >= slotCount) {
            return -1;
        }

        float elapsedTicks = (System.currentTimeMillis() - crucible.getEssenceInputPulseStartedAtMillis()) / 50.0f;
        return elapsedTicks >= 0.0f && elapsedTicks < SLOT_CLICK_PRESS_TICKS + SLOT_CLICK_RELEASE_TICKS ? slot : -1;
    }

    private static float getSelectedSlotClickScale(TransmutationCrucibleBlockEntity crucible) {
        if (crucible.getEssenceInputPulseSlot() < 0) {
            return 1.0f;
        }

        float elapsedTicks = (System.currentTimeMillis() - crucible.getEssenceInputPulseStartedAtMillis()) / 50.0f;
        if (elapsedTicks < 0.0f || elapsedTicks >= SLOT_CLICK_PRESS_TICKS + SLOT_CLICK_RELEASE_TICKS) {
            return 1.0f;
        }
        if (elapsedTicks <= SLOT_CLICK_PRESS_TICKS) {
            return Easing.CUBIC_OUT.ease(elapsedTicks, 1.0f, SLOT_CLICK_MIN_SCALE - 1.0f, SLOT_CLICK_PRESS_TICKS);
        }

        float releaseTick = elapsedTicks - SLOT_CLICK_PRESS_TICKS;
        return Easing.BACK_OUT.ease(releaseTick, SLOT_CLICK_MIN_SCALE, 1.0f - SLOT_CLICK_MIN_SCALE, SLOT_CLICK_RELEASE_TICKS);
    }

    private static float easeStep(DeltaTracker delta) {
        float t = Mth.clamp(delta.getGameTimeDeltaTicks(), 0.0f, EASE_DURATION_TICKS);
        return Easing.EXPO_OUT.ease(t, 0.0f, 1.0f, EASE_DURATION_TICKS);
    }

    private static float approach(float current, float target, float step, float snapDistance) {
        if (Math.abs(target - current) <= snapDistance) {
            return target;
        }
        float next = Mth.lerp(step, current, target);
        return Math.abs(target - next) <= snapDistance ? target : next;
    }

    private static void drawArrow(GuiGraphicsExtractor guiGraphics, long[] xys, List<AbstractAlchemySlot> alchemySlots, int magicNumber, TransmutationCrucibleBlockEntity crucible) {
        int pulsedSlotIndex = getActiveEssenceInputPulseSlot(crucible, Math.min(xys.length, alchemySlots.size()));
        for (int i = 0, size = alchemySlots.size(); i < size; i++) {
            if (i == pulsedSlotIndex) {
                continue;
            }
            drawArrow(guiGraphics, getXFromPacked(xys[i]), getYFromPacked(xys[i]), alchemySlots.get(i), magicNumber, i);
        }

        if (pulsedSlotIndex < 0) {
            return;
        }

        long packed = xys[pulsedSlotIndex];
        int x = getXFromPacked(packed);
        int y = getYFromPacked(packed);
        float scale = getSelectedSlotClickScale(crucible);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x + Textures.NORMAL_SLOT.width() / 2.0f - 0.5f, y + Textures.NORMAL_SLOT.height() / 2.0f - 0.5f);
        guiGraphics.pose().scale(scale, scale);
        drawArrow(guiGraphics, -Textures.NORMAL_SLOT.width() / 2, -Textures.NORMAL_SLOT.height() / 2, alchemySlots.get(pulsedSlotIndex), magicNumber, pulsedSlotIndex);
        guiGraphics.pose().popMatrix();
    }

    private static void drawArrow(GuiGraphicsExtractor guiGraphics, int x, int y, AbstractAlchemySlot alchemySlot, int magicNumber, int slotIndex) {
        switch (alchemySlot.getShowDirection(AbstractAlchemySlot.getSlotMagicNumber(magicNumber, slotIndex))) {
            case 0 -> Textures.UP_ARROW.render(guiGraphics, x + 11, y - 2);
            case 1 -> Textures.UPRIGHT_ARROW.render(guiGraphics, x + 21, y + 5);
            case 2 -> Textures.DOWNRIGHT_ARROW.render(guiGraphics, x + 21, y + 17);
            case 3 -> Textures.DOWN_ARROW.render(guiGraphics, x + 11, y + 24);
            case 4 -> Textures.DOWNLEFT_ARROW.render(guiGraphics, x + 1, y + 17);
            case 5 -> Textures.UPLEFT_ARROW.render(guiGraphics, x + 1, y + 5);
        }
    }

    private static void drawNumbers(GuiGraphicsExtractor guiGraphics, Font font, long[] xys) {
        for (int i = 0; i < xys.length; i++) {
            long packed = xys[i];
            String str = String.valueOf(i + 1);
            guiGraphics.text(font, str, getXFromPacked(packed) + 14 - font.width(str) / 2, getYFromPacked(packed) + 10, 0xffffffff, true);
        }
    }

    private record StorageBoxHudStyle(
            int frameSize,
            float radiusRateX,
            float radiusRateY,
            float minScale,
            float maxScale,
            int maxOverlayAlpha
    ) {
    }

    private static final class RingRotationState {
        private float smoothRotation;
        private float unboundedTarget;
        private int lastComponentRotation;
        private boolean initialized;

        private float update(int componentRotation, DeltaTracker delta) {
            if (!initialized) {
                smoothRotation = componentRotation;
                unboundedTarget = componentRotation;
                lastComponentRotation = componentRotation;
                initialized = true;
            }

            if (componentRotation != lastComponentRotation) {
                int rotationDelta = componentRotation - lastComponentRotation;
                if (rotationDelta > 6) {
                    rotationDelta -= 12;
                } else if (rotationDelta < -6) {
                    rotationDelta += 12;
                }
                unboundedTarget += rotationDelta;
                lastComponentRotation = componentRotation;
            }

            smoothRotation = approach(smoothRotation, unboundedTarget, easeStep(delta), 0.01f);

            if (Math.abs(smoothRotation) > 12f) {
                float shift = 12f * Math.round(smoothRotation / 12f);
                smoothRotation -= shift;
                unboundedTarget -= shift;
            }

            return smoothRotation;
        }
    }

    private static final class SmoothPoint {
        private float x;
        private float y;
        private boolean initialized;

        private void moveTo(float targetX, float targetY, DeltaTracker delta) {
            if (!initialized) {
                x = targetX;
                y = targetY;
                initialized = true;
                return;
            }

            float step = easeStep(delta);
            x = approach(x, targetX, step, 0.01f);
            y = approach(y, targetY, step, 0.01f);
        }

        private float x() {
            return x;
        }

        private float y() {
            return y;
        }
    }

    private static final class SmoothValue {
        private float value;
        private boolean initialized;

        private void moveTo(float target, DeltaTracker delta, float snapDistance) {
            if (!initialized) {
                value = target;
                initialized = true;
                return;
            }

            value = approach(value, target, easeStep(delta), snapDistance);
        }

        private float value() {
            return value;
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
