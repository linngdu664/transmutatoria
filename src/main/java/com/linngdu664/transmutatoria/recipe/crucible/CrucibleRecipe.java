package com.linngdu664.transmutatoria.recipe.crucible;

import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import net.minecraft.world.item.ItemStack;

public interface CrucibleRecipe {
    int MIN_POLARITY = -50;
    int MAX_POLARITY = 50;

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
