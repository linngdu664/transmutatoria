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

public record AlchemicalTransformationRecipe(
        Ingredient inputItems,
        ItemStackTemplate output,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe, Recipe<CrucibleRecipeInput> {
    public static final MapCodec<AlchemicalTransformationRecipe> MAP_CODEC = createMapCodec();

    private static MapCodec<AlchemicalTransformationRecipe> createMapCodec() {
        return RecordCodecBuilder.<AlchemicalTransformationRecipe>mapCodec(inst -> inst.group(
                Ingredient.CODEC.fieldOf("input_items").forGetter(AlchemicalTransformationRecipe::inputItems),
                ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlchemicalTransformationRecipe::output),
                Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationRecipe::oneTime),
                LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalTransformationRecipe::level),
                Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationRecipe::minPolarity),
                Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationRecipe::maxPolarity)
        ).apply(inst, AlchemicalTransformationRecipe::new))
                .validate(recipe -> recipe.isValid()
                        ? DataResult.success(recipe)
                        : DataResult.error(() -> "Invalid alchemical_transformation recipe: min_polarity=" + recipe.minPolarity() + ", max_polarity=" + recipe.maxPolarity() + ", level_valid=" + recipe.level().isValid()));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalTransformationRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AlchemicalTransformationRecipe::inputItems,
            ItemStackTemplate.STREAM_CODEC, AlchemicalTransformationRecipe::output,
            ByteBufCodecs.BOOL, AlchemicalTransformationRecipe::oneTime,
            LevelFunction.STREAM_CODEC, AlchemicalTransformationRecipe::level,
            ByteBufCodecs.VAR_INT, AlchemicalTransformationRecipe::minPolarity,
            ByteBufCodecs.VAR_INT, AlchemicalTransformationRecipe::maxPolarity,
            AlchemicalTransformationRecipe::new
    );

    @Override
    public ItemStack getOtherSideItemStack() {
        return output.create();
    }

    // 判断某个物品是否匹配该规则（匹配的是输入物品）
    @Override
    public boolean matches(ItemStack stack) {
        return inputItems.test(stack);
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
        return PlacementInfo.create(inputItems);
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<AlchemicalTransformationRecipe> getSerializer() {
        return InitRecipes.ALCHEMICAL_TRANSFORMATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<AlchemicalTransformationRecipe> getType() {
        return InitRecipes.ALCHEMICAL_TRANSFORMATION_TYPE.get();
    }
}
