package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.util.EssenceMetal;
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

import java.util.List;

public class EssenceFusionCategory extends AbstractRecipeCategory<EssenceFusionJeiRecipe> {
    private static final int WIDTH = 166;
    private static final int HEIGHT = 120;
    private static final int INFO_TOP = 100;
    private static final int INPUT_X = 6;
    private static final int INPUT_Y = 22;
    private static final int INPUT_COLUMNS = 3;
    private static final int CATALYST_X = 92;
    private static final int OUTPUT_X = 138;
    private static final int CENTER_SLOT_Y = 50;

    public EssenceFusionCategory(IGuiHelper guiHelper) {
        super(
                AlchemicalJeiTypes.ESSENCE_FUSION,
                Component.translatable("jei.transmutatoria.essence_fusion"),
                guiHelper.createDrawableItemLike(InitItems.PANDEMONIUM.get()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EssenceFusionJeiRecipe recipe, IFocusGroup focuses) {
        List<EssenceMetal> required = recipe.requiredEssences();
        for (int i = 0; i < required.size(); i++) {
            EssenceMetal essence = required.get(i);
            builder.addSlot(
                            RecipeIngredientRole.INPUT,
                            INPUT_X + i % INPUT_COLUMNS * 18,
                            INPUT_Y + i / INPUT_COLUMNS * 18
                    )
                    .setStandardSlotBackground()
                    .addItemStacks(getAllStates(essence));
        }

        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, CATALYST_X, CENTER_SLOT_Y)
                .setStandardSlotBackground()
                .add(recipe.catalyst());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, CENTER_SLOT_Y)
                .setOutputSlotBackground()
                .add(recipe.catalyst());
    }

    @Override
    public void draw(
            EssenceFusionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        var theme = AlchemicalJeiGraphics.FUSION_THEME;
        AlchemicalJeiGraphics.drawCustomBase(
                graphics,
                theme,
                Textures.ALCHEMY_ARRAY_8,
                WIDTH,
                HEIGHT,
                WIDTH / 2,
                (AlchemicalJeiGraphics.HEADER_BOTTOM + INFO_TOP) / 2,
                INFO_TOP
        );
        int arrowY = CENTER_SLOT_Y + 8;
        AlchemicalJeiGraphics.drawArrowBetween(graphics, 66, 85, arrowY, theme.arrowColor());
        AlchemicalJeiGraphics.drawArrowBetween(graphics, 112, 131, arrowY, theme.arrowColor());

        Font font = Minecraft.getInstance().font;
        AlchemicalJeiGraphics.drawCentered(
                graphics,
                font,
                Component.translatable("jei.transmutatoria.essence_fusion.required"),
                32,
                5,
                theme.headerTextColor()
        );
        AlchemicalJeiGraphics.drawCentered(
                graphics,
                font,
                Component.translatable("jei.transmutatoria.catalyst"),
                CATALYST_X + 8,
                5,
                theme.headerTextColor()
        );
        AlchemicalJeiGraphics.drawCentered(
                graphics,
                font,
                Component.translatable("jei.transmutatoria.output"),
                OUTPUT_X + 8,
                5,
                theme.headerTextColor()
        );

        Component water = Component.translatable("jei.transmutatoria.essence_fusion.water.short");
        Component count = Component.translatable(
                "jei.transmutatoria.essence_fusion.input_count.short",
                recipe.requiredEssences().size()
        );
        graphics.text(font, water, 7, 107, theme.textColor(), false);
        graphics.text(font, count, WIDTH - 7 - font.width(count), 107, theme.textColor(), false);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            EssenceFusionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseY >= INFO_TOP) {
            if (mouseX < WIDTH / 2.0) {
                tooltip.add(Component.translatable("jei.transmutatoria.essence_fusion.water.tooltip"));
            } else {
                tooltip.add(Component.translatable(
                        "jei.transmutatoria.essence_fusion.input_count.tooltip",
                        recipe.requiredEssences().size()
                ));
            }
        } else if (mouseY >= AlchemicalJeiGraphics.HEADER_BOTTOM) {
            tooltip.add(Component.translatable("jei.transmutatoria.essence_fusion.description"));
        }
    }

    @Override
    public Identifier getIdentifier(EssenceFusionJeiRecipe recipe) {
        return Identifier.fromNamespaceAndPath(
                ArsTransmutatoria.MODID,
                "essence_fusion/" + recipe.essence().getKey() + "/" + recipe.state()
        );
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private static List<ItemStack> getAllStates(EssenceMetal essence) {
        return List.of(
                essence.getItemStack(-1),
                essence.getItemStack(0),
                essence.getItemStack(1),
                essence.getItemStack(2)
        );
    }
}
