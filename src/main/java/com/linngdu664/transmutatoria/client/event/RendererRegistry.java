package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.model.AlchemistStorageBoxModel;
import com.linngdu664.transmutatoria.client.renderer.blockentity.AlchemistStorageBoxRenderer;
import com.linngdu664.transmutatoria.client.renderer.blockentity.TransmutationCrucibleRenderer;
import com.linngdu664.transmutatoria.client.renderer.special.AlchemistStorageBoxSpecialRenderer;
import com.linngdu664.transmutatoria.init.InitBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class RendererRegistry {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AlchemistStorageBoxModel.LAYER_LOCATION, AlchemistStorageBoxModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(InitBlocks.ALCHEMIST_STORAGE_BOX_BLOCK_ENTITY.get(), AlchemistStorageBoxRenderer::new);
        event.registerBlockEntityRenderer(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), TransmutationCrucibleRenderer::new);
    }

    @SubscribeEvent
    public static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(
                ArsTransmutatoria.makeMyIdentifier("alchemist_storage_box_special"),
                AlchemistStorageBoxSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
