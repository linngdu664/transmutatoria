package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record JEIAlchemicalTransformationDisplay(CrucibleRecipe recipe, ItemStack displayInputItem, ItemStack displayOutputItem) implements Comparable<JEIAlchemicalTransformationDisplay> {
    @Override
    public int compareTo(@NonNull JEIAlchemicalTransformationDisplay o) {
        return Item.getId(displayInputItem.getItem()) - Item.getId(o.displayInputItem.getItem());
    }
}
