package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

public class AlchemicalReplicationCategory implements IRecipeCategory<RecipeHolder<?>> {
    @Override
    public IRecipeType<RecipeHolder<?>> getRecipeType() {
        return AlchemicalJeiTypes.ALCHEMICAL_REPLICATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.transmutatoria.alchemical_replication");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<?> holder, IFocusGroup focuses) {
        if (holder.value() instanceof AlchemicalReplicationRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, 20, 20).add(recipe.getOtherSideItemStack());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20).add(recipe.outputItems());
        } else if (holder.value() instanceof AlchemicalReplicationPreciseRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, 20, 20).add(recipe.getOtherSideItemStack());
            builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20).add(recipe.output().create());
        }
    }

    @Override
    public void draw(RecipeHolder<?> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
    }
}
