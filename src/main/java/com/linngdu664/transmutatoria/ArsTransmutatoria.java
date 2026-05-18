package com.linngdu664.transmutatoria;

import com.linngdu664.transmutatoria.init.*;
import com.linngdu664.transmutatoria.network.to_client.*;
import com.linngdu664.transmutatoria.network.to_server.RotateStorageBoxPayload;
import com.linngdu664.transmutatoria.network.to_client.handler.CrucibleSetHandler;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ArsTransmutatoria.MODID)
public class ArsTransmutatoria {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "transmutatoria";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static Identifier makeMyIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ArsTransmutatoria(IEventBus modEventBus, ModContainer modContainer) {
        // Register the Deferred Register to the mod event bus so blocks get registered
        InitDataComponents.DATA_COMPONENTS.register(modEventBus);
        InitBlocks.BLOCKS.register(modEventBus);
        InitBlocks.BLOCK_ENTITIES.register(modEventBus);
        InitItems.ITEMS.register(modEventBus);
        InitItems.TABS.register(modEventBus);
        InitMenuTypes.MENU_TYPES.register(modEventBus);
        InitRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        // Register the Deferred Register to the mod event bus so tabs get registered
//        CREATIVE_MODE_TABS.register(modEventBus);

        // Register the item to a creative tab
//        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(ArsTransmutatoria::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(MODID)
                .playToServer(
                        RotateStorageBoxPayload.TYPE,
                        RotateStorageBoxPayload.STREAM_CODEC,
                        (payload, context) -> payload.handle(context.player())
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetItemPayload.TYPE,
                        CrucibleSetItemPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handleItem(payload)
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetFinishPayload.TYPE,
                        CrucibleSetFinishPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handleFinish(payload)
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetPolarityPayload.TYPE,
                        CrucibleSetPolarityPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handlePolarity(payload)
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetSelectedSlotPayload.TYPE,
                        CrucibleSetSelectedSlotPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handleSelectedSlot(payload)
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetProcessTimerPayload.TYPE,
                        CrucibleSetProcessTimerPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handleProcessTimer(payload)
                );
        event.registrar(MODID)
                .playToClient(
                        CrucibleSetTargetTimerPayload.TYPE,
                        CrucibleSetTargetTimerPayload.STREAM_CODEC,
                        (payload, _) -> CrucibleSetHandler.handleTargetTimer(payload)
                );
    }
}
