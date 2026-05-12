package com.linngdu664.transmutatoria.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CatalystShapelessRecipe implements CraftingRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final ItemStackTemplate result;
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<Ingredient> catalysts;

    public CatalystShapelessRecipe(String group, CraftingBookCategory category, ItemStackTemplate result,
                                   List<Ingredient> ingredients, List<Ingredient> catalysts) {
        this.group = group;
        this.category = category;
        this.result = result;
        this.ingredients = NonNullList.copyOf(ingredients);
        this.catalysts = NonNullList.copyOf(catalysts);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                boolean isCatalyst = false;
                for (Ingredient catalyst : catalysts) {
                    if (catalyst.test(stack)) {
                        remaining.set(i, stack.copyWithCount(1));
                        isCatalyst = true;
                        break;
                    }
                }
                if (!isCatalyst) {
                    ItemStackTemplate remainder = stack.getItem().getCraftingRemainder();
                    if (remainder != null && remainder.item() != null) {
                        remaining.set(i, remainder.create());
                    }
                }
            }
        }
        return remaining;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                inputItems.add(stack);
            }
        }
        List<Ingredient> allIngredients = new ArrayList<>(ingredients.size() + catalysts.size());
        allIngredients.addAll(ingredients);
        allIngredients.addAll(catalysts);

        if (inputItems.size() != allIngredients.size()) {
            return false;
        }
        return matchShapeless(inputItems, allIngredients);
    }

    private static boolean matchShapeless(List<ItemStack> inputs, List<Ingredient> ingredientList) {
        boolean[] used = new boolean[ingredientList.size()];
        for (ItemStack input : inputs) {
            boolean matched = false;
            for (int i = 0; i < ingredientList.size(); i++) {
                if (!used[i] && ingredientList.get(i).test(input)) {
                    used[i] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return result.create();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public RecipeSerializer<CatalystShapelessRecipe> getSerializer() {
        return InitRecipes.CATALYST_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    public CraftingBookCategory category() {
        return category;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static final MapCodec<CatalystShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(r -> r.group),
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(r -> r.category),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(r -> r.ingredients),
            Ingredient.CODEC.listOf().fieldOf("catalysts").forGetter(r -> r.catalysts)
    ).apply(instance, CatalystShapelessRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CatalystShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, r -> r.group,
            CraftingBookCategory.STREAM_CODEC, r -> r.category,
            ItemStackTemplate.STREAM_CODEC, r -> r.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.catalysts,
            CatalystShapelessRecipe::new
    );
}
