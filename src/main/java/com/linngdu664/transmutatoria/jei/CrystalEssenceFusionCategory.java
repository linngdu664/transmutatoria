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

public class CrystalEssenceFusionCategory extends AbstractRecipeCategory<CrystalEssenceFusionJeiRecipe> {
    private static final int WIDTH = 188;
    private static final int HEIGHT = 82;
    private static final int INFO_TOP = 63;
    private static final int SLOT_Y = 31;
    private static final int INPUT_1_X = 12;
    private static final int INPUT_2_X = 34;
    private static final int CATALYST_X = 82;
    private static final int OUTPUT_1_X = 132;
    private static final int OUTPUT_2_X = 158;

    private static final AlchemicalJeiGraphics.Theme THEME = new AlchemicalJeiGraphics.Theme(
            0xFF102A46,
            0xFFE6F1FA,
            0xFF1B568C,
            0xFFC5DFF2,
            0xFF4B9DDB,
            0xFF63B7F0,
            0xFF183F60,
            0xFFF0F9FF,
            0xD0FFFFFF
    );

    public CrystalEssenceFusionCategory(IGuiHelper guiHelper) {
        super(
                AlchemicalJeiTypes.CRYSTAL_ESSENCE_FUSION,
                Component.translatable("jei.transmutatoria.crystal_essence_fusion"),
                guiHelper.createDrawableItemLike(InitItems.TRANSMUTATION_CRYSTAL.get()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrystalEssenceFusionJeiRecipe recipe, IFocusGroup focuses) {
        EssenceMetal.Relation relation = recipe.relation();

        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_1_X, SLOT_Y)
                .setStandardSlotBackground()
                .add(recipe.first().getItemStack(recipe.firstState()));
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_2_X, SLOT_Y)
                .setStandardSlotBackground()
                .add(recipe.second().getItemStack(recipe.secondState()));
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, CATALYST_X, SLOT_Y)
                .setStandardSlotBackground()
                .add(InitItems.TRANSMUTATION_CRYSTAL.get());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_1_X, SLOT_Y)
                .setOutputSlotBackground()
                .add(recipe.first().getItemStack(recipe.firstState() + relation.self));
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_2_X, SLOT_Y)
                .setOutputSlotBackground()
                .add(recipe.second().getItemStack(recipe.secondState() + relation.other));
    }

    @Override
    public void draw(
            CrystalEssenceFusionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        int centerY = SLOT_Y + 8;
        AlchemicalJeiGraphics.drawCustomBase(
                graphics,
                THEME,
                Textures.ALCHEMY_ARRAY_6,
                WIDTH,
                HEIGHT,
                CATALYST_X + 8,
                centerY,
                INFO_TOP
        );
        AlchemicalJeiGraphics.drawArrowBetween(graphics, 56, 79, centerY, THEME.arrowColor());
        AlchemicalJeiGraphics.drawArrowBetween(graphics, 104, 127, centerY, THEME.arrowColor());

        Font font = Minecraft.getInstance().font;
        AlchemicalJeiGraphics.drawCentered(graphics, font, Component.translatable("jei.transmutatoria.input"), 31, 5, THEME.headerTextColor());
        AlchemicalJeiGraphics.drawCentered(graphics, font, Component.translatable("jei.transmutatoria.catalyst"), CATALYST_X + 8, 5, THEME.headerTextColor());
        AlchemicalJeiGraphics.drawCentered(graphics, font, Component.translatable("jei.transmutatoria.output"), 153, 5, THEME.headerTextColor());

        Component water = Component.translatable("jei.transmutatoria.crystal_essence_fusion.water.short");
        Component polarity = Component.translatable(
                "jei.transmutatoria.crystal_essence_fusion.polarity.short",
                signed(recipe.relation().self + recipe.relation().other)
        );
        graphics.text(font, water, 7, 69, THEME.textColor(), false);
        graphics.text(font, polarity, WIDTH - 7 - font.width(polarity), 69, THEME.textColor(), false);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            CrystalEssenceFusionJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseY >= INFO_TOP) {
            if (mouseX < WIDTH / 2.0) {
                tooltip.add(Component.translatable("jei.transmutatoria.crystal_essence_fusion.water.tooltip"));
            } else {
                tooltip.add(Component.translatable(
                        "jei.transmutatoria.crystal_essence_fusion.polarity.tooltip",
                        signed(recipe.relation().self + recipe.relation().other)
                ));
            }
        } else if (mouseY >= AlchemicalJeiGraphics.HEADER_BOTTOM && mouseX >= 54 && mouseX < 130) {
            tooltip.add(Component.translatable("jei.transmutatoria.crystal_essence_fusion.description"));
        }
    }

    @Override
    public Identifier getIdentifier(CrystalEssenceFusionJeiRecipe recipe) {
        return Identifier.fromNamespaceAndPath(
                ArsTransmutatoria.MODID,
                "crystal_essence_fusion/"
                        + recipe.first().getKey() + "/" + recipe.firstState() + "/"
                        + recipe.second().getKey() + "/" + recipe.secondState()
        );
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }
}
