package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.animation.RingRotationState;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.joml.Matrix3x2fStack;

import java.util.Arrays;

public class StorageBoxRing implements HudComponent {
    private static final float SLOT_MIN_SCALE = 0.5f;
    private static final float SLOT_MAX_SCALE = 1.25f;
    private static final int SLOT_MAX_OVERLAY_ALPHA = 0xcc;

    private final RingRotationState storageBoxRotation = new RingRotationState();
    private final SmoothValue storageBoxExpansion = new SmoothValue();

    private final NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
    private final int[] slotXYs = new int[24];
    private final float[] scales = new float[12];
    private final int[] renderOrder = new int[12];
    private final int[] maskAlphas = new int[12];
    private final TextureRenderable[] relationTextures = new TextureRenderable[12];

    private boolean shouldRender;
    private int centerXi;
    private int collapsedCenterYi;
    private int relationTextureAlpha;
    private Component essenceComponent;

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        ItemStack boxStack;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (mainHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = mainHand;
        } else if (offHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = offHand;
        } else {
            shouldRender = false;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        shouldRender = true;

        int componentRotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
        int selectedSlot = Math.floorMod(componentRotation + 6, 12);
        float smoothRotation = storageBoxRotation.update(componentRotation, delta);

        storageBoxExpansion.moveTo(mc.hasAltDown() ? 1.0F : 0.0F, delta, 0.005F);
        float expansion = storageBoxExpansion.value();

        int frameSize = Textures.SIMPLE_FRAME.height();
        float edgePadding = frameSize * (0.5F * SLOT_MAX_SCALE);

        Window window = mc.getWindow();
        int screenW = window.getGuiScaledWidth();
        int screenH = window.getGuiScaledHeight();

        float centerX = screenW * 0.5F;
        float collapsedCenterY = Math.max(12F, screenH * 0.04F) + edgePadding;
        float expandedCenterY = screenH * 0.5F;
        float desiredRadius = expandedCenterY - collapsedCenterY;
        float radius = Math.min(desiredRadius, Math.max(0.0f, expandedCenterY - edgePadding));
        centerXi = Math.round(centerX);
        collapsedCenterYi = Math.round(collapsedCenterY);

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(items);

        EssenceMetal[] essenceMetals = EssenceMetal.values();
        EssenceMetal selectedEssence = essenceMetals[selectedSlot];

        for (int i = 0; i < 12; i++) {
            float angle = ((smoothRotation - i) * 30.0F - 90.0F) * Mth.DEG_TO_RAD;
            int slotX = Math.round(centerX + radius * Mth.cos(angle));
            float expandedSlotY = expandedCenterY - radius * Mth.sin(angle);
            int slotY = Math.round(Mth.lerp(expansion, collapsedCenterY, expandedSlotY));
            float depth = (Mth.sin(angle) + 1.0F) * 0.5F;
            float perspectiveScale = SLOT_MIN_SCALE + (SLOT_MAX_SCALE - SLOT_MIN_SCALE) * depth;
            int collapsedMaskAlpha = Math.round(SLOT_MAX_OVERLAY_ALPHA * (1.0f - depth));
            EssenceMetal essence = essenceMetals[i];
            int expandedMaskAlpha = i != selectedSlot && selectedEssence.getRelationTo(essence) == EssenceMetal.Relation.NEUTRAL ? SLOT_MAX_OVERLAY_ALPHA : 0;
            slotXYs[2 * i] = slotX;
            slotXYs[2 * i + 1] = slotY;
            scales[i] = i != selectedSlot ? Mth.lerp(expansion, perspectiveScale, 1.0F) : perspectiveScale;
            maskAlphas[i] = Math.round(Mth.lerp(expansion, collapsedMaskAlpha, expandedMaskAlpha));
        }

        if (expansion > 0.005F) {
            for (int i = 0; i < 12; i++) {
                relationTextures[i] = getRelationBorder(selectedEssence, essenceMetals[i]);
            }
            relationTextureAlpha = Math.round(255.0f * expansion);
        } else {
            Arrays.fill(relationTextures, null);
        }

        int centerIdx = Math.floorMod(Math.round(smoothRotation), 12);
        int ri = 0;
        renderOrder[ri++] = centerIdx;
        for (int offset = 1; offset <= 5; offset++) {
            renderOrder[ri++] = Math.floorMod(centerIdx + offset, 12);
            renderOrder[ri++] = Math.floorMod(centerIdx - offset, 12);
        }
        renderOrder[ri] = Math.floorMod(centerIdx + 6, 12);

        essenceComponent = mc.hasAltDown() ? null : Component.translatable("item.transmutatoria." + selectedEssence.getKey());
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics) {
        if (!shouldRender) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Matrix3x2fStack pose = guiGraphics.pose();
        int frameSize = Textures.SIMPLE_FRAME.height();

        for (int idx = 0; idx < 12; idx++) {
            int i = renderOrder[idx];
            pose.pushMatrix();
            pose.translate(slotXYs[2 * i], slotXYs[2 * i + 1]);
            float scale = scales[i];
            pose.scale(scale, scale);

            TextureRenderable relationBorder = relationTextures[i];
            if (relationBorder != null) {
                relationBorder.render(guiGraphics, -relationBorder.width() / 2, -relationBorder.height() / 2, TextureOption.withAlpha(relationTextureAlpha));
            }

            Textures.SIMPLE_FRAME.render(guiGraphics, -frameSize / 2, -frameSize / 2);

            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                guiGraphics.item(stack, -8, -8);
                guiGraphics.itemDecorations(mc.font, stack, -8, -8);
            }

            int maskAlpha = maskAlphas[i];
            if (maskAlpha > 0) {
                Textures.SIMPLE_FRAME_MASK.render(guiGraphics, -frameSize / 2, -frameSize / 2, TextureOption.withAlpha(maskAlpha));
            }

            pose.popMatrix();
        }

        if (essenceComponent != null) {
            guiGraphics.text(mc.font, essenceComponent, centerXi + 10, collapsedCenterYi + 16, 0xffffffff);
//            guiGraphics.text(mc.font, essenceComponent, centerXi - mc.font.width(essenceComponent) / 2, collapsedCenterYi - 26, 0xffffffff);
        }

        Textures.STORAGE_BOX_INSERT_ARROW.render(guiGraphics, centerXi - Textures.STORAGE_BOX_INSERT_ARROW.width() / 2, collapsedCenterYi + 16);
    }

    private static TextureRenderable getRelationBorder(EssenceMetal selected, EssenceMetal other) {
        return switch (selected.getRelationTo(other)) {
            case RESTRAIN -> Textures.STORAGE_BOX_RESTRAINED_BORDER;
            case BE_RESTRAINED -> Textures.STORAGE_BOX_RESTRAINING_BORDER;
            case SYMBIOSIS -> Textures.STORAGE_BOX_SYMBIOSIS_BORDER;
            case MUTUAL_RESTRAINED -> Textures.STORAGE_BOX_MUTUAL_RESTRAINED_BORDER;
            case DOUBLE_RESTRAIN -> Textures.STORAGE_BOX_DOUBLE_RESTRAINED_BORDER;
            case DOUBLE_BE_RESTRAINED -> Textures.STORAGE_BOX_DOUBLE_RESTRAINING_BORDER;
            case SAME -> Textures.STORAGE_BOX_SAME_BORDER;
            case NEUTRAL -> null;
        };
    }
}
