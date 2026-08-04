package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.particle.AlchemyParticleSpawner;
import com.linngdu664.transmutatoria.client.tool.CrucibleItemAnimator;
import com.linngdu664.transmutatoria.util.ItemStackWithTwoSlots;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record CrucibleFinishPayload(BlockPos blockPos, byte polarity, List<ItemStackWithTwoSlots> updates) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CrucibleFinishPayload> TYPE =
            new CustomPacketPayload.Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_finish"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleFinishPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleFinishPayload::blockPos,
                    ByteBufCodecs.BYTE, CrucibleFinishPayload::polarity,
                    ByteBufCodecs.collection(ArrayList::new, ItemStackWithTwoSlots.STREAM_CODEC), CrucibleFinishPayload::updates,
                    CrucibleFinishPayload::new
            );

    public void handle(Player player) {
        Level level = player.level();
        if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible) {
            crucible.clientSetSelectedSlot(0);
            crucible.clientSetTargetTimer(0);
            crucible.clientSetProcessTimer(0);
            crucible.clientSetPolarity(polarity);
            for (var stackWithSlot : updates) {
                crucible.clientSetItem(stackWithSlot.slot(), stackWithSlot.rendererSlot(), stackWithSlot.stack());
            }
            CrucibleItemAnimator animator = crucible.getAnimator();
            if (animator != null) {
                animator.onReactionFinished();
            }
            AlchemyParticleSpawner.spawnBurst((ClientLevel) level, blockPos, polarity);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
