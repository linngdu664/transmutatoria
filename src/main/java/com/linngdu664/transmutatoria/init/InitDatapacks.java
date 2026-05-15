package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.recipe.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.AlchemicalTransformationRecipe;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class InitDatapacks {
    public static final ResourceKey<Registry<AlchemicalReplicationRecipe>> ALCHEMICAL_REPLICATION_KEY =
            ResourceKey.createRegistryKey(ArsTransmutatoria.makeMyIdentifier("alchemical_replication_recipe"));
    public static final ResourceKey<Registry<AlchemicalTransformationRecipe>> ALCHEMICAL_TRANSFORMATION_KEY =
            ResourceKey.createRegistryKey(ArsTransmutatoria.makeMyIdentifier("alchemical_transformation_recipe"));


    @SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ALCHEMICAL_REPLICATION_KEY, AlchemicalReplicationRecipe.CODEC, AlchemicalReplicationRecipe.CODEC);
        event.dataPackRegistry(ALCHEMICAL_TRANSFORMATION_KEY, AlchemicalTransformationRecipe.CODEC, AlchemicalTransformationRecipe.CODEC);
    }
}
