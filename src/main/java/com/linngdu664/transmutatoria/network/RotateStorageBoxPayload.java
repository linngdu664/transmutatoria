package com.linngdu664.transmutatoria.network;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record RotateStorageBoxPayload(int hand, int rotation) implements CustomPacketPayload {
    public static final Type<RotateStorageBoxPayload> TYPE =
            new Type<>(ArsTransmutatoria.makeMyIdentifier("rotate_storage_box"));

    public static final StreamCodec<FriendlyByteBuf, RotateStorageBoxPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, RotateStorageBoxPayload::hand,
                    ByteBufCodecs.INT, RotateStorageBoxPayload::rotation,
                    RotateStorageBoxPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(Player player) {
        InteractionHand h = hand == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = player.getItemInHand(h);
        stack.set(InitDataComponents.ROTATION, Math.floorMod(rotation, 12));
    }
}
