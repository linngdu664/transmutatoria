package com.linngdu664.transmutatoria.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RecipeConditions(boolean oneTime, int minPolarity, int maxPolarity) {
    public static final Codec<RecipeConditions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("one_time").forGetter(RecipeConditions::oneTime),
                    Codec.INT.fieldOf("min_polarity").forGetter(RecipeConditions::minPolarity),
                    Codec.INT.fieldOf("max_polarity").forGetter(RecipeConditions::maxPolarity)
            ).apply(instance, RecipeConditions::new)
    );
    public static final StreamCodec<ByteBuf, RecipeConditions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RecipeConditions::oneTime,
            ByteBufCodecs.VAR_INT, RecipeConditions::minPolarity,
            ByteBufCodecs.VAR_INT, RecipeConditions::maxPolarity,
            RecipeConditions::new
    );

    public static final RecipeConditions DEFAULT = new RecipeConditions(false, -50, 50);
}
