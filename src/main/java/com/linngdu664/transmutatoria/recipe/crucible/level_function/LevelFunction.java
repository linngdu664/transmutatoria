package com.linngdu664.transmutatoria.recipe.crucible.level_function;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
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

    int MIN_BOUND = 2;
    int MAX_BOUND = 24;

    IntIntImmutablePair getMinMax(Level level, ItemStack stack);

    default boolean isValid() {
        return true;
    }

    String type();
}
