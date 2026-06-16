package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.recipe.CatalystShapelessRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalTransformationPreciseRecipe>> ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE =
            RECIPE_TYPES.register("alchemical_transformation_precise", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MODID + ":alchemical_transformation_precise";
                }
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalTransformationRecipe>> ALCHEMICAL_TRANSFORMATION_TYPE =
            RECIPE_TYPES.register("alchemical_transformation", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MODID + ":alchemical_transformation";
                }
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalReplicationRecipe>> ALCHEMICAL_REPLICATION_TYPE =
            RECIPE_TYPES.register("alchemical_replication", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MODID + ":alchemical_replication";
                }
            });
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlchemicalReplicationPreciseRecipe>> ALCHEMICAL_REPLICATION_PRECISE_TYPE =
            RECIPE_TYPES.register("alchemical_replication_precise", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return MODID + ":alchemical_replication_precise";
                }
            });


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalTransformationPreciseRecipe>> ALCHEMICAL_TRANSFORMATION_PRECISE_SERIALIZER =
            RECIPE_SERIALIZERS.register("alchemical_transformation_precise", () -> new RecipeSerializer<>(
                    AlchemicalTransformationPreciseRecipe.MAP_CODEC,
                    AlchemicalTransformationPreciseRecipe.STREAM_CODEC
            ));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalTransformationRecipe>> ALCHEMICAL_TRANSFORMATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("alchemical_transformation", () -> new RecipeSerializer<>(
                    AlchemicalTransformationRecipe.MAP_CODEC,
                    AlchemicalTransformationRecipe.STREAM_CODEC
            ));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalReplicationPreciseRecipe>> ALCHEMICAL_REPLICATION_PRECISE_SERIALIZER =
            RECIPE_SERIALIZERS.register("alchemical_replication_precise", () -> new RecipeSerializer<>(
                    AlchemicalReplicationPreciseRecipe.MAP_CODEC,
                    AlchemicalReplicationPreciseRecipe.STREAM_CODEC
            ));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AlchemicalReplicationRecipe>> ALCHEMICAL_REPLICATION_SERIALIZER =
            RECIPE_SERIALIZERS.register("alchemical_replication", () -> new RecipeSerializer<>(
                    AlchemicalReplicationRecipe.MAP_CODEC,
                    AlchemicalReplicationRecipe.STREAM_CODEC
            ));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CatalystShapelessRecipe>> CATALYST_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("catalyst_shapeless", () -> new RecipeSerializer<>(
                    CatalystShapelessRecipe.CODEC,
                    CatalystShapelessRecipe.STREAM_CODEC
            ));
}
