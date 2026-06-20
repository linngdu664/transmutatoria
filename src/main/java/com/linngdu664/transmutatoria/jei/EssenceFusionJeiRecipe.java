package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record EssenceFusionJeiRecipe(EssenceMetal essence, int state) {
    public ItemStack catalyst() {
        return essence.getItemStack(state);
    }

    public List<EssenceMetal> requiredEssences() {
        return List.copyOf(essence.getRestrainsAndDoubleRestrains());
    }
}
