package com.linngdu664.transmutatoria.client.renderer.special;

import com.linngdu664.transmutatoria.client.model.AlchemistStorageBoxModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public record AlchemistStorageBoxSpecialRenderer(AlchemistStorageBoxModel model)
        implements SpecialModelRenderer<Float> {
    @Override
    public void submit(
            @Nullable Float rawOpenness,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        float openness = rawOpenness == null ? 0.0F : rawOpenness;
        float closedness = 1.0F - openness;
        openness = 1.0F - closedness * closedness * closedness;
        submitNodeCollector.submitModel(
                model,
                openness,
                poseStack,
                AlchemistStorageBoxModel.TEXTURE_LOCATION,
                lightCoords,
                overlayCoords,
                outlineColor,
                null);
    }

    @Override
    public Float extractArgument(ItemStack stack) {
        return AlchemistStorageBoxItemLidAnimation.getOpenness(stack);
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        model.setupAnim(0.0F);
        model.root().getExtentsForGui(poseStack, consumer);
        model.setupAnim(1.0F);
        model.root().getExtentsForGui(poseStack, consumer);
    }

    public static class Unbaked implements SpecialModelRenderer.Unbaked<Float> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public AlchemistStorageBoxSpecialRenderer bake(BakingContext bakingContext) {
            var model = new AlchemistStorageBoxModel(
                    bakingContext.entityModelSet().bakeLayer(AlchemistStorageBoxModel.LAYER_LOCATION));
            return new AlchemistStorageBoxSpecialRenderer(model);
        }

        @Override
        public @NonNull MapCodec<? extends SpecialModelRenderer.Unbaked<Float>> type() {
            return MAP_CODEC;
        }
    }
}
