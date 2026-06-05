package com.linngdu664.transmutatoria.client.renderer.special;

import com.linngdu664.transmutatoria.client.model.AlchemistStorageBoxModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import org.jspecify.annotations.NonNull;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record AlchemistStorageBoxSpecialRenderer(AlchemistStorageBoxModel model)
        implements NoDataSpecialModelRenderer {
    @Override
    public void submit(
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        submitNodeCollector.submitModel(
                model,
                0.0F,
                poseStack,
                AlchemistStorageBoxModel.TEXTURE_LOCATION,
                lightCoords,
                overlayCoords,
                outlineColor,
                null);
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        model.setupAnim(0.0F);
        model.root().getExtentsForGui(poseStack, consumer);
    }

    public static class Unbaked implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public AlchemistStorageBoxSpecialRenderer bake(BakingContext bakingContext) {
            var model = new AlchemistStorageBoxModel(
                    bakingContext.entityModelSet().bakeLayer(AlchemistStorageBoxModel.LAYER_LOCATION));
            return new AlchemistStorageBoxSpecialRenderer(model);
        }

        @Override
        public @NonNull MapCodec<? extends NoDataSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
