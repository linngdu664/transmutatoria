package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record AlchemicalReplicationRecipe(
        Identifier inputId,
        AlchemicalIOType outputType,
        Identifier outputId,
        boolean oneTime,
        int minLevel,
        int maxLevel,
        int minPolarity,
        int maxPolarity
) implements IAlchemicalRecipe {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalReplicationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Identifier.CODEC.fieldOf("input_id").forGetter(AlchemicalReplicationRecipe::inputId),
            AlchemicalIOType.CODEC.fieldOf("output_type").forGetter(AlchemicalReplicationRecipe::outputType),
            Identifier.CODEC.fieldOf("output_id").forGetter(AlchemicalReplicationRecipe::outputId),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalReplicationRecipe::oneTime),
            Codec.INT.optionalFieldOf("min_level", 2).forGetter(AlchemicalReplicationRecipe::minLevel),
            Codec.INT.optionalFieldOf("max_level", 2).forGetter(AlchemicalReplicationRecipe::maxLevel),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalReplicationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalReplicationRecipe::maxPolarity)
    ).apply(inst, AlchemicalReplicationRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return BuiltInRegistries.ITEM.get(inputId).map(Holder.Reference::value).orElse(Items.AIR).getDefaultInstance();
    }

    // 判断某个物品是否匹配该规则（匹配的是输出物品 outputId）
    public boolean matches(ItemStack stack) {
        return switch (outputType) {
            case ITEM -> stack.getItem().builtInRegistryHolder().key().identifier().equals(outputId);
            case TAG -> stack.is(TagKey.create(Registries.ITEM, outputId));
            case NAMESPACE -> stack.getItem().builtInRegistryHolder().key().identifier().getNamespace().equals(outputId.getNamespace());
        };
    }

    // 取消配方判断
    public boolean isValid() {
        if (minPolarity > maxPolarity) return false;
        return minLevel >= 2 && maxLevel >= 2 && minLevel <= 24 && maxLevel <= 24 && minLevel <= maxLevel;
    }
}
