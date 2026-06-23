package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.init.InitRecipes;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record AlchemicalReplicationRecipe(
        Optional<ItemStackTemplate> input,
        Ingredient outputItems,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe, Recipe<CrucibleRecipeInput> {
    public static final MapCodec<AlchemicalReplicationRecipe> MAP_CODEC = createMapCodec();

    private static MapCodec<AlchemicalReplicationRecipe> createMapCodec() {
        return RecordCodecBuilder.<AlchemicalReplicationRecipe>mapCodec(inst -> inst.group(
                ItemStackTemplate.CODEC.optionalFieldOf("input").forGetter(AlchemicalReplicationRecipe::input),
                Ingredient.CODEC.fieldOf("output_items").forGetter(AlchemicalReplicationRecipe::outputItems),
                Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalReplicationRecipe::oneTime),
                LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalReplicationRecipe::level),
                Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalReplicationRecipe::minPolarity),
                Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalReplicationRecipe::maxPolarity)
        ).apply(inst, AlchemicalReplicationRecipe::new))
                .validate(recipe -> recipe.isValid()
                        ? DataResult.success(recipe)
                        : DataResult.error(() -> "Invalid alchemical_replication recipe: min_polarity=" + recipe.minPolarity() + ", max_polarity=" + recipe.maxPolarity() + ", level_valid=" + recipe.level().isValid()));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalReplicationRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC), AlchemicalReplicationRecipe::input,
            Ingredient.CONTENTS_STREAM_CODEC, AlchemicalReplicationRecipe::outputItems,
            ByteBufCodecs.BOOL, AlchemicalReplicationRecipe::oneTime,
            LevelFunction.STREAM_CODEC, AlchemicalReplicationRecipe::level,
            ByteBufCodecs.VAR_INT, AlchemicalReplicationRecipe::minPolarity,
            ByteBufCodecs.VAR_INT, AlchemicalReplicationRecipe::maxPolarity,
            AlchemicalReplicationRecipe::new
    );

    @Override
    public ItemStack getOtherSideItemStack() {
        return input.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
    }

    // 判断某个物品是否匹配该规则（匹配的是输出物品）
    @Override
    public boolean matches(ItemStack stack) {
        return outputItems.test(stack);
    }

    @Override
    public boolean matches(CrucibleRecipeInput input, Level level) {
        return matches(input.stack());
    }

    @Override
    public ItemStack assemble(CrucibleRecipeInput input) {
        return getOtherSideItemStack();
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(outputItems);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<AlchemicalReplicationRecipe> getSerializer() {
        return InitRecipes.ALCHEMICAL_REPLICATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<AlchemicalReplicationRecipe> getType() {
        return InitRecipes.ALCHEMICAL_REPLICATION_TYPE.get();
    }
}
