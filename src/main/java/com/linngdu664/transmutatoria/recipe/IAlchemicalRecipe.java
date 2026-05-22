package com.linngdu664.transmutatoria.recipe;

import net.minecraft.world.item.ItemStack;

public interface IAlchemicalRecipe {
    ItemStack getOtherSideItemStack();
    boolean oneTime();
    int minLevel();
    int maxLevel();
    int minPolarity();
    int maxPolarity();
}
