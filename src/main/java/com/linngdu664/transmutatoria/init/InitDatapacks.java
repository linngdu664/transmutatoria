package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationRecipe;
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
    public static final ResourceKey<Registry<AlchemicalReplicationPreciseRecipe>> ALCHEMICAL_REPLICATION_PRECISE_KEY =
            ResourceKey.createRegistryKey(ArsTransmutatoria.makeMyIdentifier("alchemical_replication_precise_recipe"));
    public static final ResourceKey<Registry<AlchemicalTransformationPreciseRecipe>> ALCHEMICAL_TRANSFORMATION_PRECISE_KEY =
            ResourceKey.createRegistryKey(ArsTransmutatoria.makeMyIdentifier("alchemical_transformation_precise_recipe"));


    @SubscribeEvent
    public static void registerDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ALCHEMICAL_REPLICATION_KEY, AlchemicalReplicationRecipe.CODEC, AlchemicalReplicationRecipe.CODEC);
        event.dataPackRegistry(ALCHEMICAL_TRANSFORMATION_KEY, AlchemicalTransformationRecipe.CODEC, AlchemicalTransformationRecipe.CODEC);
        event.dataPackRegistry(ALCHEMICAL_REPLICATION_PRECISE_KEY, AlchemicalReplicationPreciseRecipe.CODEC, AlchemicalReplicationPreciseRecipe.CODEC);
        event.dataPackRegistry(ALCHEMICAL_TRANSFORMATION_PRECISE_KEY, AlchemicalTransformationPreciseRecipe.CODEC, AlchemicalTransformationPreciseRecipe.CODEC);
    }
}
