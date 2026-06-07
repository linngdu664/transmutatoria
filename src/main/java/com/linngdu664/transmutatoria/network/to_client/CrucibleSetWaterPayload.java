package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public record CrucibleSetWaterPayload(BlockPos blockPos, FluidStack water) implements CustomPacketPayload {
    public static final Type<CrucibleSetWaterPayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_water"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleSetWaterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetWaterPayload::blockPos,
                    FluidStack.OPTIONAL_STREAM_CODEC, CrucibleSetWaterPayload::water,
                    CrucibleSetWaterPayload::new
            );

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetWater(water);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
