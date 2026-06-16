package com.linngdu664.transmutatoria.recipe.crucible.level_function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record FixedLevel(int min, int max) implements LevelFunction {

    public static final MapCodec<FixedLevel> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("min").forGetter(FixedLevel::min),
            Codec.INT.fieldOf("max").forGetter(FixedLevel::max)
    ).apply(i, FixedLevel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FixedLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FixedLevel::min,
            ByteBufCodecs.VAR_INT, FixedLevel::max,
            FixedLevel::new
    );

    @Override
    public String type() {
        return "transmutatoria:fixed";
    }

    @Override
    public IntIntImmutablePair getMinMax(Level level, ItemStack stack) {
        return new IntIntImmutablePair(min, max);
    }

    @Override
    public boolean isValid() {
        return min >= MIN_BOUND && max >= MIN_BOUND && min <= MAX_BOUND && max <= MAX_BOUND && min <= max;
    }
}
