package com.linngdu664.transmutatoria.client.renderer.effect;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Owns a shader-readable copy of the opaque world color for the crucible's heat-haze material.
 * The copy is requested while an active crucible render state is extracted, then performed once at
 * the opaque-to-translucent boundary regardless of how many crucibles are visible.
 */
@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public final class CrucibleHeatHazeCapture extends AbstractTexture {
    public static final Identifier TEXTURE_ID = ArsTransmutatoria.makeMyIdentifier("dynamic/crucible_heat_haze_scene");

    private static final CrucibleHeatHazeCapture INSTANCE = new CrucibleHeatHazeCapture();
    private static final AtomicBoolean REQUESTED = new AtomicBoolean();

    private boolean registered;
    private int width;
    private int height;
    private TextureFormat format;

    private CrucibleHeatHazeCapture() {
        this.sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
    }

    /** Marks the opaque scene color as needed by at least one visible crucible this frame. */
    public static void request() {
        REQUESTED.set(true);
    }

    @SubscribeEvent
    public static void captureAfterOpaqueFeatures(RenderLevelStageEvent.AfterOpaqueFeatures event) {
        if (REQUESTED.getAndSet(false)) {
            INSTANCE.capture();
        }
    }

    private void capture() {
        Minecraft minecraft = Minecraft.getInstance();
        var mainTarget = minecraft.getMainRenderTarget();
        GpuTexture mainColor = mainTarget.getColorTexture();
        if (mainColor == null || mainTarget.width <= 0 || mainTarget.height <= 0) {
            return;
        }

        ensureStorage(mainTarget.width, mainTarget.height, mainColor.getFormat());
        if (!registered) {
            minecraft.getTextureManager().register(TEXTURE_ID, this);
            registered = true;
        }

        RenderSystem.getDevice()
                .createCommandEncoder()
                .copyTextureToTexture(
                        mainColor,
                        this.texture,
                        0,
                        0,
                        0,
                        0,
                        0,
                        mainTarget.width,
                        mainTarget.height
                );
    }

    private void ensureStorage(int width, int height, TextureFormat format) {
        if (this.texture != null
                && !this.texture.isClosed()
                && this.width == width
                && this.height == height
                && this.format == format) {
            return;
        }

        destroyStorage();
        this.width = width;
        this.height = height;
        this.format = format;
        this.texture = RenderSystem.getDevice().createTexture(
                "Transmutatoria crucible heat-haze scene color",
                GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING,
                format,
                width,
                height,
                1,
                1
        );
        this.textureView = RenderSystem.getDevice().createTextureView(this.texture);
    }

    private void destroyStorage() {
        if (this.textureView != null) {
            this.textureView.close();
            this.textureView = null;
        }
        if (this.texture != null) {
            this.texture.close();
            this.texture = null;
        }
        this.width = 0;
        this.height = 0;
        this.format = null;
    }

    @Override
    public void close() {
        destroyStorage();
        registered = false;
    }
}
