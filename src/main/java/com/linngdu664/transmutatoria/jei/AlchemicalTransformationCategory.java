package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalTransformationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class AlchemicalTransformationCategory extends AbstractAlchemicalCategory {
    public AlchemicalTransformationCategory(IGuiHelper guiHelper) {
        super(
                guiHelper,
                AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION,
                Component.translatable("jei.transmutatoria.alchemical_transformation"),
                InitItems.TRANSMUTATION_EQUATION_SCROLL.get().getDefaultInstance(),
                false
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<?> holder, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, CATALYST_X, SLOT_Y)
                .setStandardSlotBackground()
                .addItemStacks(getCatalysts());

        if (holder.value() instanceof AlchemicalTransformationRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.inputItems());
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                    .setOutputSlotBackground()
                    .add(recipe.getOtherSideItemStack());
        } else if (holder.value() instanceof AlchemicalTransformationPreciseRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.input().create());
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                    .setOutputSlotBackground()
                    .add(recipe.getOtherSideItemStack());
        }
    }

    private static List<ItemStack> getCatalysts() {
        return List.of(
                InitItems.TRANSMUTATION_EQUATION_SCROLL.get().getDefaultInstance(),
                InitItems.TERRESTRIAL_EQUATION_SCROLL.get().getDefaultInstance(),
                InitItems.LUNAR_EQUATION_SCROLL.get().getDefaultInstance(),
                InitItems.SOLAR_EQUATION_SCROLL.get().getDefaultInstance(),
                InitItems.VOID_EQUATION_SCROLL.get().getDefaultInstance()
        );
    }
}
