package com.linngdu664.transmutatoria.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class AlchemicalTransformationCategory implements IRecipeCategory<JEIAlchemicalTransformationDisplay> {
    @Override
    public IRecipeType<JEIAlchemicalTransformationDisplay> getRecipeType() {
        return AlchemicalJeiTypes.ALCHEMICAL_TRANSFORMATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.transmutatoria.alchemical_transformation");
    }

    @Override
    public int getWidth() {
        return 116; // copy from jei
    }

    @Override
    public int getHeight() {
        return 54;  // copy from jei
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return null;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JEIAlchemicalTransformationDisplay recipe, IFocusGroup focuses) {
        // 输入槽
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 20).add(recipe.displaySourceItem());
        // 输出槽
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20).add(recipe.displayTargetItem());
    }

    @Override
    public void draw(JEIAlchemicalTransformationDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 自定义画图逻辑
    }
}
