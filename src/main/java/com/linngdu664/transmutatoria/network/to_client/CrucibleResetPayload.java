package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CrucibleResetPayload(BlockPos blockPos, boolean isFinish, int polarity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleResetPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_reset"));

    public static final StreamCodec<ByteBuf, CrucibleResetPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleResetPayload::blockPos,
                    ByteBufCodecs.BOOL, CrucibleResetPayload::isFinish,
                    ByteBufCodecs.VAR_INT, CrucibleResetPayload::polarity,
                    CrucibleResetPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
