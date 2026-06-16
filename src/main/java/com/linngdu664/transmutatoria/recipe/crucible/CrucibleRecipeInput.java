package com.linngdu664.transmutatoria.recipe.crucible;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record CrucibleRecipeInput(ItemStack stack) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index != 0) throw new IllegalArgumentException("No item for index " + index);
        return stack;
    }

    @Override
    public int size() {
        return 1;
    }
}
