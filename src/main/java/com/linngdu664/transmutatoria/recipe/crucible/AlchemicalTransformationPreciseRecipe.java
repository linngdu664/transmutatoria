package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record AlchemicalTransformationPreciseRecipe(
        ItemStackTemplate input,
        // 输出不可以为空
        ItemStackTemplate output,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalTransformationPreciseRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStackTemplate.CODEC.fieldOf("input").forGetter(AlchemicalTransformationPreciseRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlchemicalTransformationPreciseRecipe::output),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationPreciseRecipe::oneTime),
            LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalTransformationPreciseRecipe::level),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationPreciseRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationPreciseRecipe::maxPolarity)
    ).apply(inst, AlchemicalTransformationPreciseRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return output.create();
    }

    // 判断某个物品是否匹配该规则（匹配的是输入物品）
    @Override
    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, input);
    }
}
