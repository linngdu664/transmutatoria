package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.EnchantmentLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
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

import java.util.ArrayList;
import java.util.List;

public class ChaosDecompositionCategory extends AbstractRecipeCategory<ChaosDecompositionJeiRecipe> {
    public ChaosDecompositionCategory(IGuiHelper guiHelper) {
        super(
                AlchemicalJeiTypes.CHAOS_DECOMPOSITION,
                Component.translatable("jei.transmutatoria.chaos_decomposition"),
                guiHelper.createDrawableItemLike(InitItems.PHILOSOPHERS_STONE.get()),
                AlchemicalJeiGraphics.WIDTH,
                AlchemicalJeiGraphics.HEIGHT
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChaosDecompositionJeiRecipe display, IFocusGroup focuses) {
        if (display.replicationRecipe().value() instanceof AlchemicalReplicationRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, AlchemicalJeiGraphics.INPUT_X, AlchemicalJeiGraphics.SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.outputItems());
        } else if (display.replicationRecipe().value() instanceof AlchemicalReplicationPreciseRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, AlchemicalJeiGraphics.INPUT_X, AlchemicalJeiGraphics.SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.output().create());
        }

        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, AlchemicalJeiGraphics.CATALYST_X, AlchemicalJeiGraphics.SLOT_Y)
                .setStandardSlotBackground()
                .add(InitItems.PHILOSOPHERS_STONE.get());
        builder.addSlot(RecipeIngredientRole.OUTPUT, AlchemicalJeiGraphics.OUTPUT_X, AlchemicalJeiGraphics.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStacks(getOutputs());
    }

    @Override
    public void draw(
            ChaosDecompositionJeiRecipe display,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        AlchemicalJeiGraphics.drawBase(
                graphics,
                AlchemicalJeiGraphics.CHAOS_THEME,
                Textures.ALCHEMY_ARRAY_1
        );

        Font font = Minecraft.getInstance().font;
        var theme = AlchemicalJeiGraphics.CHAOS_THEME;
        AlchemicalJeiGraphics.drawSlotLabels(graphics, font, theme);

        LevelFunction level = display.recipe().level();
        Component water = Component.translatable("jei.transmutatoria.chaos_decomposition.water.short");
        Component count = Component.translatable(
                "jei.transmutatoria.chaos_decomposition.count.short",
                level.toCompactString()
        );
        graphics.text(font, water, 7, 63, theme.textColor(), false);
        graphics.text(font, count, AlchemicalJeiGraphics.WIDTH - 7 - font.width(count), 63, theme.textColor(), false);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            ChaosDecompositionJeiRecipe display,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseY >= AlchemicalJeiGraphics.INFO_TOP) {
            if (mouseX < AlchemicalJeiGraphics.WIDTH / 2.0) {
                tooltip.add(Component.translatable("jei.transmutatoria.chaos_decomposition.water.tooltip"));
            } else {
                tooltip.add(display.recipe().level().getDecompTooltipComponent());
            }
        } else if (mouseY >= AlchemicalJeiGraphics.HEADER_BOTTOM && mouseX >= 35 && mouseX < 115) {
            tooltip.add(Component.translatable("jei.transmutatoria.chaos_decomposition.description"));
        }
    }

    @Override
    public Identifier getIdentifier(ChaosDecompositionJeiRecipe display) {
        Identifier source = display.replicationRecipe().id().identifier();
        return Identifier.fromNamespaceAndPath(
                ArsTransmutatoria.MODID,
                "chaos_decomposition/" + source.getNamespace() + "/" + source.getPath()
        );
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
