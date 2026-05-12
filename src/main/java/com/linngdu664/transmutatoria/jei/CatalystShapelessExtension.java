package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.CatalystShapelessRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;

public class CatalystShapelessExtension implements ICraftingCategoryExtension<CatalystShapelessRecipe> {

    @Override
    public List<SlotDisplay> getIngredients(RecipeHolder<CatalystShapelessRecipe> recipeHolder) {
        CatalystShapelessRecipe recipe = recipeHolder.value();
        List<SlotDisplay> displays = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            displays.add(ingredient.display());
        }
        for (Ingredient catalyst : recipe.getCatalysts()) {
            displays.add(catalyst.display());
        }
        return displays;
    }

    @Override
    public void setRecipe(RecipeHolder<CatalystShapelessRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        CatalystShapelessRecipe recipe = recipeHolder.value();
        List<Ingredient> ingredients = recipe.getIngredients();
        List<Ingredient> catalysts = recipe.getCatalysts();
        int ingCount = ingredients.size();
        int catCount = catalysts.size();
        int ingRows = ingCount > 0 ? (ingCount - 1) / 3 + 1 : 0;

        for (int i = 0; i < ingCount; i++) {
            int x = 1 + (i % 3) * 18;
            int y = 1 + (i / 3) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                    .add(ingredients.get(i))
                    .setStandardSlotBackground();
        }

        int catY = 1 + ingRows * 18 + 4;
        for (int i = 0; i < catCount; i++) {
            int x = 1 + (i % 3) * 18;
            builder.addSlot(RecipeIngredientRole.INPUT, x, catY)
                    .add(catalysts.get(i))
                    .setStandardSlotBackground();
        }

        int resultX = 95;
        int resultY = 27; // centered in the default 72-height area
        builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, resultY)
                .add(recipe.getResult())
                .setOutputSlotBackground();

        builder.setShapeless();
    }

    @Override
    public void drawInfo(RecipeHolder<CatalystShapelessRecipe> recipeHolder, int recipeWidth, int recipeHeight,
                         GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        CatalystShapelessRecipe recipe = recipeHolder.value();
        int ingCount = recipe.getIngredients().size();
        int ingRows = ingCount > 0 ? (ingCount - 1) / 3 + 1 : 0;
        if (ingCount > 0 && recipe.getCatalysts().size() > 0) {
            int lineY = 2 + ingRows * 18;
            guiGraphics.fill(1, lineY, 55, lineY + 1, 0xFF9E9E9E);
        }
    }
}