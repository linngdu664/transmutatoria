package com.linngdu664.transmutatoria.recipe.crucible.level_function;

import com.linngdu664.transmutatoria.client.tool.RomanNumberRenderer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public record EnchantmentLevel(int baseMin, int baseMax, double scaleMin, double scaleMax) implements LevelFunction {

    public static final MapCodec<EnchantmentLevel> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("base_min", 2).forGetter(EnchantmentLevel::baseMin),
            Codec.INT.optionalFieldOf("base_max", 2).forGetter(EnchantmentLevel::baseMax),
            Codec.DOUBLE.optionalFieldOf("scale_min", 1.0).forGetter(EnchantmentLevel::scaleMin),
            Codec.DOUBLE.optionalFieldOf("scale_max", 1.0).forGetter(EnchantmentLevel::scaleMax)
    ).apply(i, EnchantmentLevel::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentLevel> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EnchantmentLevel::baseMin,
            ByteBufCodecs.VAR_INT, EnchantmentLevel::baseMax,
            ByteBufCodecs.DOUBLE, EnchantmentLevel::scaleMin,
            ByteBufCodecs.DOUBLE, EnchantmentLevel::scaleMax,
            EnchantmentLevel::new
    );

    @Override
    public String type() {
        return "transmutatoria:enchantment";
    }

    @Override
    public IntIntImmutablePair getMinMax(Level level, ItemStack stack) {
        double value = getEnchantIndex(level, stack);
        return new IntIntImmutablePair(Math.max(MIN_BOUND, baseMin + (int) (value * scaleMin)), Math.min(MAX_BOUND, baseMax + (int) (value * scaleMax)));
    }

    @Override
    public boolean isValid() {
        return scaleMin >= 0 && scaleMax >= 0 && scaleMax >= scaleMin;
    }

    private static double getEnchantIndex(Level level, ItemStack stack) {
        // EnchantmentHelper.getAvailableEnchantmentResults(1, stack, level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.IN_ENCHANTING_TABLE).get().stream());
        double value = 0;
        // todo 考虑权重对等级的影响？
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var kv : enchantments.entrySet()) {
            Enchantment enchantment = kv.getKey().value();
            // int weight = enchantment.getWeight();   // 附魔权重是 (0, 1024]
            value += ((double) kv.getIntValue() / (double) enchantment.getMaxLevel());
        }

        enchantments = stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var kv : enchantments.entrySet()) {
            Enchantment enchantment = kv.getKey().value();
            // int weight = enchantment.getWeight();   // 附魔权重是 (0, 1024]
            value += ((double) kv.getIntValue() / (double) enchantment.getMaxLevel());
        }

        return value;
    }

    @Override
    public Component getAlchTooltipComponent() {
        return Component.translatable(
                "jei.transmutatoria.info.level.enchantment.tooltip",
                RomanNumberRenderer.romanOrFallback(baseMin),
                RomanNumberRenderer.romanOrFallback(baseMax),
                scaleMin,
                scaleMax
        );
    }

    @Override
    public Component getDecompTooltipComponent() {
        return Component.translatable(
                "jei.transmutatoria.chaos_decomposition.count.enchantment.tooltip",
                RomanNumberRenderer.romanOrFallback(baseMin),
                RomanNumberRenderer.romanOrFallback(baseMax),
                scaleMin,
                scaleMax
        );
    }

    @Override
    public String toCompactString() {
        return "*";
    }
}
