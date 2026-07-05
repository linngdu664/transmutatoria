package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.animation.CrucibleSlotAnimation;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothPoint;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.tool.Easing;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.item.TransmutationSigilScrollItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;

import java.util.*;

public class EssenceSlotGraph implements HudComponent {
    private static final TextureOption VIRTUAL_ITEM = TextureOption.withAlpha(48);
    private static final float SLOT_CLICK_PRESS_TICKS = 3.0f;
    private static final float SLOT_CLICK_RELEASE_TICKS = 7.0f;
    private static final float SLOT_CLICK_MIN_SCALE = 0.78f;
    private static final int HALF_SLOT_WIDTH = Textures.NORMAL_SLOT.width() / 2;
    private static final int HALF_SLOT_HEIGHT = Textures.NORMAL_SLOT.height() / 2;
    private static final float POSE_TRANS_HALF_SLOT_WIDTH = Textures.NORMAL_SLOT.width() * 0.5f - 0.5f;
    private static final float POSE_TRANS_HALF_SLOT_HEIGHT = Textures.NORMAL_SLOT.height() * 0.5f - 0.5f;
    private static final int HALF_SELECTED_WIDTH = Textures.SLOT_SELECTED.width() / 2;
    private static final int HALF_SELECTED_HEIGHT = Textures.SLOT_SELECTED.height() / 2;
    private static final float POSE_TRANS_HALF_SELECTED_WIDTH = Textures.SLOT_SELECTED.width() * 0.5f - 0.5f;
    private static final float POSE_TRANS_HALF_SELECTED_HEIGHT = Textures.SLOT_SELECTED.height() * 0.5f - 0.5f;

    private final SmoothPoint selectedSlotHighlight = new SmoothPoint();
    private final CrucibleSlotAnimation crucibleSlotAnimation = new CrucibleSlotAnimation();

    private final int[] xys = new int[48];
    private final float[] scales = new float[24];
    private final Object[] essences = new Object[24];
    private final TextureRenderable[] slotTextures = new TextureRenderable[24];
    private final int[] directions = new int[24];

    private int size;
    private boolean drawSelectHighlight;
    private boolean drawArrow;
    private TextureRenderable background;
    private int selectedSlotIndex;
    private int centerX;
    private int centerY;

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        centerX = window.getGuiScaledWidth() / 2;
        centerY = window.getGuiScaledHeight() / 2;

        ItemStack catalyst = crucible.getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            size = 0;
            drawSelectHighlight = false;
            drawArrow = false;
            background = Textures.ALCHEMY_ARRAY_2;
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            size = 24;
            drawSelectHighlight = false;
            drawArrow = false;
            background = Textures.ALCHEMY_ARRAY_1;

            int originX = centerX - HALF_SLOT_WIDTH;
            int originY = centerY + HALF_SLOT_HEIGHT;
            int index = appendHollowTriangle(0, 1, 1, 3, false, originX, originY);
            appendHollowTriangle(index, 0, 0, 6, true, originX, originY);

            List<ItemStack> outputEssences = crucible.getOutputEssences();
            for (int i = 0; i < 24; i++) {
                essences[i] = outputEssences.get(i);
            }

