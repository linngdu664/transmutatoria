package com.linngdu664.transmutatoria.client.renderer.blockentity;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.renderer.state.blockentity.TransmutationCrucibleRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TransmutationCrucibleRenderer
        implements BlockEntityRenderer<TransmutationCrucibleBlockEntity, TransmutationCrucibleRenderState> {
    private static final Identifier WATER_TEXTURE =
            ArsTransmutatoria.makeMyIdentifier("textures/block/water_0.png");
    private static final int WATER_CAPACITY = 1000;
    private static final float WATER_MIN_Y = 5.0625F / 16.0F;
    private static final float WATER_MAX_Y = 13.0F / 16.0F;
    private static final float WATER_X0 = 2.0625F / 16.0F;
    private static final float WATER_X1 = 13.9375F / 16.0F;
    private static final float WATER_Z0 = 2.0625F / 16.0F;
    private static final float WATER_Z1 = 13.9375F / 16.0F;
    private static final int MIN_POLARITY = -50;
    private static final int MAX_POLARITY = 50;
    private static final int NEGATIVE_WATER_COLOR = ARGB.color(190, 18, 45, 160);
    private static final int POSITIVE_WATER_COLOR = ARGB.color(190, 240, 42, 32);

    private final ItemModelResolver itemModelResolver;

    public TransmutationCrucibleRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public TransmutationCrucibleRenderState createRenderState() {
        return new TransmutationCrucibleRenderState();
    }

    @Override
    public void extractRenderState(
            TransmutationCrucibleBlockEntity blockEntity,
            TransmutationCrucibleRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.waterAmount = blockEntity.getWaterAmount();
        state.waterColor = getWaterColor(blockEntity);

        state.gameTime = blockEntity.getLevel().getGameTime() + partialTicks;
        int idx = 0;
        List<ItemStack> essences = blockEntity.getInputEssences();
        List<ItemStack> outputEssences = blockEntity.getOutputEssences();
        Level level = blockEntity.getLevel();

        // 1. Output (slot 50)
        ItemStack output = blockEntity.getOutput();
        if (!output.isEmpty()) {
            updateFloatingItem(state.floatingItems[idx++], level, output);
        }
        // 2. Output essences (slots 24-47, from high to low)
        for (int i = 23; i >= 0 && idx < 8; i--) {
            ItemStack stack = outputEssences.get(i);
            if (!stack.isEmpty()) {
                updateFloatingItem(state.floatingItems[idx++], level, stack);
            }
        }
        // 3. Input essences (slots 0-23, from high to low)
        for (int i = 23; i >= 0 && idx < 8; i--) {
            ItemStack stack = essences.get(i);
            if (!stack.isEmpty()) {
                updateFloatingItem(state.floatingItems[idx++], level, stack);
            }
        }
        // 4. Input (slot 49)
        ItemStack input = blockEntity.getInput();
        if (!input.isEmpty() && idx < 8) {
            updateFloatingItem(state.floatingItems[idx++], level, input);
        }
        // 5. Catalyst (slot 48)
        ItemStack catalyst = blockEntity.getCatalyst();
        if (!catalyst.isEmpty() && idx < 8) {
            updateFloatingItem(state.floatingItems[idx++], level, catalyst);
        }
        state.floatingItemCount = idx;
    }

    @Override
    public void submit(
            TransmutationCrucibleRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        if (state.waterAmount <= 0) {
            return;
        }

        float fill = Mth.clamp((float) state.waterAmount / WATER_CAPACITY, 0.0F, 1.0F);
        float waterY = WATER_MIN_Y + (WATER_MAX_Y - WATER_MIN_Y) * fill;
        int topColor = state.waterColor;
        int sideColor = ARGB.scaleRGB(topColor, 0.72F);
        int lightCoords = state.lightCoords;

        submitNodeCollector.submitCustomGeometry(
                poseStack,
                RenderTypes.entityTranslucent(WATER_TEXTURE, false),
                (pose, buffer) -> renderWater(pose, buffer, waterY, topColor, sideColor, lightCoords));

        // Render floating items
        int count = state.floatingItemCount;
        if (count > 0) {
            float angularSpeed = Mth.TWO_PI * 0.002F;
            float radius = 0.25F;
            float amplitude = 0.05F;
            float yOffset = 0.06F;
            float itemScale = 0.25F;

            for (int i = 0; i < count; i++) {
                ItemStackRenderState itemState = state.floatingItems[i];
                if (itemState.isEmpty()) {
                    continue;
                }
                float phase = Mth.TWO_PI * i / (float) count;
                float angle = angularSpeed * state.gameTime + phase;
                float x = 0.5F + radius * Mth.cos(angle);
                float y = waterY + yOffset + amplitude * Mth.sin(angle);
                float z = 0.5F + radius * Mth.sin(angle);

                poseStack.pushPose();
                poseStack.translate(x, y, z);
                poseStack.scale(itemScale, itemScale, itemScale);
                itemState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
            }
        }
    }

    private void updateFloatingItem(ItemStackRenderState renderState, Level level, ItemStack stack) {
//        renderState.clear();
        itemModelResolver.updateForTopItem(renderState, stack, ItemDisplayContext.GROUND, level, null, 0);
    }

    private static int getWaterColor(TransmutationCrucibleBlockEntity blockEntity) {
        int polarity = Mth.clamp(blockEntity.getPolarity(), MIN_POLARITY, MAX_POLARITY);
        float progress = (float) (polarity - MIN_POLARITY) / (MAX_POLARITY - MIN_POLARITY);
        return ARGB.srgbLerp(progress, NEGATIVE_WATER_COLOR, POSITIVE_WATER_COLOR);
    }

    private static void renderWater(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float waterY,
            int topColor,
            int sideColor,
            int lightCoords
    ) {
        quad(pose, buffer,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                topColor, lightCoords,
                0.0F, 1.0F, 0.0F);

        quad(pose, buffer,
                WATER_X1, WATER_MIN_Y, WATER_Z0,
                WATER_X1, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, WATER_MIN_Y, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                0.0F, 0.0F, -1.0F);

        quad(pose, buffer,
                WATER_X0, WATER_MIN_Y, WATER_Z1,
                WATER_X0, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, WATER_MIN_Y, WATER_Z1,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                0.0F, 0.0F, 1.0F);

        quad(pose, buffer,
                WATER_X0, WATER_MIN_Y, WATER_Z0,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z1,
                WATER_X0, WATER_MIN_Y, WATER_Z1,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                -1.0F, 0.0F, 0.0F);

        quad(pose, buffer,
                WATER_X1, WATER_MIN_Y, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z0,
                WATER_X1, WATER_MIN_Y, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                1.0F, 0.0F, 0.0F);
    }

    private static void quad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x0,
            float y0,
            float z0,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2,
            float x3,
            float y3,
            float z3,
            float u0,
            float v0,
            float u1,
            float v1,
            int color,
            int lightCoords,
            float normalX,
            float normalY,
            float normalZ
    ) {
        vertex(pose, buffer, x0, y0, z0, u0, v0, color, lightCoords, normalX, normalY, normalZ);
        vertex(pose, buffer, x1, y1, z1, u0, v1, color, lightCoords, normalX, normalY, normalZ);
        vertex(pose, buffer, x2, y2, z2, u1, v1, color, lightCoords, normalX, normalY, normalZ);
        vertex(pose, buffer, x3, y3, z3, u1, v0, color, lightCoords, normalX, normalY, normalZ);
    }

    private static void vertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x,
            float y,
            float z,
            float u,
            float v,
            int color,
            int lightCoords,
            float normalX,
            float normalY,
            float normalZ
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
