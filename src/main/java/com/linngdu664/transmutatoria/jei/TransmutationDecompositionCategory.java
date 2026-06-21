package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TransmutationDecompositionCategory extends AbstractRecipeCategory<TransmutationDecompositionJeiRecipe> {
    public TransmutationDecompositionCategory(IGuiHelper guiHelper) {
        super(
                AlchemicalJeiTypes.TRANSMUTATION_DECOMPOSITION,
                Component.translatable("jei.transmutatoria.transmutation_decomposition"),
                guiHelper.createDrawableItemLike(InitItems.TRANSMUTATION_CRYSTAL.get()),
                AlchemicalJeiGraphics.WIDTH,
                AlchemicalJeiGraphics.HEIGHT
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            TransmutationDecompositionJeiRecipe recipe,
            IFocusGroup focuses
    ) {
        builder.addSlot(RecipeIngredientRole.INPUT, AlchemicalJeiGraphics.INPUT_X, AlchemicalJeiGraphics.SLOT_Y)
                .setStandardSlotBackground()
                .add(InitItems.TRANSMUTATION_CRYSTAL.get());
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, AlchemicalJeiGraphics.CATALYST_X, AlchemicalJeiGraphics.SLOT_Y)
                .setStandardSlotBackground()
                .add(Items.ENDER_EYE);
        builder.addSlot(RecipeIngredientRole.OUTPUT, AlchemicalJeiGraphics.OUTPUT_X, AlchemicalJeiGraphics.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStacks(getOutputs());
    }

    @Override
    public void draw(
            TransmutationDecompositionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        AlchemicalJeiGraphics.drawBase(
                graphics,
                AlchemicalJeiGraphics.DECOMPOSITION_THEME,
                Textures.ALCHEMY_ARRAY_2
        );

        Font font = Minecraft.getInstance().font;
        var theme = AlchemicalJeiGraphics.DECOMPOSITION_THEME;
        AlchemicalJeiGraphics.drawSlotLabels(graphics, font, theme);

        Component water = Component.translatable("jei.transmutatoria.transmutation_decomposition.water.short");
        Component chance = Component.translatable("jei.transmutatoria.transmutation_decomposition.chance.short");
        Component time = Component.translatable("jei.transmutatoria.transmutation_decomposition.time.short");
        graphics.text(font, water, 7, 63, theme.textColor(), false);
        AlchemicalJeiGraphics.drawCentered(graphics, font, chance, AlchemicalJeiGraphics.WIDTH / 2, 63, theme.textColor());
        graphics.text(font, time, AlchemicalJeiGraphics.WIDTH - 7 - font.width(time), 63, theme.textColor(), false);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            TransmutationDecompositionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseY >= AlchemicalJeiGraphics.INFO_TOP) {
            if (mouseX < AlchemicalJeiGraphics.WIDTH / 3.0) {
                tooltip.add(Component.translatable("jei.transmutatoria.transmutation_decomposition.water.tooltip"));
            } else if (mouseX < AlchemicalJeiGraphics.WIDTH * (2.0 / 3.0)) {
                tooltip.add(Component.translatable("jei.transmutatoria.transmutation_decomposition.chance.tooltip"));
            } else {
                tooltip.add(Component.translatable("jei.transmutatoria.transmutation_decomposition.time.tooltip"));
            }
        } else if (mouseY >= AlchemicalJeiGraphics.HEADER_BOTTOM && mouseX >= 35 && mouseX < 115) {
            tooltip.add(Component.translatable("jei.transmutatoria.transmutation_decomposition.description"));
        }
    }

    @Override
    public Identifier getIdentifier(TransmutationDecompositionJeiRecipe recipe) {
        return AlchemicalJeiTypes.TRANSMUTATION_DECOMPOSITION_UID;
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private static List<ItemStack> getOutputs() {
        List<ItemStack> outputs = new ArrayList<>(InitItems.ESSENCE_METAL_ITEMS.length);
        for (var item : InitItems.ESSENCE_METAL_ITEMS) {
            outputs.add(item.toStack());
        }
        return outputs;
    }
}
