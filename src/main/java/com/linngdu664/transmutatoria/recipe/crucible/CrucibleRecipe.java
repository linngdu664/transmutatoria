package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import net.minecraft.world.item.ItemStack;

public interface CrucibleRecipe {
    boolean oneTime();
    LevelFunction level();
    int minPolarity();
    int maxPolarity();
    boolean matches(ItemStack stack);
    ItemStack getOtherSideItemStack();

    default boolean isValid() {
        return minPolarity() <= maxPolarity() && level().isValid();
    }
}
