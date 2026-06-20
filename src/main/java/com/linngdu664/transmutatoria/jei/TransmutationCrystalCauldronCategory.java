package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.recipe.TransmutationCrystalCauldronProcess;
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
import net.minecraft.world.level.block.Blocks;

public class TransmutationCrystalCauldronCategory extends AbstractRecipeCategory<TransmutationCrystalCauldronJeiRecipe> {
    private static final int WIDTH = 180;
    private static final int HEIGHT = 112;
    private static final int HEADER_BOTTOM = 16;
    private static final int INFO_TOP = 94;
    private static final int INPUT_Y = 47;
    private static final int STATION_X = 94;
    private static final int WATER_Y = 20;
    private static final int CAULDRON_Y = 47;
    private static final int EMERALD_Y = 74;
    private static final int OUTPUT_X = 154;
    private static final int OUTPUT_Y = 47;

    private static final int OUTER_COLOR = 0xFF123528;
    private static final int BODY_COLOR = 0xFFE7F4EA;
    private static final int HEADER_COLOR = 0xFF1F6045;
    private static final int INFO_COLOR = 0xFFCDE7D4;
    private static final int ACCENT_COLOR = 0xFF49A36F;
    private static final int ARROW_COLOR = 0xFF49A36F;
    private static final int TEXT_COLOR = 0xFF204C36;
    private static final int HEADER_TEXT_COLOR = 0xFFF2FFF5;

    public TransmutationCrystalCauldronCategory(IGuiHelper guiHelper) {
        super(
                AlchemicalJeiTypes.TRANSMUTATION_CRYSTAL_CAULDRON,
                Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron"),
                guiHelper.createDrawableItemLike(InitItems.TRANSMUTATION_CRYSTAL.get()),
                WIDTH,
                HEIGHT
        );
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            TransmutationCrystalCauldronJeiRecipe recipe,
            IFocusGroup focuses
    ) {
        for (int i = 0; i < TransmutationCrystalCauldronProcess.REQUIREMENTS.size(); i++) {
            var requirement = TransmutationCrystalCauldronProcess.REQUIREMENTS.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, 16 + i * 18, INPUT_Y)
                    .setStandardSlotBackground()
                    .add(new ItemStack(requirement.item(), requirement.count()));
        }

        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, STATION_X, WATER_Y)
                .setStandardSlotBackground()
                .add(Items.WATER_BUCKET);
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, STATION_X, CAULDRON_Y)
                .setStandardSlotBackground()
                .add(Items.CAULDRON);
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, STATION_X, EMERALD_Y)
                .setStandardSlotBackground()
                .add(Blocks.EMERALD_BLOCK);

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .setOutputSlotBackground()
                .add(new ItemStack(
                        InitItems.TRANSMUTATION_CRYSTAL.get(),
                        TransmutationCrystalCauldronProcess.RESULT_COUNT
                ));
    }

    @Override
    public void draw(
            TransmutationCrystalCauldronJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        drawPanel(graphics);
        drawRightArrow(graphics, 73, 91, INPUT_Y + 8);
        drawRightArrow(graphics, 116, 149, CAULDRON_Y + 8);
        drawDownArrow(graphics, STATION_X + 8, 39, 46);
        drawDownArrow(graphics, STATION_X + 8, 66, 73);

        Font font = Minecraft.getInstance().font;
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.ingredients"), 42, 5, HEADER_TEXT_COLOR);
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.apparatus"), STATION_X + 8, 5, HEADER_TEXT_COLOR);
        drawCentered(graphics, font, Component.translatable("jei.transmutatoria.output"), OUTPUT_X + 8, 5, HEADER_TEXT_COLOR);

        Component drop = Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.drop.short");
        Component water = Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.water.short");
        graphics.text(font, drop, 7, 101, TEXT_COLOR, false);
        graphics.text(font, water, WIDTH - 7 - font.width(water), 101, TEXT_COLOR, false);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            TransmutationCrystalCauldronJeiRecipe recipe,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        if (mouseY >= INFO_TOP) {
            if (mouseX < WIDTH / 2.0) {
                tooltip.add(Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.drop.tooltip"));
            } else {
                tooltip.add(Component.translatable("jei.transmutatoria.transmutation_crystal_cauldron.water.tooltip"));
            }
        }
    }

    @Override
    public Identifier getIdentifier(TransmutationCrystalCauldronJeiRecipe recipe) {
        return AlchemicalJeiTypes.TRANSMUTATION_CRYSTAL_CAULDRON_UID;
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private static void drawPanel(GuiGraphicsExtractor graphics) {
        graphics.fill(0, 0, WIDTH, HEIGHT, OUTER_COLOR);
        graphics.fill(1, 1, WIDTH - 1, HEIGHT - 1, BODY_COLOR);
        graphics.fill(2, 2, WIDTH - 2, HEADER_BOTTOM, HEADER_COLOR);
        graphics.fill(2, HEADER_BOTTOM - 1, WIDTH - 2, HEADER_BOTTOM, ACCENT_COLOR);
        graphics.fill(2, INFO_TOP, WIDTH - 2, HEIGHT - 2, INFO_COLOR);
        graphics.fill(2, INFO_TOP, WIDTH - 2, INFO_TOP + 1, ACCENT_COLOR);
    }

    private static void drawRightArrow(GuiGraphicsExtractor graphics, int startX, int endX, int centerY) {
        graphics.fill(startX, centerY - 1, endX - 2, centerY + 1, ARROW_COLOR);
        graphics.fill(endX - 7, centerY - 5, endX - 5, centerY + 5, ARROW_COLOR);
        graphics.fill(endX - 5, centerY - 3, endX - 3, centerY + 3, ARROW_COLOR);
        graphics.fill(endX - 3, centerY - 1, endX, centerY + 1, ARROW_COLOR);
    }

    private static void drawDownArrow(GuiGraphicsExtractor graphics, int centerX, int startY, int endY) {
        graphics.fill(centerX - 1, startY, centerX + 1, endY - 3, ARROW_COLOR);
        graphics.fill(centerX - 4, endY - 5, centerX + 4, endY - 3, ARROW_COLOR);
        graphics.fill(centerX - 2, endY - 3, centerX + 2, endY - 1, ARROW_COLOR);
        graphics.fill(centerX - 1, endY - 1, centerX + 1, endY, ARROW_COLOR);
    }

    private static void drawCentered(GuiGraphicsExtractor graphics, Font font, Component text, int centerX, int y, int color) {
        graphics.text(font, text, centerX - font.width(text) / 2, y, color, false);
    }
}
