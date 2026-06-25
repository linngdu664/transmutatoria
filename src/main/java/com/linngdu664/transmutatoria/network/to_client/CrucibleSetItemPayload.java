package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.util.ItemStackWithTwoSlots;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record CrucibleSetItemPayload(BlockPos blockPos, List<ItemStackWithTwoSlots> updates) implements CustomPacketPayload {
    public static final Type<CrucibleSetItemPayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleSetItemPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetItemPayload::blockPos,
                    ByteBufCodecs.collection(ArrayList::new, ItemStackWithTwoSlots.STREAM_CODEC), CrucibleSetItemPayload::updates,
                    CrucibleSetItemPayload::new
            );

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            for (var stackWithSlot : updates) {
                crucible.clientSetItem(stackWithSlot.slot(), stackWithSlot.rendererSlot(), stackWithSlot.stack());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
