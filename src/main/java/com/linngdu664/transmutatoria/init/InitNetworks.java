package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.network.to_client.*;
import com.linngdu664.transmutatoria.network.to_client.handler.CrucibleSetHandler;
import com.linngdu664.transmutatoria.network.to_server.ChangeCrucibleSelectedSlotPayload;
import com.linngdu664.transmutatoria.network.to_server.RotateStorageBoxPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class InitNetworks {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                RotateStorageBoxPayload.TYPE,
                RotateStorageBoxPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> payload.handle(context.player()))
        );
        registrar.playToServer(
                ChangeCrucibleSelectedSlotPayload.TYPE,
                ChangeCrucibleSelectedSlotPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> payload.handle(context.player()))
        );
        registrar.playToClient(
                CrucibleSetItemPayload.TYPE,
                CrucibleSetItemPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleItem(payload))
        );
        registrar.playToClient(
                CrucibleSetFinishPayload.TYPE,
                CrucibleSetFinishPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleFinish(payload))
        );
        registrar.playToClient(
                CrucibleSetPolarityPayload.TYPE,
                CrucibleSetPolarityPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handlePolarity(payload))
        );
        registrar.playToClient(
                CrucibleSetSelectedSlotPayload.TYPE,
                CrucibleSetSelectedSlotPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleSelectedSlot(payload))
        );
        registrar.playToClient(
                CrucibleSetProcessTimerPayload.TYPE,
                CrucibleSetProcessTimerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleProcessTimer(payload))
        );
        registrar.playToClient(
                CrucibleSetTargetTimerPayload.TYPE,
                CrucibleSetTargetTimerPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleTargetTimer(payload))
        );
        registrar.playToClient(
                CrucibleResetPayload.TYPE,
                CrucibleResetPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> CrucibleSetHandler.handleReset(payload))
        );
    }
}
