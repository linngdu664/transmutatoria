package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.client.tool.RomanNumberRenderer;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.EnchantmentLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.FixedLevel;
import com.linngdu664.transmutatoria.recipe.crucible.level_function.LevelFunction;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * Shared presentation for crucible datapack recipes.
 *
 * <p>The ingredient slots remain normal JEI slots, so focuses, bookmarks and
 * recipe lookups keep working. Everything around them is presentation only.</p>
 */
public abstract class AbstractAlchemicalCategory extends AbstractRecipeCategory<RecipeHolder<?>> {
    protected static final int WIDTH = AlchemicalJeiGraphics.WIDTH;
    protected static final int HEIGHT = AlchemicalJeiGraphics.HEIGHT;
    protected static final int INPUT_X = AlchemicalJeiGraphics.INPUT_X;
    protected static final int CATALYST_X = AlchemicalJeiGraphics.CATALYST_X;
    protected static final int OUTPUT_X = AlchemicalJeiGraphics.OUTPUT_X;
    protected static final int SLOT_Y = AlchemicalJeiGraphics.SLOT_Y;

    private static final int HEADER_BOTTOM = AlchemicalJeiGraphics.HEADER_BOTTOM;
    private static final int INFO_TOP = AlchemicalJeiGraphics.INFO_TOP;

    private final boolean replication;

    protected AbstractAlchemicalCategory(
            IGuiHelper guiHelper,
            IRecipeType<RecipeHolder<?>> recipeType,
            Component title,
            ItemStack icon,
            boolean replication
    ) {
        super(recipeType, title, guiHelper.createDrawableItemStack(icon), WIDTH, HEIGHT);
        this.replication = replication;
    }

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

        AlchemicalJeiGraphics.drawBase(
                graphics,
                AlchemicalJeiGraphics.PARCHMENT_THEME,
                replication ? Textures.SCROLL_ARR_SG_BASE : Textures.SCROLL_ARR_EQ_BASE
        );
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
            if (recipe.oneTime() && mouseX >= WIDTH / 2.0 - 12 && mouseX < WIDTH / 2.0 + 12) {
                tooltip.add(Component.translatable("jei.transmutatoria.info.one_time.tooltip"));
            } else if (mouseX < WIDTH / 2.0) {
                tooltip.add(getLevelTooltip(recipe.level()));
            } else {
                tooltip.add(Component.translatable(
                        "jei.transmutatoria.info.polarity.tooltip",
                        signed(recipe.minPolarity()),
                        signed(recipe.maxPolarity())
                ));
            }
        } else if (mouseX >= 48 && mouseX < 101 && mouseY >= HEADER_BOTTOM && mouseY < INFO_TOP) {
            tooltip.add(Component.translatable(replication
                    ? "jei.transmutatoria.alchemical_replication.description"
                    : "jei.transmutatoria.alchemical_transformation.description"));
        }
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    private void drawLabels(GuiGraphicsExtractor graphics, CrucibleRecipe recipe) {
        Font font = Minecraft.getInstance().font;
        AlchemicalJeiGraphics.drawSlotLabels(graphics, font, AlchemicalJeiGraphics.PARCHMENT_THEME);

        if (recipe.oneTime()) {
            Component oneTime = Component.literal("1x");
            graphics.text(font, oneTime, (WIDTH - font.width(oneTime)) / 2, 63, AlchemicalJeiGraphics.PARCHMENT_THEME.textColor(), false);
        }

        Component level = Component.translatable("jei.transmutatoria.info.level.short", compactLevel(recipe.level()));
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

    private static String compactLevel(LevelFunction level) {
        if (level instanceof FixedLevel fixed) {
            return romanRange(fixed.min(), fixed.max());
        }
        if (level instanceof EnchantmentLevel) {
            return "*";
        }
        return "?";
    }

    private static Component getLevelTooltip(LevelFunction level) {
        if (level instanceof FixedLevel fixed) {
            return Component.translatable(
                    "jei.transmutatoria.info.level.fixed.tooltip",
                    roman(fixed.min()),
                    roman(fixed.max())
            );
        }
        if (level instanceof EnchantmentLevel enchantment) {
            return Component.translatable(
                    "jei.transmutatoria.info.level.enchantment.tooltip",
                    roman(enchantment.baseMin()),
                    roman(enchantment.baseMax()),
                    enchantment.scaleMin(),
                    enchantment.scaleMax()
            );
        }
        return Component.translatable("jei.transmutatoria.info.level.unknown.tooltip", level.type());
    }

    private static String romanRange(int min, int max) {
        return min == max ? roman(min) : roman(min) + "–" + roman(max);
    }

    private static String roman(int number) {
        if (number < 1 || number > 39) {
            return Integer.toString(number);
        }
        return RomanNumberRenderer.toRomanNumber(number);
    }

    private static String signed(int value) {
        return value > 0 ? "+" + value : Integer.toString(value);
    }
}
