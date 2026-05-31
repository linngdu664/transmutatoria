package com.linngdu664.transmutatoria.recipe;

import com.linngdu664.transmutatoria.init.InitDatapacks;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AlchemicalRecipeManager {
    // 为了保险，用 ConcurrentHashMap
    private static final ConcurrentHashMap<Item, AlchemicalReplicationRecipe> repCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Item, AlchemicalTransformationRecipe> transCache = new ConcurrentHashMap<>();

    public static void invalidateCache() {
        repCache.clear();
        transCache.clear();
    }

    public static AlchemicalReplicationRecipe findMatchRep(Level level, ItemStack outputStack) {
        var result = repCache.computeIfAbsent(outputStack.getItem(), _ -> {
            var lookupOpt = level.registryAccess().lookup(InitDatapacks.ALCHEMICAL_REPLICATION_KEY);
            if (lookupOpt.isEmpty()) return AlchemicalReplicationRecipe.EMPTY_MARKER;

            // 1. 先收集成 List
            List<AlchemicalReplicationRecipe> recipes = lookupOpt.get().listElements()
                    .map(Holder.Reference::value)
                    .toList();

            // 2. 倒序遍历：后加载的优先级更高（摆烂解法）
            for (int i = recipes.size() - 1; i >= 0; i--) {
                AlchemicalReplicationRecipe recipe = recipes.get(i);
                if (recipe.matches(outputStack)) {
                    if (recipe.isValid()) {
                        return recipe;
                    }
                    return AlchemicalReplicationRecipe.EMPTY_MARKER;
                }
            }
            return AlchemicalReplicationRecipe.EMPTY_MARKER;
        });
        return result == AlchemicalReplicationRecipe.EMPTY_MARKER ? null : result;
    }

    public static AlchemicalTransformationRecipe findMatchTrans(Level level, ItemStack inputStack) {
        var result = transCache.computeIfAbsent(inputStack.getItem(), _ -> {
            var lookupOpt = level.registryAccess().lookup(InitDatapacks.ALCHEMICAL_TRANSFORMATION_KEY);
            if (lookupOpt.isEmpty()) return AlchemicalTransformationRecipe.EMPTY_MARKER;

            // 1. 先收集成 List
            List<AlchemicalTransformationRecipe> recipes = lookupOpt.get().listElements()
                    .map(Holder.Reference::value)
                    .toList();

            // 2. 倒序遍历：后加载的优先级更高（摆烂解法）
            for (int i = recipes.size() - 1; i >= 0; i--) {
                AlchemicalTransformationRecipe recipe = recipes.get(i);
                if (recipe.matches(inputStack)) {
                    if (recipe.isValid()) {
                        return recipe;
                    }
                    return AlchemicalTransformationRecipe.EMPTY_MARKER;
                }
            }
            return AlchemicalTransformationRecipe.EMPTY_MARKER;
        });
        return result == AlchemicalTransformationRecipe.EMPTY_MARKER ? null : result;
    }
}
