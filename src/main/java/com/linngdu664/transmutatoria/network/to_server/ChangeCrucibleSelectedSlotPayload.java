package com.linngdu664.transmutatoria.network.to_server;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record ChangeCrucibleSelectedSlotPayload(BlockPos blockPos, boolean isIncrease) implements CustomPacketPayload {
    public static final Type<ChangeCrucibleSelectedSlotPayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("change_crucible_selected_slot"));

    public static final StreamCodec<FriendlyByteBuf, ChangeCrucibleSelectedSlotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ChangeCrucibleSelectedSlotPayload::blockPos,
                    ByteBufCodecs.BOOL, ChangeCrucibleSelectedSlotPayload::isIncrease,
                    ChangeCrucibleSelectedSlotPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.serverScrollSelectedSlot(isIncrease);
        }
    }
}
