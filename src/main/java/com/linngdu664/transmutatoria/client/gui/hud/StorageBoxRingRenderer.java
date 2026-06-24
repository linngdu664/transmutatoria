package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.client.gui.ScreenPos;
import com.linngdu664.transmutatoria.client.gui.animation.RingRotationState;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import com.linngdu664.transmutatoria.client.gui.texture.TextureOption;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class StorageBoxRingRenderer {
    private static final StorageBoxHudStyle STYLE = new StorageBoxHudStyle(
            Textures.SIMPLE_FRAME.height(),
            0.4f,
            0.5f,
            1.3f,
            0xc0
    );

    private StorageBoxRingRenderer() {
    }

    public static void render(GuiGraphicsExtractor guiGraphics, ItemStack boxStack, DeltaTracker delta, RingRotationState storageBoxRotation, SmoothValue storageBoxExpansion) {
        Minecraft mc = Minecraft.getInstance();

        int componentRotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
        float smoothRotation = storageBoxRotation.update(componentRotation, delta);
        storageBoxExpansion.moveTo(mc.hasAltDown() ? 1.0f : 0.0f, delta, 0.005f);
        float expansion = storageBoxExpansion.value();

        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        float centerX = screenW * 0.5f;
        float collapsedCenterY = screenH * 0.1f;
        float expandedCenterY = screenH * 0.5f;
        int frameSize = STYLE.frameSize();
        float desiredRadius = screenW * 0.5f * STYLE.radiusRate();
        float edgePadding = frameSize * STYLE.maxScale() * 0.5f;
        float radius = Math.min(desiredRadius, Math.max(0.0f, expandedCenterY - edgePadding));

        ItemContainerContents contents = boxStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(12, ItemStack.EMPTY);
        contents.copyInto(items);

        int selectedSlot = Math.floorMod(componentRotation + 6, 12);
        EssenceMetal selectedEssence = EssenceMetal.values()[selectedSlot];

        long[] slotXYs = new long[12];
        float[] scales = new float[12];
        float[] depths = new float[12];

        for (int i = 0; i < 12; i++) {
            float angle = ((i - smoothRotation) * 30.0f - 90.0f) * Mth.DEG_TO_RAD;
            int slotX = Math.round(centerX + radius * Mth.cos(angle));
            depths[i] = (Mth.sin(angle) + 1.0f) * 0.5f;
            float expandedSlotY = expandedCenterY - radius * Mth.sin(angle);
            int slotY = Math.round(Mth.lerp(expansion, collapsedCenterY, expandedSlotY));
            slotXYs[i] = ScreenPos.pack(slotX, slotY);
            float perspectiveScale = STYLE.minScale() + (STYLE.maxScale() - STYLE.minScale()) * depths[i];
            scales[i] = Mth.lerp(expansion, perspectiveScale, STYLE.maxScale());
        }

        int centerIdx = Math.floorMod(Math.round(smoothRotation), 12);
        int[] renderOrder = new int[12];
        int ri = 0;
        renderOrder[ri++] = centerIdx;
        for (int offset = 1; offset <= 5; offset++) {
            renderOrder[ri++] = Math.floorMod(centerIdx + offset, 12);
            renderOrder[ri++] = Math.floorMod(centerIdx - offset, 12);
        }
        renderOrder[ri] = Math.floorMod(centerIdx + 6, 12);

        for (int idx = 0; idx < 12; idx++) {
            int i = renderOrder[idx];
            long packed = slotXYs[i];
            float scale = scales[i];

            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(ScreenPos.unpackX(packed), ScreenPos.unpackY(packed));
            guiGraphics.pose().scale(scale, scale);

            TextureRenderable relationBorder = getRelationBorder(selectedEssence, EssenceMetal.values()[i]);
            if (relationBorder != null && expansion > 0.005f) {
                relationBorder.render(
                        guiGraphics,
                        TextureOption.withAlpha(Math.round(255.0f * expansion)),
                        -relationBorder.width() / 2,
                        -relationBorder.height() / 2
                );
            }

            Textures.SIMPLE_FRAME.render(guiGraphics, -frameSize / 2, -frameSize / 2);

            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                guiGraphics.item(stack, -8, -8);
                guiGraphics.itemDecorations(mc.font, stack, -8, -8);
            }

            int collapsedMaskAlpha = Math.round(STYLE.maxOverlayAlpha() * (1.0f - depths[i]));
            int expandedMaskAlpha = i != selectedSlot && relationBorder == null ? STYLE.maxOverlayAlpha() : 0;
            int maskAlpha = Math.round(Mth.lerp(expansion, collapsedMaskAlpha, expandedMaskAlpha));
            if (maskAlpha > 0) {
                Textures.SIMPLE_FRAME_MASK.render(
                        guiGraphics,
                        TextureOption.withAlpha(maskAlpha),
                        -frameSize / 2,
                        -frameSize / 2
                );
            }

            guiGraphics.pose().popMatrix();
        }
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

    private record StorageBoxHudStyle(int frameSize, float radiusRate, float minScale, float maxScale, int maxOverlayAlpha) {
    }
}
