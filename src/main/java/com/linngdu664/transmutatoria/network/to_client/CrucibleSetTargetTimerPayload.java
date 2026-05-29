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

public record CrucibleSetTargetTimerPayload(BlockPos blockPos, int targetTimer) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleSetTargetTimerPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_target_timer"));

    public static final StreamCodec<ByteBuf, CrucibleSetTargetTimerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetTargetTimerPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CrucibleSetTargetTimerPayload::targetTimer,
                    CrucibleSetTargetTimerPayload::new
            );

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetTargetTimer(targetTimer);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
