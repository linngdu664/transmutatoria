package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import net.minecraft.world.item.ItemStack;

public final class AlchemicalEmptyRecipe implements CrucibleRecipe {
    public static AlchemicalEmptyRecipe INSTANCE = new AlchemicalEmptyRecipe();

    private AlchemicalEmptyRecipe() {}

    @Override
    public boolean oneTime() {
        return false;
    }

    @Override
    public LevelFunction level() {
        return new FixedLevel(0, 0);
    }

    @Override
    public int minPolarity() {
        return 0;
    }

    @Override
    public int maxPolarity() {
        return 0;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack getOtherSideItemStack() {
        return null;
    }
}
