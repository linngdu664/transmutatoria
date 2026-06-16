package com.linngdu664.transmutatoria.recipe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.linngdu664.transmutatoria.client.event.ClientRecipeManager;
import com.linngdu664.transmutatoria.init.InitRecipes;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalEmptyRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Collection;
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
                CrucibleRecipe found = tryLookup(level, InitRecipes.ALCHEMICAL_REPLICATION_PRECISE_TYPE.get(), ClientRecipeManager.replicationPrecises, outputStack);
                if (found != null) return found;
                found = tryLookup(level, InitRecipes.ALCHEMICAL_REPLICATION_TYPE.get(), ClientRecipeManager.replications, outputStack);
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
                CrucibleRecipe found = tryLookup(level, InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE.get(), ClientRecipeManager.transformationPrecises, inputStack);
                if (found != null) return found;
                found = tryLookup(level, InitRecipes.ALCHEMICAL_TRANSFORMATION_TYPE.get(), ClientRecipeManager.transformations, inputStack);
                return found != null ? found : AlchemicalEmptyRecipe.INSTANCE;
            });
            return result instanceof AlchemicalEmptyRecipe ? null : result;
        } catch (ExecutionException e) {
            return null;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static CrucibleRecipe tryLookup(Level level, RecipeType<?> recipeType, Collection<?> clientRecipes, ItemStack stack) {
        Collection<RecipeHolder<?>> recipes;
        MinecraftServer server = level.getServer();
        if (server != null) {
            recipes = (Collection) server.getRecipeManager().recipeMap().byType((RecipeType) recipeType);
        } else if (level.isClientSide()) {
            recipes = (Collection) clientRecipes;
        } else {
            return null;
        }
        for (RecipeHolder<?> recipeHolder : recipes) {
            CrucibleRecipe recipe = (CrucibleRecipe) recipeHolder.value();
            if (recipe.matches(stack)) {
                return recipe;
            }
        }
        return null;
    }
}
