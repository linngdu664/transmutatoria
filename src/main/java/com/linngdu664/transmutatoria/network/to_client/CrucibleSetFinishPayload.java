package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CrucibleSetFinishPayload(BlockPos blockPos, boolean isFinish) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleSetFinishPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_finish"));

    public static final StreamCodec<ByteBuf, CrucibleSetFinishPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetFinishPayload::blockPos,
                    ByteBufCodecs.BOOL, CrucibleSetFinishPayload::isFinish,
                    CrucibleSetFinishPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
