package com.linngdu664.transmutatoria.network.to_client.handler;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.network.to_client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class CrucibleSetHandler {
    public static void handleItem(CrucibleSetItemPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            for (var stackWithSlot : payload.itemStackWithSlots()) {
                crucible.clientSetItem(stackWithSlot.slot(), stackWithSlot.stack());
            }
        }
    }

    public static void handleSelectedSlot(CrucibleSetSelectedSlotPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetSelectedSlot(payload.selectedSlot());
        }
    }

    public static void handlePolarity(CrucibleSetPolarityPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetPolarity(payload.polarity());
        }
    }

    public static void handleProcessTimer(CrucibleSetProcessTimerPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetProcessTimer(payload.processTimer());
        }
    }

    public static void handleTargetTimer(CrucibleSetTargetTimerPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetTargetTimer(payload.targetTimer());
        }
    }

    public static void handleFinish(CrucibleSetFinishPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetFinish(payload.isFinish());
        }
    }

    public static void handleReset(CrucibleResetPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        BlockPos blockPos = payload.blockPos();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetFinish(payload.isFinish());
            crucible.clientSetPolarity(payload.polarity());
            crucible.clientSetSelectedSlot(0);
            crucible.clientSetTargetTimer(0);
            crucible.clientSetProcessTimer(0);
        }
    }
}
