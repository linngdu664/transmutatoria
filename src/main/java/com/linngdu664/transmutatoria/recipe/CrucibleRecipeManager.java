package com.linngdu664.transmutatoria.recipe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.linngdu664.transmutatoria.init.InitDatapacks;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalEmptyRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class CrucibleRecipeManager {
    private static final Cache<ItemStackTemplate, CrucibleRecipe> repCache = CacheBuilder.newBuilder().maximumSize(1048576).build();
    private static final Cache<ItemStackTemplate, CrucibleRecipe> transCache = CacheBuilder.newBuilder().maximumSize(1048576).build();

    public static void invalidateCache() {
        repCache.invalidateAll();
        transCache.invalidateAll();
    }

    public static CrucibleRecipe findMatchRep(Level level, ItemStack outputStack) {
        try {
            var result = repCache.get(new ItemStackTemplate(outputStack.getItem(), outputStack.getComponentsPatch()), () -> {
                RegistryAccess registryAccess = level.registryAccess();
                CrucibleRecipe found = tryLookup(registryAccess, InitDatapacks.ALCHEMICAL_REPLICATION_PRECISE_KEY, outputStack);
                if (found != null) return found;
                found = tryLookup(registryAccess, InitDatapacks.ALCHEMICAL_REPLICATION_KEY, outputStack);
                return found != null ? found : AlchemicalEmptyRecipe.INSTANCE;
            });
            return result instanceof AlchemicalEmptyRecipe ? null : result;
        } catch (ExecutionException e) {
            return null;
        }
    }

    public static CrucibleRecipe findMatchTrans(Level level, ItemStack inputStack) {
        try {
            var result = transCache.get(new ItemStackTemplate(inputStack.getItem(), inputStack.getComponentsPatch()), () -> {
                RegistryAccess registryAccess = level.registryAccess();
                CrucibleRecipe found = tryLookup(registryAccess, InitDatapacks.ALCHEMICAL_TRANSFORMATION_PRECISE_KEY, inputStack);
                if (found != null) return found;
                found = tryLookup(registryAccess, InitDatapacks.ALCHEMICAL_TRANSFORMATION_KEY, inputStack);
                return found != null ? found : AlchemicalEmptyRecipe.INSTANCE;
            });
            return result instanceof AlchemicalEmptyRecipe ? null : result;
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Nullable
    private static CrucibleRecipe tryLookup(RegistryAccess registryAccess, ResourceKey<? extends Registry<? extends CrucibleRecipe>> key, ItemStack stack) {
        var lookupOpt = registryAccess.lookup(key);
        return lookupOpt.map(iAlchemicalRecipes -> innerLookup(iAlchemicalRecipes, stack)).orElse(null);
    }

    private static CrucibleRecipe innerLookup(Registry<? extends CrucibleRecipe> lookup, ItemStack stack) {
        // 1. 先收集成 List
        List<? extends CrucibleRecipe> recipes = lookup.listElements()
                .map(Holder.Reference::value)
                .toList();

        // 2. 倒序遍历：后加载的优先级更高（摆烂解法）
        for (int i = recipes.size() - 1; i >= 0; i--) {
            CrucibleRecipe recipe = recipes.get(i);
            if (recipe.matches(stack)) {
                if (recipe.isValid()) {
                    return recipe;
                }
                return null;
            }
        }
        return null;
    }
}
