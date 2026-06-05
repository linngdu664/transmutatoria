package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record AlchemicalTransformationRecipe(
        // 输入二选一即可，优先级 items > namespace，但不能都为空
        Optional<Ingredient> inputItems,
        Optional<String> inputNamespace,
        // 输出不可以为空
        ItemStackTemplate output,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalTransformationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.optionalFieldOf("input_items").forGetter(AlchemicalTransformationRecipe::inputItems),
            Codec.STRING.optionalFieldOf("input_namespace").forGetter(AlchemicalTransformationRecipe::inputNamespace),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlchemicalTransformationRecipe::output),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationRecipe::oneTime),
            LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalTransformationRecipe::level),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationRecipe::maxPolarity)
    ).apply(inst, AlchemicalTransformationRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return output.create();
    }

    // 判断某个物品是否匹配该规则（匹配的是输入物品）
    @Override
    public boolean matches(ItemStack stack) {
        return inputItems
                .map(ingredient -> ingredient.test(stack))
                .orElseGet(() -> inputNamespace
                        .filter(s -> stack.getItem().builtInRegistryHolder().key().identifier().getNamespace().equals(s))
                        .isPresent());
    }
}
