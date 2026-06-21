package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AlchemicalJeiTypes {
    public static final Identifier ALCHEMICAL_REPLICATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_replication");
    public static final Identifier ALCHEMICAL_TRANSFORMATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_transformation");
    public static final Identifier TRANSMUTATION_DECOMPOSITION_UID = ArsTransmutatoria.makeMyIdentifier("transmutation_decomposition");
    public static final Identifier CHAOS_DECOMPOSITION_UID = ArsTransmutatoria.makeMyIdentifier("chaos_decomposition");
    public static final Identifier ESSENCE_FUSION_UID = ArsTransmutatoria.makeMyIdentifier("essence_fusion");
    public static final Identifier TRANSMUTATION_CRYSTAL_CAULDRON_UID = ArsTransmutatoria.makeMyIdentifier("transmutation_crystal_cauldron");

    public static final IRecipeType<TransmutationDecompositionJeiRecipe> TRANSMUTATION_DECOMPOSITION =
            IRecipeType.create(TRANSMUTATION_DECOMPOSITION_UID, TransmutationDecompositionJeiRecipe.class);
    public static final IRecipeType<ChaosDecompositionJeiRecipe> CHAOS_DECOMPOSITION =
            IRecipeType.create(CHAOS_DECOMPOSITION_UID, ChaosDecompositionJeiRecipe.class);
    public static final IRecipeType<EssenceFusionJeiRecipe> ESSENCE_FUSION =
            IRecipeType.create(ESSENCE_FUSION_UID, EssenceFusionJeiRecipe.class);
    public static final IRecipeType<TransmutationCrystalCauldronJeiRecipe> TRANSMUTATION_CRYSTAL_CAULDRON =
            IRecipeType.create(TRANSMUTATION_CRYSTAL_CAULDRON_UID, TransmutationCrystalCauldronJeiRecipe.class);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final IRecipeType<RecipeHolder<?>> ALCHEMICAL_TRANSFORMATION =
            (IRecipeType) IRecipeType.create(ALCHEMICAL_TRANSFORMATION_UID, RecipeHolder.class);
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final IRecipeType<RecipeHolder<?>> ALCHEMICAL_REPLICATION =
            (IRecipeType) IRecipeType.create(ALCHEMICAL_REPLICATION_UID, RecipeHolder.class);
}
