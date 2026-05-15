package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.AlchemicalTransformationRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record JEIAlchemicalTransformationDisplay(AlchemicalTransformationRecipe recipe, ItemStack displaySourceItem, ItemStack displayTargetItem) implements Comparable<JEIAlchemicalTransformationDisplay> {
    @Override
    public int compareTo(@NonNull JEIAlchemicalTransformationDisplay o) {
        return Item.getId(displaySourceItem.getItem()) - Item.getId(o.displaySourceItem.getItem());
    }
}
