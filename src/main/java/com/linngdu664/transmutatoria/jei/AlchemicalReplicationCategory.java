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
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class AlchemicalReplicationCategory implements IRecipeCategory<JEIAlchemicalReplicationDisplay> {

    @Override
    public IRecipeType<JEIAlchemicalReplicationDisplay> getRecipeType() {
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
    public void setRecipe(IRecipeLayoutBuilder builder, JEIAlchemicalReplicationDisplay recipe, IFocusGroup focuses) {
        ItemStack item = recipe.displayItem();

        // 输入槽
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 20).add(item);

        // 输出槽 (不消耗输入的复制，数量+1表示产出)
        ItemStack output = item.copy();
        output.setCount(output.getCount() + 1);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20).add(output);
    }

    @Override
    public void draw(JEIAlchemicalReplicationDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 自定义画图逻辑
    }
}
