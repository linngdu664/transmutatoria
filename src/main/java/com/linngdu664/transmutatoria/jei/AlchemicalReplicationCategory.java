package com.linngdu664.transmutatoria.jei;

import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationPreciseRecipe;
import com.linngdu664.transmutatoria.recipe.crucible.AlchemicalReplicationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class AlchemicalReplicationCategory extends AbstractAlchemicalCategory {
    public AlchemicalReplicationCategory(IGuiHelper guiHelper) {
        super(
                guiHelper,
                AlchemicalJeiTypes.ALCHEMICAL_REPLICATION,
                Component.translatable("jei.transmutatoria.alchemical_replication"),
                InitItems.TRANSMUTATION_SIGIL_SCROLL.get().getDefaultInstance()
        );
    }

    @Override
    protected TextureRenderable getMark() {
        return Textures.SCROLL_ARR_SG_BASE;
    }

    @Override
    protected Component getDescriptionTooltip() {
        return Component.translatable("jei.transmutatoria.alchemical_replication.description");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<?> holder, IFocusGroup focuses) {
        addEssenceInputSlot(builder);
        builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, CATALYST_X, SLOT_Y)
                .setStandardSlotBackground()
                .addItemStacks(getCatalysts());

        if (holder.value() instanceof AlchemicalReplicationRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.getOtherSideItemStack());
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                    .setOutputSlotBackground()
                    .add(recipe.outputItems());
        } else if (holder.value() instanceof AlchemicalReplicationPreciseRecipe recipe) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                    .setStandardSlotBackground()
                    .add(recipe.getOtherSideItemStack());
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                    .setOutputSlotBackground()
                    .add(recipe.output().create());
        }
    }

    private static List<ItemStack> getCatalysts() {
        return List.of(
                InitItems.TRANSMUTATION_SIGIL_SCROLL.get().getDefaultInstance(),
                InitItems.TERRESTRIAL_SIGIL_SCROLL.get().getDefaultInstance(),
                InitItems.LUNAR_SIGIL_SCROLL.get().getDefaultInstance(),
                InitItems.SOLAR_SIGIL_SCROLL.get().getDefaultInstance(),
                InitItems.VOID_SIGIL_SCROLL.get().getDefaultInstance()
        );
    }
}
