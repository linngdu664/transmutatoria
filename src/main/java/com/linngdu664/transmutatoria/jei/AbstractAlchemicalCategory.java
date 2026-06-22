package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.init.InitTags;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Shared presentation for crucible datapack recipes.
 *
 * <p>The ingredient slots remain normal JEI slots, so focuses, bookmarks and
 * recipe lookups keep working. Everything around them is presentation only.</p>
 */
public abstract class AbstractAlchemicalCategory extends AbstractRecipeCategory<RecipeHolder<?>> {
    protected static final int WIDTH = AlchemicalJeiGraphics.RECIPE_WIDTH;
    protected static final int HEIGHT = AlchemicalJeiGraphics.RECIPE_HEIGHT;
    protected static final int INPUT_X = AlchemicalJeiGraphics.RECIPE_INPUT_X;
    protected static final int CATALYST_X = AlchemicalJeiGraphics.RECIPE_CATALYST_X;
    protected static final int OUTPUT_X = AlchemicalJeiGraphics.RECIPE_OUTPUT_X;
    protected static final int SLOT_Y = AlchemicalJeiGraphics.RECIPE_SLOT_Y;

    private static final int HEADER_BOTTOM = AlchemicalJeiGraphics.HEADER_BOTTOM;
    private static final int INFO_TOP = AlchemicalJeiGraphics.INFO_TOP;

    protected AbstractAlchemicalCategory(
            IGuiHelper guiHelper,
            IRecipeType<RecipeHolder<?>> recipeType,
            Component title,
            ItemStack icon
    ) {
        super(recipeType, title, guiHelper.createDrawableItemStack(icon), WIDTH, HEIGHT);
    }

    protected abstract Component getDescriptionTooltip();

    protected abstract TextureRenderable getMark();

    @Override
    public void draw(
            RecipeHolder<?> holder,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor graphics,
            double mouseX,
            double mouseY
    ) {
        CrucibleRecipe recipe = getCrucibleRecipe(holder);
        if (recipe == null) {
            return;
        }

        AlchemicalJeiGraphics.drawRecipeBase(graphics, AlchemicalJeiGraphics.PARCHMENT_THEME, getMark());
        drawLabels(graphics, recipe);
    }

    @Override
    public void getTooltip(
            ITooltipBuilder tooltip,
            RecipeHolder<?> holder,
            IRecipeSlotsView recipeSlotsView,
            double mouseX,
            double mouseY
    ) {
        CrucibleRecipe recipe = getCrucibleRecipe(holder);
        if (recipe == null) {
            return;
        }

        if (mouseY >= INFO_TOP) {
            if (recipe.oneTime() && mouseX >= WIDTH * 0.5 - 12 && mouseX < WIDTH * 0.5 + 12) {
                tooltip.add(Component.translatable("jei.transmutatoria.info.one_time.tooltip"));
            } else if (mouseX < WIDTH * 0.5) {
                tooltip.add(recipe.level().getAlchTooltipComponent());
            } else {
                tooltip.add(Component.translatable(
                        "jei.transmutatoria.info.polarity.tooltip",
                        signed(recipe.minPolarity()),
                        signed(recipe.maxPolarity())
                ));
            }
        } else if (mouseY >= HEADER_BOTTOM && mouseX >= 64 && mouseX < 131) {
            tooltip.add(getDescriptionTooltip());
        }
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private void drawLabels(GuiGraphicsExtractor graphics, CrucibleRecipe recipe) {
        Font font = Minecraft.getInstance().font;
        AlchemicalJeiGraphics.drawRecipeSlotLabels(graphics, font, AlchemicalJeiGraphics.PARCHMENT_THEME);

        if (recipe.oneTime()) {
            Component oneTime = Component.literal("1x");
            graphics.text(font, oneTime, (WIDTH - font.width(oneTime)) / 2, 63, AlchemicalJeiGraphics.PARCHMENT_THEME.textColor(), false);
        }

        Component level = Component.translatable("jei.transmutatoria.info.level.short", recipe.level().toCompactString());
        Component polarity = Component.translatable(
                "jei.transmutatoria.info.polarity.short",
                signed(recipe.minPolarity()) + ".." + signed(recipe.maxPolarity())
        );
        int textColor = AlchemicalJeiGraphics.PARCHMENT_THEME.textColor();
        graphics.text(font, level, 7, 63, textColor, false);
        graphics.text(font, polarity, WIDTH - 7 - font.width(polarity), 63, textColor, false);
    }

    private static CrucibleRecipe getCrucibleRecipe(RecipeHolder<?> holder) {
        return holder.value() instanceof CrucibleRecipe recipe ? recipe : null;
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }

    protected static void addEssenceInputSlot(IRecipeLayoutBuilder builder) {
        builder.addSlot(
                        RecipeIngredientRole.INPUT,
                        AlchemicalJeiGraphics.RECIPE_INPUT_ESSENCE_X,
                        AlchemicalJeiGraphics.RECIPE_ESSENCE_SLOT_Y
                )
                .setStandardSlotBackground()
                .add(new SlotDisplay.TagSlotDisplay(InitTags.ALL_ESSENCE_METALS))
                .addRichTooltipCallback((slot, tooltip) -> tooltip.add(
                        Component.translatable("jei.transmutatoria.info.all_essence_metals.tooltip")
                ))
                .setSlotName("all_essence_metals");
    }
}
