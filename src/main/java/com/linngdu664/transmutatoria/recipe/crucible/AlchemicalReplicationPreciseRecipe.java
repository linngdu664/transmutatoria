package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.Optional;

public record AlchemicalReplicationPreciseRecipe(
        // 输入可以为空
        Optional<ItemStackTemplate> input,
        ItemStackTemplate output,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe {
    // 定义 Codec 用于 JSON 解析
    public static final Codec<AlchemicalReplicationPreciseRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStackTemplate.CODEC.optionalFieldOf("input").forGetter(AlchemicalReplicationPreciseRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlchemicalReplicationPreciseRecipe::output),
            Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalReplicationPreciseRecipe::oneTime),
            LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalReplicationPreciseRecipe::level),
            Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalReplicationPreciseRecipe::minPolarity),
            Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalReplicationPreciseRecipe::maxPolarity)
    ).apply(inst, AlchemicalReplicationPreciseRecipe::new));

    @Override
    public ItemStack getOtherSideItemStack() {
        return input.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }

    // 判断某个物品是否匹配该规则（匹配的是输出物品）
    @Override
    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, output);
    }
}
