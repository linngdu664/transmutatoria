package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.resources.Identifier;

public class AlchemicalJeiTypes {
    public static final Identifier ALCHEMICAL_REPLICATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_replication");
    public static final Identifier ALCHEMICAL_TRANSFORMATION_UID = ArsTransmutatoria.makeMyIdentifier("alchemical_transformation");

    // 注意：泛型变成了 JEIXxxDisplay
    public static final IRecipeType<JEIAlchemicalReplicationDisplay> ALCHEMICAL_REPLICATION =
            IRecipeType.create(ALCHEMICAL_REPLICATION_UID, JEIAlchemicalReplicationDisplay.class);
    public static final IRecipeType<JEIAlchemicalTransformationDisplay> ALCHEMICAL_TRANSFORMATION =
            IRecipeType.create(ALCHEMICAL_TRANSFORMATION_UID, JEIAlchemicalTransformationDisplay.class);
}
