package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.event.ClientRecipeManager;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@JeiPlugin
public class JEITransmutatoriaPlugin implements IModPlugin {

    @Override
    public Identifier getPluginUid() {
        return ArsTransmutatoria.makeMyIdentifier("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new TransmutationCrystalCauldronCategory(guiHelper));
        registration.addRecipeCategories(new TransmutationDecompositionCategory(guiHelper));
        registration.addRecipeCategories(new CrystalEssenceFusionCategory(guiHelper));
        registration.addRecipeCategories(new EssenceFusionCategory(guiHelper));
        registration.addRecipeCategories(new AlchemicalReplicationCategory(guiHelper));
        registration.addRecipeCategories(new AlchemicalTransformationCategory(guiHelper));
        registration.addRecipeCategories(new ChaosDecompositionCategory(guiHelper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(
                AlchemicalJeiTypes.ALCHEMICAL_REPLICATION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.TRANSMUTATION_DECOMPOSITION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.CHAOS_DECOMPOSITION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.ESSENCE_FUSION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.TRANSMUTATION_CRYSTAL_CAULDRON,
                Items.CAULDRON,
                Blocks.EMERALD_BLOCK
        );
        registration.addCraftingStation(
                AlchemicalJeiTypes.CRYSTAL_ESSENCE_FUSION,
                InitBlocks.TRANSMUTATION_CRUCIBLE.get()
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RecipeHolder<?>> transList = new ArrayList<>();
        List<RecipeHolder<?>> repList = new ArrayList<>();

        transList.addAll(ClientRecipeManager.transformationPrecises);
        transList.addAll(ClientRecipeManager.transformations);
        repList.addAll(ClientRecipeManager.replicationPrecises);
        repList.addAll(ClientRecipeManager.replications);

        registration.addRecipes(
                AlchemicalJeiTypes.TRANSMUTATION_CRYSTAL_CAULDRON,
                List.of(TransmutationCrystalCauldronJeiRecipe.INSTANCE)
        );
        registration.addRecipes(
                AlchemicalJeiTypes.TRANSMUTATION_DECOMPOSITION,
                List.of(TransmutationDecompositionJeiRecipe.INSTANCE)
        );
        registration.addRecipes(
                AlchemicalJeiTypes.CRYSTAL_ESSENCE_FUSION,
                getCrystalEssenceFusionRecipes()
        );
        registration.addRecipes(
                AlchemicalJeiTypes.ESSENCE_FUSION,
                Arrays.stream(EssenceMetal.values())
                        .flatMap(essence -> IntStream.rangeClosed(-1, 2)
                                .mapToObj(state -> new EssenceFusionJeiRecipe(essence, state)))
                        .toList()
        );
        registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION, transList);
        registration.addRecipes(AlchemicalJeiTypes.ALCHEMICAL_REPLICATION, repList);
        registration.addRecipes(
                AlchemicalJeiTypes.CHAOS_DECOMPOSITION,
                repList.stream().map(ChaosDecompositionJeiRecipe::new).toList()
        );
    }

    private static List<CrystalEssenceFusionJeiRecipe> getCrystalEssenceFusionRecipes() {
        EssenceMetal[] essences = EssenceMetal.values();
        int distinctPairCount = essences.length * (essences.length - 1) / 2;
        List<CrystalEssenceFusionJeiRecipe> recipes = new ArrayList<>(distinctPairCount * 16);
        for (int first = 0; first < essences.length; first++) {
            for (int second = first + 1; second < essences.length; second++) {
                if (essences[first].getRelationTo(essences[second]) == EssenceMetal.Relation.NEUTRAL) {
                    continue;
                }
                for (int firstState = -1; firstState <= 2; firstState++) {
                    for (int secondState = -1; secondState <= 2; secondState++) {
                        recipes.add(new CrystalEssenceFusionJeiRecipe(
                                essences[first], firstState,
                                essences[second], secondState
                        ));
                    }
                }
            }
        }
        return recipes;
    }
}
