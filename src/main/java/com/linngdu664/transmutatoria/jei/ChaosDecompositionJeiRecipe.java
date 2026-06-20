package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

/** JEI view of a replication recipe when it is used by the Philosopher's Stone reaction. */
public record ChaosDecompositionJeiRecipe(RecipeHolder<?> replicationRecipe) {
    public CrucibleRecipe recipe() {
        return (CrucibleRecipe) replicationRecipe.value();
    }
}
