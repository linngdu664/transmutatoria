package com.linngdu664.transmutatoria.client.renderer.effect;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

/** Render pipeline and translucent render type used by the crucible heat-haze billboard. */
@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public final class CrucibleHeatHazeRenderType {
    private static final RenderPipeline PIPELINE = RenderPipeline.builder(
                    RenderPipelines.MATRICES_PROJECTION_SNIPPET,
                    RenderPipelines.GLOBALS_SNIPPET
            )
            .withLocation(ArsTransmutatoria.makeMyIdentifier("pipeline/crucible_heat_haze"))
            .withVertexShader(ArsTransmutatoria.makeMyIdentifier("core/crucible_heat_haze"))
            .withFragmentShader(ArsTransmutatoria.makeMyIdentifier("core/crucible_heat_haze"))
            .withSampler("SceneSampler")
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withCull(false)
            .build();

    private static final RenderType RENDER_TYPE = RenderType.create(
            "transmutatoria_crucible_heat_haze",
            RenderSetup.builder(PIPELINE)
                    .withTexture("SceneSampler", CrucibleHeatHazeCapture.TEXTURE_ID)
                    .sortOnUpload()
                    .bufferSize(256)
                    .createRenderSetup()
    );

    private CrucibleHeatHazeRenderType() {}

    public static RenderType get() {
        return RENDER_TYPE;
    }

    @SubscribeEvent
    public static void registerPipeline(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(PIPELINE);
    }
}
