package com.linngdu664.transmutatoria.network.to_client.handler;

import com.linngdu664.transmutatoria.block.entity.BlockEntityTransmutationCrucible;
import com.linngdu664.transmutatoria.network.to_client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class CrucibleSetHandler {
    public static void handleItem(CrucibleSetItemPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            for (var stackWithSlot : payload.itemStackWithSlots()) {
                crucible.clientSetItem(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }
    }

    public static void handleSelectedSlot(CrucibleSetSelectedSlotPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            crucible.setSelectedSlot(payload.selectedSlot());
        }
    }

    public static void handlePolarity(CrucibleSetPolarityPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            crucible.clientSetPolarity(payload.polarity());
        }
    }

    public static void handleProcessTimer(CrucibleSetProcessTimerPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            crucible.clientSetProcessTimer(payload.processTimer());
        }
    }

    public static void handleTargetTimer(CrucibleSetTargetTimerPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            crucible.clientSetTargetTimer(payload.targetTimer());
        }
    }

    public static void handleFinish(CrucibleSetFinishPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof BlockEntityTransmutationCrucible crucible) {
            crucible.clientSetFinish(payload.isFinish());
        }
    }
}
