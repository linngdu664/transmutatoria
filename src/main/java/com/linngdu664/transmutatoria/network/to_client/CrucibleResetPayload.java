package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetFinish(isFinish);
            crucible.clientSetPolarity(polarity);
            crucible.clientSetSelectedSlot(0);
            crucible.clientSetTargetTimer(0);
            crucible.clientSetProcessTimer(0);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
