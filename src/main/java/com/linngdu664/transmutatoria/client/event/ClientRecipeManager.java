package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitRecipes;
import com.linngdu664.transmutatoria.recipe.CrucibleRecipeManager;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsTransmutatoria.MODID)
public class ClientRecipeManager {
    public static List<RecipeHolder<AlchemicalReplicationPreciseRecipe>> replicationPrecises;
    public static List<RecipeHolder<AlchemicalReplicationRecipe>> replications;
    public static List<RecipeHolder<AlchemicalTransformationPreciseRecipe>> transformationPrecises;
    public static List<RecipeHolder<AlchemicalTransformationRecipe>> transformations;

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        RecipeMap recipeMap = event.getRecipeMap();
        replicationPrecises = new ArrayList<>(recipeMap.byType(InitRecipes.ALCHEMICAL_REPLICATION_PRECISE_TYPE.get()));
        replications = new ArrayList<>(recipeMap.byType(InitRecipes.ALCHEMICAL_REPLICATION_TYPE.get()));
        transformationPrecises = new ArrayList<>(recipeMap.byType(InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE.get()));
        transformations = new ArrayList<>(recipeMap.byType(InitRecipes.ALCHEMICAL_TRANSFORMATION_TYPE.get()));
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        replicationPrecises = null;
        replications = null;
        transformationPrecises = null;
        transformations = null;
        CrucibleRecipeManager.invalidateCache();
    }
}
