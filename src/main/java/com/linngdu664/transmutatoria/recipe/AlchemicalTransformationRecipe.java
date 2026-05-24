package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

public record AlchemicalTransformationRecipe(
        AlchemicalIOType inputType,
        Identifier inputId,
        Identifier outputId,
        boolean oneTime,
        int minLevel,
        int maxLevel,
        int minPolarity,
        int maxPolarity
) implements IAlchemicalRecipe {
    public static final AlchemicalTransformationRecipe EMPTY_MARKER = new AlchemicalTransformationRecipe(
            AlchemicalIOType.ITEM,
            Identifier.fromNamespaceAndPath("minecraft", "air"),
            Identifier.fromNamespaceAndPath("minecraft", "air"),
            false,
            0,
            0,
            0,
            0
    );

    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalTransformationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            AlchemicalIOType.CODEC.fieldOf("input_type").forGetter(AlchemicalTransformationRecipe::inputType),
            Identifier.CODEC.fieldOf("input_id").forGetter(AlchemicalTransformationRecipe::inputId),
            Identifier.CODEC.fieldOf("output_id").forGetter(AlchemicalTransformationRecipe::outputId),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationRecipe::oneTime),
            Codec.INT.optionalFieldOf("min_level", 2).forGetter(AlchemicalTransformationRecipe::minLevel),
            Codec.INT.optionalFieldOf("max_level", 2).forGetter(AlchemicalTransformationRecipe::maxLevel),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationRecipe::maxPolarity)
    ).apply(inst, AlchemicalTransformationRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return BuiltInRegistries.ITEM.getValue(outputId).getDefaultInstance();
    }

    // 判断某个物品是否匹配该规则（匹配的是输入物品 inputId）
    public boolean matches(ItemStack stack) {
        return switch (inputType) {
            case ITEM -> stack.getItem().builtInRegistryHolder().key().identifier().equals(inputId);
            case TAG -> stack.is(TagKey.create(Registries.ITEM, inputId));
            case NAMESPACE -> stack.getItem().builtInRegistryHolder().key().identifier().getNamespace().equals(inputId.getNamespace());
        };
    }

    // 取消配方判断
    public boolean isValid() {
        if (minPolarity > maxPolarity) return false;
        return minLevel >= 2 && maxLevel >= 2 && minLevel <= 24 && maxLevel <= 24 && minLevel <= maxLevel;
    }
}
