package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CrucibleSetSelectedSlotPayload(BlockPos blockPos, int selectedSlot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleSetSelectedSlotPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_selected_slot"));

    public static final StreamCodec<ByteBuf, CrucibleSetSelectedSlotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetSelectedSlotPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CrucibleSetSelectedSlotPayload::selectedSlot,
                    CrucibleSetSelectedSlotPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
