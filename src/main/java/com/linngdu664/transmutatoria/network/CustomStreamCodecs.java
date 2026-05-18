package com.linngdu664.transmutatoria.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;

public class CustomStreamCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackWithSlot> ITEM_STACK_OPTIONAL_WITH_SLOT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ItemStackWithSlot::slot,
            ItemStack.OPTIONAL_STREAM_CODEC, ItemStackWithSlot::stack,
            ItemStackWithSlot::new
    );
}
