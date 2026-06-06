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

public record CrucibleSetPolarityPayload(BlockPos blockPos, int polarity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleSetPolarityPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_polarity"));

    public static final StreamCodec<ByteBuf, CrucibleSetPolarityPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetPolarityPayload::blockPos,
                    ByteBufCodecs.VAR_INT, CrucibleSetPolarityPayload::polarity,
                    CrucibleSetPolarityPayload::new
            );

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetPolarity(polarity);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
