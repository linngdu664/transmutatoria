package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CrucibleSetProcessTimerPayload(BlockPos blockPos, int processTimer) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleSetProcessTimerPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_process_timer"));

    public static final StreamCodec<ByteBuf, CrucibleSetProcessTimerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetProcessTimerPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CrucibleSetProcessTimerPayload::processTimer,
                    CrucibleSetProcessTimerPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
