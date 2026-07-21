package com.linngdu664.transmutatoria.client.renderer.blockentity;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.renderer.state.blockentity.CrucibleRSlotPose;
import com.linngdu664.transmutatoria.client.renderer.state.blockentity.TransmutationCrucibleRenderState;
import com.linngdu664.transmutatoria.client.tool.CrucibleItemAnimator;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
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
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TransmutationCrucibleRenderer implements BlockEntityRenderer<TransmutationCrucibleBlockEntity, TransmutationCrucibleRenderState> {
    private static final Identifier WATER_TEXTURE = ArsTransmutatoria.makeMyIdentifier("textures/block/water_0.png");
    private static final int WATER_CAPACITY = 1000;
    private static final float WATER_MIN_Y = 5.0625F / 16.0F;
    private static final float WATER_MAX_Y = 13.0F / 16.0F;
    private static final float WATER_X0 = 2.0625F / 16.0F;
    private static final float WATER_X1 = 13.9375F / 16.0F;
    private static final float WATER_Z0 = 2.0625F / 16.0F;
    private static final float WATER_Z1 = 13.9375F / 16.0F;
    // The inner model boundary is a 12×12 square with one-pixel corner cut-outs, not the block collision shape.
    private static final float MODEL_EDGE_OUTER_MIN = 2.0F / 16.0F;
    private static final float MODEL_EDGE_INNER_MIN = 3.0F / 16.0F;
    private static final float MODEL_EDGE_INNER_MAX = 13.0F / 16.0F;
    private static final float MODEL_EDGE_OUTER_MAX = 14.0F / 16.0F;
    /** Moves the light half a model sub-pixel into the hollow to prevent z-fighting with the inner wall. */
    private static final float INTERIOR_LIGHT_WALL_OFFSET = 1.0F / 1024.0F;
    /**
     * The 12 vertices of the stepped inner outline after mitering each corner. Adjacent wall panels share
     * these exact endpoints, preventing the gaps and crossings caused by independently offset panels.
     */
    private static final float[] INTERIOR_LIGHT_OUTLINE_X = {
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET
    };
    private static final float[] INTERIOR_LIGHT_OUTLINE_Z = {
            MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
            MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET
    };
    private static final float INTERIOR_LIGHT_BOTTOM_Y = 5.0F / 16.0F + INTERIOR_LIGHT_WALL_OFFSET;
    private static final float CRUCIBLE_OPENING_Y = 15.0F / 16.0F + INTERIOR_LIGHT_WALL_OFFSET;
    private static final float INTERIOR_LIGHT_TOP_Y = 20.0F / 16.0F;
    private static final int INTERIOR_LIGHT_BOTTOM_ALPHA = 224;
    private static final int INTERIOR_LIGHT_TOP_ALPHA = 0;
    private static final int MIN_POLARITY = -50;
    private static final int MAX_POLARITY = 50;
    /** RGB tint baked into water_0.png before it was converted to a white texture. */
    private static final int ORIGINAL_WATER_TEXTURE_COLOR = ARGB.color(212, 36, 212);
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

        CrucibleItemAnimator animator = blockEntity.getAnimator();
        if (animator == null) {
            return;
        }

        List<ItemStack> items = blockEntity.getItems();
        int[] realSlotToRendererSlot = blockEntity.getRealSlotToRendererSlot();

        for (int i = 0; i < TransmutationCrucibleBlockEntity.SLOT_COUNT; i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                int rendererSlot = realSlotToRendererSlot[i];
                if (rendererSlot >= 0 && rendererSlot < TransmutationCrucibleBlockEntity.RENDERER_SLOT_COUNT) {
                    ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
                    itemModelResolver.updateForTopItem(itemStackRenderState, itemStack, ItemDisplayContext.GUI, null, null, 0);
                    CrucibleRSlotPose pose = animator.extractPose(rendererSlot, i, partialTicks);
                    state.itemAndPoses.add(new ObjectObjectImmutablePair<>(itemStackRenderState, pose));
                }
            }
        }
    }

    @Override
    public void submit(
            TransmutationCrucibleRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            CameraRenderState camera
    ) {
        int lightCoords = state.lightCoords;

        for (var pair : state.itemAndPoses) {
            CrucibleRSlotPose slotPose = pair.right();
            poseStack.pushPose();
            poseStack.translate(0.5F + slotPose.x(), slotPose.y(), 0.5F + slotPose.z());
            float scale = slotPose.scale();
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(new Quaternionf(new AxisAngle4f(slotPose.yaw(), 0, 1, 0))
                    .rotateAxis(Mth.HALF_PI + slotPose.pitch(), 1, 0, 0));
            pair.left().submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.waterAmount > 0) {
            float fill = Mth.clamp((float) state.waterAmount / WATER_CAPACITY, 0.0F, 1.0F);
            float waterY = WATER_MIN_Y + (WATER_MAX_Y - WATER_MIN_Y) * fill;
            int topColor = compensateWhiteWaterTexture(state.waterColor);
            int sideColor = compensateWhiteWaterTexture(ARGB.scaleRGB(state.waterColor, 0.72F));

            submitNodeCollector.submitCustomGeometry(
                    poseStack,
                    RenderTypes.entityTranslucent(WATER_TEXTURE, false),
                    (pose, buffer) -> renderWater(pose, buffer, waterY, topColor, sideColor, lightCoords)
            );
        }

        int openingLightColor = compensateWhiteWaterTexture(state.waterColor);
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                // lightning() writes into WEATHER_TARGET, which breaks the compositing of nearby world translucency.
                // This normal translucent layer is sorted with other transparent geometry and does not write depth.
                RenderTypes.entityTranslucentEmissive(WATER_TEXTURE, false),
                (pose, buffer) -> renderInteriorLight(
                        pose, buffer,
                        ARGB.red(openingLightColor),
                        ARGB.green(openingLightColor),
                        ARGB.blue(openingLightColor)
                )
        );
    }

    @Override
    public AABB getRenderBoundingBox(TransmutationCrucibleBlockEntity blockEntity) {
        var pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + INTERIOR_LIGHT_TOP_Y, pos.getZ() + 1);
    }

    private static int getWaterColor(TransmutationCrucibleBlockEntity blockEntity) {
        return getWaterColor(blockEntity.getPolarity());
    }

    private static int getWaterColor(int polarity) {
        polarity = Mth.clamp(polarity, MIN_POLARITY, MAX_POLARITY);
        float progress = (float) (polarity - MIN_POLARITY) * (1F / (MAX_POLARITY - MIN_POLARITY));
        return ARGB.srgbLerp(progress, NEGATIVE_WATER_COLOR, POSITIVE_WATER_COLOR);
    }

    /** Returns the exact RGB tint used by the water's top surface. */
    public static int getWaterSurfaceColor(int polarity) {
        return compensateWhiteWaterTexture(getWaterColor(polarity));
    }

    /**
     * Keeps the displayed color unchanged after water_0.png was made white by moving its former RGB tint to the
     * vertex color. The texture alpha remains in the texture, so it is deliberately not multiplied here.
     */
    private static int compensateWhiteWaterTexture(int color) {
        return ARGB.color(
                ARGB.alpha(color),
                Math.round(ARGB.red(color) * ARGB.red(ORIGINAL_WATER_TEXTURE_COLOR) / 255.0F),
                Math.round(ARGB.green(color) * ARGB.green(ORIGINAL_WATER_TEXTURE_COLOR) / 255.0F),
                Math.round(ARGB.blue(color) * ARGB.blue(ORIGINAL_WATER_TEXTURE_COLOR) / 255.0F)
        );
    }

    private static void renderWater(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float waterY,
            int topColor,
            int sideColor,
            int lightCoords
    ) {
        waterQuad(
                pose, buffer,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                topColor, lightCoords,
                0.0F, 1.0F, 0.0F
        );

        waterQuad(
                pose, buffer,
                WATER_X1, WATER_MIN_Y, WATER_Z0,
                WATER_X1, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, WATER_MIN_Y, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                0.0F, 0.0F, -1.0F
        );

        waterQuad(
                pose, buffer,
                WATER_X0, WATER_MIN_Y, WATER_Z1,
                WATER_X0, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, WATER_MIN_Y, WATER_Z1,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                0.0F, 0.0F, 1.0F
        );

        waterQuad(
                pose, buffer,
                WATER_X0, WATER_MIN_Y, WATER_Z0,
                WATER_X0, waterY, WATER_Z0,
                WATER_X0, waterY, WATER_Z1,
                WATER_X0, WATER_MIN_Y, WATER_Z1,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                -1.0F, 0.0F, 0.0F
        );

        waterQuad(
                pose, buffer,
                WATER_X1, WATER_MIN_Y, WATER_Z1,
                WATER_X1, waterY, WATER_Z1,
                WATER_X1, waterY, WATER_Z0,
                WATER_X1, WATER_MIN_Y, WATER_Z0,
                0.0F, 1.0F, 1.0F, 0.0F,
                sideColor, lightCoords,
                1.0F, 0.0F, 0.0F
        );
    }

    private static void waterQuad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float u0, float v0,
            float u1, float v1,
            int color, int lightCoords,
            float normalX, float normalY, float normalZ
    ) {
        waterVertex(pose, buffer, x0, y0, z0, u0, v0, color, lightCoords, normalX, normalY, normalZ);
        waterVertex(pose, buffer, x1, y1, z1, u0, v1, color, lightCoords, normalX, normalY, normalZ);
        waterVertex(pose, buffer, x2, y2, z2, u1, v1, color, lightCoords, normalX, normalY, normalZ);
        waterVertex(pose, buffer, x3, y3, z3, u1, v0, color, lightCoords, normalX, normalY, normalZ);
    }

    private static void waterVertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x, float y, float z,
            float u, float v,
            int color, int lightCoords,
            float normalX, float normalY, float normalZ
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightCoords)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    /** Renders the fixed floor light plus a vertical fade along the model's stepped inner outline. */
    private static void renderInteriorLight(PoseStack.Pose pose, VertexConsumer buffer, int red, int green, int blue) {
        renderInteriorLightFloor(pose, buffer, red, green, blue);
        renderInteriorLightSide(pose, buffer, red, green, blue);
    }

    private static void renderInteriorLightFloor(PoseStack.Pose pose, VertexConsumer buffer, int red, int green, int blue) {
        // 10×10 center, then the four 10×1 strips. Together these are the 12×12 outline with its four 1×1 corners removed.
        interiorLightFloorQuad(
                pose, buffer,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                red, green, blue
        );
        interiorLightFloorQuad(
                pose, buffer,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                red, green, blue
        );
        interiorLightFloorQuad(
                pose, buffer,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                red, green, blue
        );
        interiorLightFloorQuad(
                pose, buffer,
                MODEL_EDGE_OUTER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                red, green, blue
        );
        interiorLightFloorQuad(
                pose, buffer,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MIN + INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_OUTER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                MODEL_EDGE_INNER_MAX - INTERIOR_LIGHT_WALL_OFFSET,
                red, green, blue
        );
    }

    private static void renderInteriorLightSide(PoseStack.Pose pose, VertexConsumer buffer, int red, int green, int blue) {
        int openingAlpha = Math.round(
                INTERIOR_LIGHT_BOTTOM_ALPHA + (INTERIOR_LIGHT_TOP_ALPHA - INTERIOR_LIGHT_BOTTOM_ALPHA)
                        * (CRUCIBLE_OPENING_Y - INTERIOR_LIGHT_BOTTOM_Y) / (INTERIOR_LIGHT_TOP_Y - INTERIOR_LIGHT_BOTTOM_Y));

        // 4 large faces
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[0], INTERIOR_LIGHT_OUTLINE_Z[0],
                INTERIOR_LIGHT_OUTLINE_X[1], INTERIOR_LIGHT_OUTLINE_Z[1],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[3], INTERIOR_LIGHT_OUTLINE_Z[3],
                INTERIOR_LIGHT_OUTLINE_X[4], INTERIOR_LIGHT_OUTLINE_Z[4],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[6], INTERIOR_LIGHT_OUTLINE_Z[6],
                INTERIOR_LIGHT_OUTLINE_X[7], INTERIOR_LIGHT_OUTLINE_Z[7],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[9], INTERIOR_LIGHT_OUTLINE_Z[9],
                INTERIOR_LIGHT_OUTLINE_X[10], INTERIOR_LIGHT_OUTLINE_Z[10],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );

        // 8 inner small faces
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[1], INTERIOR_LIGHT_OUTLINE_Z[1],
                INTERIOR_LIGHT_OUTLINE_X[2], INTERIOR_LIGHT_OUTLINE_Z[2],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[2], INTERIOR_LIGHT_OUTLINE_Z[2],
                INTERIOR_LIGHT_OUTLINE_X[3], INTERIOR_LIGHT_OUTLINE_Z[3],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[4], INTERIOR_LIGHT_OUTLINE_Z[4],
                INTERIOR_LIGHT_OUTLINE_X[5], INTERIOR_LIGHT_OUTLINE_Z[5],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[5], INTERIOR_LIGHT_OUTLINE_Z[5],
                INTERIOR_LIGHT_OUTLINE_X[6], INTERIOR_LIGHT_OUTLINE_Z[6],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[7], INTERIOR_LIGHT_OUTLINE_Z[7],
                INTERIOR_LIGHT_OUTLINE_X[8], INTERIOR_LIGHT_OUTLINE_Z[8],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[8], INTERIOR_LIGHT_OUTLINE_Z[8],
                INTERIOR_LIGHT_OUTLINE_X[9], INTERIOR_LIGHT_OUTLINE_Z[9],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[10], INTERIOR_LIGHT_OUTLINE_Z[10],
                INTERIOR_LIGHT_OUTLINE_X[11], INTERIOR_LIGHT_OUTLINE_Z[11],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[11], INTERIOR_LIGHT_OUTLINE_Z[11],
                INTERIOR_LIGHT_OUTLINE_X[0], INTERIOR_LIGHT_OUTLINE_Z[0],
                red, green, blue,
                INTERIOR_LIGHT_BOTTOM_Y, INTERIOR_LIGHT_BOTTOM_ALPHA,
                CRUCIBLE_OPENING_Y, openingAlpha
        );

        // Above crucible opening: 4 diagonal faces connecting adjacent large faces, forming a convex octagon, from y=15/16
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[1], INTERIOR_LIGHT_OUTLINE_Z[1],
                INTERIOR_LIGHT_OUTLINE_X[3], INTERIOR_LIGHT_OUTLINE_Z[3],
                red, green, blue,
                CRUCIBLE_OPENING_Y, openingAlpha,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[4], INTERIOR_LIGHT_OUTLINE_Z[4],
                INTERIOR_LIGHT_OUTLINE_X[6], INTERIOR_LIGHT_OUTLINE_Z[6],
                red, green, blue,
                CRUCIBLE_OPENING_Y, openingAlpha,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[7], INTERIOR_LIGHT_OUTLINE_Z[7],
                INTERIOR_LIGHT_OUTLINE_X[9], INTERIOR_LIGHT_OUTLINE_Z[9],
                red, green, blue,
                CRUCIBLE_OPENING_Y, openingAlpha,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );
        interiorLightSideQuad(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[10], INTERIOR_LIGHT_OUTLINE_Z[10],
                INTERIOR_LIGHT_OUTLINE_X[0], INTERIOR_LIGHT_OUTLINE_Z[0],
                red, green, blue,
                CRUCIBLE_OPENING_Y, openingAlpha,
                INTERIOR_LIGHT_TOP_Y, INTERIOR_LIGHT_TOP_ALPHA
        );

        // 4 horizontal right triangles capping the gaps between the concave 12-gon and convex octagon at y=CRUCIBLE_OPENING_Y
        interiorLightTriangle(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[1], INTERIOR_LIGHT_OUTLINE_Z[1],
                INTERIOR_LIGHT_OUTLINE_X[2], INTERIOR_LIGHT_OUTLINE_Z[2],
                INTERIOR_LIGHT_OUTLINE_X[3], INTERIOR_LIGHT_OUTLINE_Z[3],
                CRUCIBLE_OPENING_Y, openingAlpha, red, green, blue
        );
        interiorLightTriangle(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[4], INTERIOR_LIGHT_OUTLINE_Z[4],
                INTERIOR_LIGHT_OUTLINE_X[5], INTERIOR_LIGHT_OUTLINE_Z[5],
                INTERIOR_LIGHT_OUTLINE_X[6], INTERIOR_LIGHT_OUTLINE_Z[6],
                CRUCIBLE_OPENING_Y, openingAlpha, red, green, blue
        );
        interiorLightTriangle(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[7], INTERIOR_LIGHT_OUTLINE_Z[7],
                INTERIOR_LIGHT_OUTLINE_X[8], INTERIOR_LIGHT_OUTLINE_Z[8],
                INTERIOR_LIGHT_OUTLINE_X[9], INTERIOR_LIGHT_OUTLINE_Z[9],
                CRUCIBLE_OPENING_Y, openingAlpha, red, green, blue
        );
        interiorLightTriangle(
                pose, buffer,
                INTERIOR_LIGHT_OUTLINE_X[10], INTERIOR_LIGHT_OUTLINE_Z[10],
                INTERIOR_LIGHT_OUTLINE_X[11], INTERIOR_LIGHT_OUTLINE_Z[11],
                INTERIOR_LIGHT_OUTLINE_X[0], INTERIOR_LIGHT_OUTLINE_Z[0],
                CRUCIBLE_OPENING_Y, openingAlpha, red, green, blue
        );
    }

    private static void interiorLightTriangle(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x0, float z0,
            float x1, float z1,
            float x2, float z2,
            float y, int alpha, int red, int green, int blue
    ) {
        interiorLightVertex(pose, buffer, x0, y, z0, alpha, red, green, blue);
        interiorLightVertex(pose, buffer, x1, y, z1, alpha, red, green, blue);
        interiorLightVertex(pose, buffer, x2, y, z2, alpha, red, green, blue);
        interiorLightVertex(pose, buffer, x2, y, z2, alpha, red, green, blue);
    }

    private static void interiorLightSideQuad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x0, float z0,
            float x1, float z1,
            int red, int green, int blue,
            float bottomY, int bottomAlpha,
            float topY, int topAlpha
    ) {
        interiorLightVertex(pose, buffer, x0, bottomY, z0, bottomAlpha, red, green, blue);
        interiorLightVertex(pose, buffer, x1, bottomY, z1, bottomAlpha, red, green, blue);
        interiorLightVertex(pose, buffer, x1, topY, z1, topAlpha, red, green, blue);
        interiorLightVertex(pose, buffer, x0, topY, z0, topAlpha, red, green, blue);
    }

    private static void interiorLightFloorQuad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x0, float z0,
            float x1, float z1,
            int red, int green, int blue
    ) {
        interiorLightVertex(pose, buffer, x0, INTERIOR_LIGHT_BOTTOM_Y, z0, INTERIOR_LIGHT_BOTTOM_ALPHA, red, green, blue);
        interiorLightVertex(pose, buffer, x0, INTERIOR_LIGHT_BOTTOM_Y, z1, INTERIOR_LIGHT_BOTTOM_ALPHA, red, green, blue);
        interiorLightVertex(pose, buffer, x1, INTERIOR_LIGHT_BOTTOM_Y, z1, INTERIOR_LIGHT_BOTTOM_ALPHA, red, green, blue);
        interiorLightVertex(pose, buffer, x1, INTERIOR_LIGHT_BOTTOM_Y, z0, INTERIOR_LIGHT_BOTTOM_ALPHA, red, green, blue);
    }

    private static void interiorLightVertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x, float y, float z,
            int alpha, int red, int green, int blue
    ) {
        buffer.addVertex(pose, x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
