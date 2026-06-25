package com.linngdu664.transmutatoria.util;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record ItemStackWithTwoSlots(int slot, int rendererSlot, ItemStack stack) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackWithTwoSlots> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ItemStackWithTwoSlots::slot,
            ByteBufCodecs.VAR_INT, ItemStackWithTwoSlots::rendererSlot,
            ItemStack.OPTIONAL_STREAM_CODEC, ItemStackWithTwoSlots::stack,
            ItemStackWithTwoSlots::new
    );
}
