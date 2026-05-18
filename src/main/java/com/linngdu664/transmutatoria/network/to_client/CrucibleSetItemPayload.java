package com.linngdu664.transmutatoria.network.to_client;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.ItemStackWithSlot;

import java.util.ArrayList;
import java.util.List;

import static com.linngdu664.transmutatoria.network.CustomStreamCodecs.ITEM_STACK_OPTIONAL_WITH_SLOT_STREAM_CODEC;

public record CrucibleSetItemPayload(BlockPos blockPos, List<ItemStackWithSlot> itemStackWithSlots) implements CustomPacketPayload {
    public static final Type<CrucibleSetItemPayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("crucible_set_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleSetItemPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, CrucibleSetItemPayload::blockPos,
                    ByteBufCodecs.collection(ArrayList::new, ITEM_STACK_OPTIONAL_WITH_SLOT_STREAM_CODEC), CrucibleSetItemPayload::itemStackWithSlots,
                    CrucibleSetItemPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
