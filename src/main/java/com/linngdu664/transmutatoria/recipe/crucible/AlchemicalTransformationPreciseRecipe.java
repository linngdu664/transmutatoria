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

public record AlchemicalTransformationPreciseRecipe(
        ItemStackTemplate input,
        ItemStackTemplate output,
        boolean oneTime,
        LevelFunction level,
        int minPolarity,
        int maxPolarity
) implements CrucibleRecipe, Recipe<CrucibleRecipeInput> {
    public static final MapCodec<AlchemicalTransformationPreciseRecipe> MAP_CODEC = createMapCodec();

    private static MapCodec<AlchemicalTransformationPreciseRecipe> createMapCodec() {
        return RecordCodecBuilder.<AlchemicalTransformationPreciseRecipe>mapCodec(inst -> inst.group(
                ItemStackTemplate.CODEC.fieldOf("input").forGetter(AlchemicalTransformationPreciseRecipe::input),
                ItemStackTemplate.CODEC.fieldOf("output").forGetter(AlchemicalTransformationPreciseRecipe::output),
                Codec.BOOL.optionalFieldOf("one_time", false).forGetter(AlchemicalTransformationPreciseRecipe::oneTime),
                LevelFunction.CODEC.optionalFieldOf("level", new FixedLevel(2, 2)).forGetter(AlchemicalTransformationPreciseRecipe::level),
                Codec.INT.optionalFieldOf("min_polarity", -50).forGetter(AlchemicalTransformationPreciseRecipe::minPolarity),
                Codec.INT.optionalFieldOf("max_polarity", 50).forGetter(AlchemicalTransformationPreciseRecipe::maxPolarity)
        ).apply(inst, AlchemicalTransformationPreciseRecipe::new))
                .validate(recipe -> recipe.isValid()
                        ? DataResult.success(recipe)
                        : DataResult.error(() -> "Invalid alchemical_transformation_precise recipe: min_polarity=" + recipe.minPolarity() + ", max_polarity=" + recipe.maxPolarity() + ", level_valid=" + recipe.level().isValid()));
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalTransformationPreciseRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, AlchemicalTransformationPreciseRecipe::input,
            ItemStackTemplate.STREAM_CODEC, AlchemicalTransformationPreciseRecipe::output,
            ByteBufCodecs.BOOL, AlchemicalTransformationPreciseRecipe::oneTime,
            LevelFunction.STREAM_CODEC, AlchemicalTransformationPreciseRecipe::level,
            ByteBufCodecs.VAR_INT, AlchemicalTransformationPreciseRecipe::minPolarity,
            ByteBufCodecs.VAR_INT, AlchemicalTransformationPreciseRecipe::maxPolarity,
            AlchemicalTransformationPreciseRecipe::new
    );

    @Override
    public ItemStack getOtherSideItemStack() {
        return output.create();
    }

    // 判断某个物品是否匹配该规则（匹配的是输入物品）
    @Override
    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItemSameComponents(stack, input);
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
        return PlacementInfo.create(Ingredient.of(input.item().value()));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<AlchemicalTransformationPreciseRecipe> getSerializer() {
        return InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_SERIALIZER.get();
    }

    @Override
    public RecipeType<AlchemicalTransformationPreciseRecipe> getType() {
        return InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE.get();
    }
}