            Arrays.fill(slotTextures, Textures.NORMAL_SLOT);
        } else if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            List<EssenceMetal> restrainEssences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().stream().toList();
            size = restrainEssences.size();
            drawSelectHighlight = true;
            drawArrow = false;
            background = Textures.ALCHEMY_ARRAY_8;

            List<ItemStack> inputEssences = crucible.getInputEssences();
            int x = centerX - HALF_SLOT_WIDTH - 10 * (size - 1);
            int y = centerY - HALF_SLOT_HEIGHT - 6;
            for (int i = 0; i < size; i++) {
                // 设置坐标
                xys[2 * i] = x;
                xys[2 * i + 1] = y;
                x += 20;
                y += ((i & 1) == 0) ? 12 : -12;

                // 设置源质
                ItemStack inputEssence = inputEssences.get(i);
                if (!inputEssence.isEmpty()) {
                    essences[i] = inputEssence;
                } else {
                    essences[i] = restrainEssences.get(i).getDefaultTexture();
                }
            }

            Arrays.fill(slotTextures, 0, size, Textures.NORMAL_SLOT);
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            size = 2;
            drawSelectHighlight = true;
            drawArrow = false;
            background = Textures.ALCHEMY_ARRAY_6;

            int x = centerX - HALF_SLOT_WIDTH;
            int y = centerY - HALF_SLOT_HEIGHT;
            xys[0] = x;
            xys[1] = y - 12;
            xys[2] = x;
            xys[3] = y + 12;

            List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
            essences[0] = essencesInCrucible.get(0);
            essences[1] = essencesInCrucible.get(1);

            slotTextures[0] = Textures.NORMAL_SLOT;
            slotTextures[1] = Textures.NORMAL_SLOT;
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            size = alchemySlots.size();
            drawSelectHighlight = true;
            drawArrow = true;
            background = crucible.isPhilosophersStoneRecipe()
                    ? Textures.ALCHEMY_ARRAY_3
                    : (catalyst.getItem() instanceof TransmutationSigilScrollItem ? Textures.ALCHEMY_ARRAY_7 : Textures.ALCHEMY_ARRAY_5);

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
            for (AbstractAlchemySlot slot : alchemySlots) {
                minX = Math.min(slot.getX(), minX);
                maxX = Math.max(slot.getX(), maxX);
                minY = Math.min(slot.getY(), minY);
                maxY = Math.max(slot.getY(), maxY);
            }
            int originX = centerX - HALF_SLOT_WIDTH;
            int originY = centerY - HALF_SLOT_HEIGHT;

            int initX = originX - 10 * (maxX - minX);
            int initY = originY - 6 * (maxY - minY);

            List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
            int magicNumber = catalyst.getOrDefault(InitDataComponents.MAGIC_NUMBER, 0);
            int i = 0;
            for (AbstractAlchemySlot alchemySlot : alchemySlots) {
                // 设置坐标
                xys[2 * i] = initX + 20 * (alchemySlot.getX() - minX);
                xys[2 * i + 1] = initY + 12 * (alchemySlot.getY() - minY);

                // 设置源质
                ItemStack essence = essencesInCrucible.get(i);
                if (!essence.isEmpty() || !alchemySlot.isShowEssence()) {
                    essences[i] = essence;
                } else {
                    essences[i] = alchemySlot.getEssenceMetal().getDefaultTexture();
                }

                // 设置槽位和方向
                slotTextures[i] = alchemySlot.getTexture();
                directions[i] = alchemySlot.getShowDirection(AbstractAlchemySlot.getSlotMagicNumber(magicNumber, i));

                i++;
            }
        } else {
            size = 0;
            drawSelectHighlight = false;
            drawArrow = false;
            background = null;
        }

        // 基础槽位缩放系数
        crucibleSlotAnimation.update(crucible, delta, 0);
        if (crucibleSlotAnimation.isRunningFor(crucible)) {
            for (int i = 0; i < size; i++) {
                float progress = crucibleSlotAnimation.processProgress();
                float slotProgress = Mth.clamp(progress * size - i, 0.0f, 1.0f);
                scales[i] = 1.0f - Easing.CUBIC_IN.ease(slotProgress, 0.0f, 1.0f, 1.0f);
            }
        } else {
            for (int i = 0; i < size; i++) {
                scales[i] = crucibleSlotAnimation.revealScale();
            }
        }

        if (drawSelectHighlight) {
            // 设置高亮槽位，移动高亮框
            selectedSlotIndex = crucible.getSelectedSlot();
            int targetX = xys[2 * selectedSlotIndex] - 1;
            int targetY = xys[2 * selectedSlotIndex + 1] - 1;
            selectedSlotHighlight.moveTo(targetX, targetY, delta);

            // 源质投入动画缩放系数
            int inputPulseSlot = crucible.getEssenceInputPulseSlot();
            if (inputPulseSlot >= 0 && inputPulseSlot < size) {
                float elapsedTicks = (System.currentTimeMillis() - crucible.getEssenceInputPulseStartedAtMillis()) * (1.0f / 50.0f);
                if (elapsedTicks >= 0.0f && elapsedTicks < SLOT_CLICK_PRESS_TICKS + SLOT_CLICK_RELEASE_TICKS) {
                    if (elapsedTicks <= SLOT_CLICK_PRESS_TICKS) {
                        scales[inputPulseSlot] *= Easing.CUBIC_OUT.ease(elapsedTicks, 1.0f, SLOT_CLICK_MIN_SCALE - 1.0f, SLOT_CLICK_PRESS_TICKS);
                    } else {
                        float releaseTick = elapsedTicks - SLOT_CLICK_PRESS_TICKS;
                        scales[inputPulseSlot] *= Easing.BACK_OUT.ease(releaseTick, SLOT_CLICK_MIN_SCALE, 1.0f - SLOT_CLICK_MIN_SCALE, SLOT_CLICK_RELEASE_TICKS);
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics) {
        Matrix3x2fStack pose = guiGraphics.pose();
        if (background != null) {
            background.render(guiGraphics, centerX - background.width() / 2, centerY - background.height() / 2);
        }

        // 画槽位和物品
        for (int i = 0; i < size; i++) {
            float scale = scales[i];
            if (scale > 0.001f) {
                int x = xys[2 * i];
                int y = xys[2 * i + 1];
                if (Math.abs(scale - 1.0f) <= 0.001f) {
                    drawSlot(guiGraphics, x, y, slotTextures[i]);
                    drawItem(guiGraphics, x, y, essences[i]);
                } else {
                    pose.pushMatrix();
                    pose.translate(x + POSE_TRANS_HALF_SLOT_WIDTH, y + POSE_TRANS_HALF_SLOT_HEIGHT);
                    pose.scale(scale, scale);
                    drawSlot(guiGraphics, -HALF_SLOT_WIDTH, -HALF_SLOT_HEIGHT, slotTextures[i]);
                    drawItem(guiGraphics, -HALF_SLOT_WIDTH, -HALF_SLOT_HEIGHT, essences[i]);
                    pose.popMatrix();
                }
            }
        }

        // 画选中高亮
        if (drawSelectHighlight) {
            float scale = scales[selectedSlotIndex];
            if (scale > 0.001f) {
                int x = Math.round(selectedSlotHighlight.x());
                int y = Math.round(selectedSlotHighlight.y());
                if (Math.abs(scale - 1.0f) <= 0.001f) {
                    Textures.SLOT_SELECTED.render(guiGraphics, x, y);
                } else {
                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(x + POSE_TRANS_HALF_SELECTED_WIDTH, y + POSE_TRANS_HALF_SELECTED_HEIGHT);
                    guiGraphics.pose().scale(scale, scale);
                    Textures.SLOT_SELECTED.render(guiGraphics, -HALF_SELECTED_WIDTH, -HALF_SELECTED_HEIGHT);
                    guiGraphics.pose().popMatrix();
                }
            }
        }

        // 画箭头
        if (drawArrow) {
            for (int i = 0; i < size; i++) {
                if (directions[i] < 0 || directions[i] > 5) {
                    continue;
                }
                float scale = scales[i];
                if (scale > 0.001f) {
                    int x = xys[2 * i];
                    int y = xys[2 * i + 1];
                    if (Math.abs(scale - 1.0f) <= 0.001f) {
                        drawArrow(guiGraphics, x, y, directions[i]);
                    } else {
                        pose.pushMatrix();
                        pose.translate(x + POSE_TRANS_HALF_SLOT_WIDTH, y + POSE_TRANS_HALF_SLOT_HEIGHT);
                        pose.scale(scale, scale);
                        drawArrow(guiGraphics, -HALF_SLOT_WIDTH, -HALF_SLOT_HEIGHT, directions[i]);
                        pose.popMatrix();
                    }
                }
            }
        }

        // 画数字
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasShiftDown()) {
            Font font = mc.font;
            for (int i = 0; i < size; i++) {
                float scale = scales[i];
                if (scale > 0.001f) {
                    int slotX = xys[2 * i];
                    int slotY = xys[2 * i + 1];
                    String str = String.valueOf(i + 1);
                    if (Math.abs(scale - 1.0f) <= 0.001f) {
                        guiGraphics.text(font, str, slotX + 14 - font.width(str) / 2, slotY + 10, 0xffffffff, true);
                    } else {
                        pose.pushMatrix();
                        pose.translate(slotX + POSE_TRANS_HALF_SLOT_WIDTH, slotY + POSE_TRANS_HALF_SLOT_HEIGHT);
                        pose.scale(scale, scale);
                        guiGraphics.text(font, str, 1 - font.width(str) / 2, -3, 0xffffffff, true);
                        pose.popMatrix();
                    }
                }
            }
        }
    }

    private static void drawArrow(GuiGraphicsExtractor guiGraphics, int x, int y, int direction) {
        switch (direction) {
            case 0 -> Textures.UP_ARROW.render(guiGraphics, x + 11, y - 2);
            case 1 -> Textures.UPRIGHT_ARROW.render(guiGraphics, x + 21, y + 5);
            case 2 -> Textures.DOWNRIGHT_ARROW.render(guiGraphics, x + 21, y + 17);
            case 3 -> Textures.DOWN_ARROW.render(guiGraphics, x + 11, y + 24);
            case 4 -> Textures.DOWNLEFT_ARROW.render(guiGraphics, x + 1, y + 17);
            case 5 -> Textures.UPLEFT_ARROW.render(guiGraphics, x + 1, y + 5);
        }
    }

    private static void drawSlot(GuiGraphicsExtractor guiGraphics, int x, int y, TextureRenderable texture) {
        texture.render(guiGraphics, x, y);
    }

    private static void drawItem(GuiGraphicsExtractor guiGraphics, int x, int y, Object itemDraw) {
        if (itemDraw instanceof ItemStack itemStack) {
            guiGraphics.item(itemStack, x + 6, y + 5);
        } else if (itemDraw instanceof TextureRenderable texture) {
            texture.render(guiGraphics, VIRTUAL_ITEM, x + 6, y + 5);
        }
    }

    private int appendHollowTriangle(int index, int originQ, int originR, int edgeSteps, boolean removeVertices, int originX, int originY) {
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                index = packPhilosophersStoneSlot(originX, originY, originQ + step, originR, index);
            }
        }
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                index = packPhilosophersStoneSlot(originX, originY, originQ + edgeSteps - step, originR + step, index);
            }
        }
        for (int step = 0; step < edgeSteps; step++) {
            if (!removeVertices || step != 0) {
                index = packPhilosophersStoneSlot(originX, originY, originQ, originR + edgeSteps - step, index);
            }
        }
        return index;
    }

    private int packPhilosophersStoneSlot(int originX, int originY, int q, int r, int index) {
        xys[2 * index] = originX + q * 20 - 40;
        xys[2 * index + 1] = originY + (2 * r + q) * 12 - 96;
        return index + 1;
    }
}
