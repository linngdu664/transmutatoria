package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.AlchemicalReplicationRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public record JEIAlchemicalReplicationDisplay(AlchemicalReplicationRecipe recipe, ItemStack displayInputItem, ItemStack displayOutputItem) implements Comparable<JEIAlchemicalReplicationDisplay> {
    @Override
    public int compareTo(@NonNull JEIAlchemicalReplicationDisplay o) {
        return Item.getId(displayOutputItem.getItem()) - Item.getId(o.displayOutputItem.getItem());
    }
}
