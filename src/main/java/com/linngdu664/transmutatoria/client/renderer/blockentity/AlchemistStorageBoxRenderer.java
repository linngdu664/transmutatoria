package com.linngdu664.transmutatoria.client.renderer.blockentity;

import com.linngdu664.transmutatoria.block.AlchemistStorageBoxBlock;
import com.linngdu664.transmutatoria.block.entity.AlchemistStorageBoxBlockEntity;
import com.linngdu664.transmutatoria.client.model.AlchemistStorageBoxModel;
import com.linngdu664.transmutatoria.client.renderer.state.blockentity.AlchemistStorageBoxRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AlchemistStorageBoxRenderer
        implements BlockEntityRenderer<AlchemistStorageBoxBlockEntity, AlchemistStorageBoxRenderState> {
    private final AlchemistStorageBoxModel model;

    public AlchemistStorageBoxRenderer(BlockEntityRendererProvider.Context context) {
        model = new AlchemistStorageBoxModel(context.bakeLayer(AlchemistStorageBoxModel.LAYER_LOCATION));
    }

    @Override
    public AlchemistStorageBoxRenderState createRenderState() {
        return new AlchemistStorageBoxRenderState();
    }

    @Override
    public void extractRenderState(
            AlchemistStorageBoxBlockEntity blockEntity,
            AlchemistStorageBoxRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(AlchemistStorageBoxBlock.FACING);
        state.openness = blockEntity.getOpenNess(partialTicks);
    }

    @Override
    public void submit(
            AlchemistStorageBoxRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        float openness = 1.0F - state.openness;
        openness = 1.0F - openness * openness * openness;
        submitNodeCollector.submitModel(
                model,
                openness,
                poseStack,
                AlchemistStorageBoxModel.TEXTURE_LOCATION,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0,
                state.breakProgress);
        poseStack.popPose();
    }
}
