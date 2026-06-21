package com.linngdu664.transmutatoria.recipe.crucible.level_function;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface LevelFunction {
    Codec<LevelFunction> CODEC = Codec.STRING.dispatch(
            "type",
            LevelFunction::type,
            type -> switch (type) {
                case "transmutatoria:fixed" -> FixedLevel.CODEC;
                case "transmutatoria:enchantment" -> EnchantmentLevel.CODEC;
                default -> throw new IllegalArgumentException("Unknown level function type: " + type);
            }
    );

    StreamCodec<RegistryFriendlyByteBuf, LevelFunction> STREAM_CODEC = StreamCodec.of(
            (buf, value) -> {
                ByteBufCodecs.STRING_UTF8.encode(buf, value.type());
                switch (value.type()) {
                    case "transmutatoria:fixed" -> FixedLevel.STREAM_CODEC.encode(buf, (FixedLevel) value);
                    case "transmutatoria:enchantment" -> EnchantmentLevel.STREAM_CODEC.encode(buf, (EnchantmentLevel) value);
                    default -> throw new IllegalArgumentException("Unknown level function type: " + value.type());
                }
            },
            buf -> {
                String type = ByteBufCodecs.STRING_UTF8.decode(buf);
                return switch (type) {
                    case "transmutatoria:fixed" -> FixedLevel.STREAM_CODEC.decode(buf);
                    case "transmutatoria:enchantment" -> EnchantmentLevel.STREAM_CODEC.decode(buf);
                    default -> throw new IllegalArgumentException("Unknown level function type: " + type);
                };
            }
    );

    int MIN_BOUND = 2;
    int MAX_BOUND = 24;

    IntIntImmutablePair getMinMax(Level level, ItemStack stack);

    String type();

    default boolean isValid() {
        return true;
    }

    default Component getAlchTooltipComponent() {
        return Component.translatable("jei.transmutatoria.info.level.unknown.tooltip", type());
    }

    default Component getDecompTooltipComponent() {
        return Component.translatable("jei.transmutatoria.chaos_decomposition.dynamic.tooltip");
    }

    default String toCompactString() {
        return "?";
    }
}
