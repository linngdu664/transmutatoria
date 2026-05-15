package com.linngdu664.transmutatoria.recipe;

import com.linngdu664.transmutatoria.init.InitDatapacks;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class AlchemicalRecipeManager {
    public static AlchemicalReplicationRecipe findMatchRep(Level level, ItemStack stack) {
        var lookupOpt = level.registryAccess().lookup(InitDatapacks.ALCHEMICAL_REPLICATION_KEY);
        if (lookupOpt.isEmpty()) return null;

        // 1. 每次查询先收集成 List
        List<AlchemicalReplicationRecipe> recipes = lookupOpt.get().listElements()
                .map(Holder.Reference::value)
                .toList();

        // 2. 倒序遍历：后加载的优先级更高（摆烂解法）
        for (int i = recipes.size() - 1; i >= 0; i--) {
            AlchemicalReplicationRecipe recipe = recipes.get(i);
            if (recipe.matches(stack)) {
                if (!recipe.isValid()) {
                    return null;
                }
                return recipe;
            }
        }
        return null;
    }

    public static AlchemicalTransformationRecipe findMatchTrans(Level level, ItemStack stack) {
        var lookupOpt = level.registryAccess().lookup(InitDatapacks.ALCHEMICAL_TRANSFORMATION_KEY);
        if (lookupOpt.isEmpty()) return null;

        // 1. 每次查询先收集成 List
        List<AlchemicalTransformationRecipe> recipes = lookupOpt.get().listElements()
                .map(Holder.Reference::value)
                .toList();

        // 2. 倒序遍历：后加载的优先级更高（摆烂解法）
        for (int i = recipes.size() - 1; i >= 0; i--) {
            AlchemicalTransformationRecipe recipe = recipes.get(i);
            if (recipe.matches(stack)) {
                if (!recipe.isValid()) {
                    return null;
                }
                return recipe;
            }
        }
        return null;
    }
}
