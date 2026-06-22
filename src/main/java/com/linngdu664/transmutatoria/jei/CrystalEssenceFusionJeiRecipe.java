package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.util.EssenceMetal;

public record CrystalEssenceFusionJeiRecipe(
        EssenceMetal first,
        int firstState,
        EssenceMetal second,
        int secondState
) {
    public EssenceMetal.Relation relation() {
        return first.getRelationTo(second);
    }
}
