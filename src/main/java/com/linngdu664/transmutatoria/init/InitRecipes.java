package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.recipe.CatalystShapelessRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CatalystShapelessRecipe>> CATALYST_SHAPELESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("catalyst_shapeless", () -> new RecipeSerializer<>(
                    CatalystShapelessRecipe.CODEC,
                    CatalystShapelessRecipe.STREAM_CODEC
            ));
}
