package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Optional;

public record AlchemicalReplicationRecipe(
        // 输入可以为空
        Optional<ItemStackTemplate> input,
        // 输出二选一即可，优先级 items > namespace，但不能都为空
        Optional<Ingredient> outputItems,
        Optional<String> outputNamespace,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalReplicationRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStackTemplate.CODEC.optionalFieldOf("input").forGetter(AlchemicalReplicationRecipe::input),
            Ingredient.CODEC.optionalFieldOf("output_items").forGetter(AlchemicalReplicationRecipe::outputItems),
            Codec.STRING.optionalFieldOf("output_namespace").forGetter(AlchemicalReplicationRecipe::outputNamespace),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalReplicationRecipe::oneTime),
            LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalReplicationRecipe::level),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalReplicationRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalReplicationRecipe::maxPolarity)
    ).apply(inst, AlchemicalReplicationRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return input.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }

    // 判断某个物品是否匹配该规则（匹配的是输出物品）
    @Override
    public boolean matches(ItemStack stack) {
        return outputItems
                .map(ingredient -> ingredient.test(stack))
                .orElseGet(() -> outputNamespace
                        .filter(s -> stack.getItem().builtInRegistryHolder().key().identifier().getNamespace().equals(s))
                        .isPresent());
    }
}
