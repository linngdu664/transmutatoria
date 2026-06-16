package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AlchemicalJeiTypes {
    public static final Identifier ALCHEMICAL_REPLICATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_replication");
    public static final Identifier ALCHEMICAL_TRANSFORMATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_transformation");

    @SuppressWarnings("unchecked")
    public static final IRecipeType<RecipeHolder<?>> ALCHEMICAL_TRANSFORMATION =
            (IRecipeType) IRecipeType.create(ALCHEMICAL_TRANSFORMATION_UID, RecipeHolder.class);
    @SuppressWarnings("unchecked")
    public static final IRecipeType<RecipeHolder<?>> ALCHEMICAL_REPLICATION =
            (IRecipeType) IRecipeType.create(ALCHEMICAL_REPLICATION_UID, RecipeHolder.class);
}
