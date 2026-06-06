package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.inventory.TransmutationEquationScrollMenu;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.recipe.CrucibleRecipeManager;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.util.AlchemySlotGenerator;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TransmutationEquationScrollItem extends AbstractTransmutationScrollItem {
    public TransmutationEquationScrollItem(Identifier id) {
        super(id);
    }

    public TransmutationEquationScrollItem(Identifier id, ExpireInfo expireInfo) {
        super(id, expireInfo);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new TransmutationEquationScrollMenu(containerId, playerInventory, stack),
                    stack.getHoverName()
            ));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void activate(Level level, ItemStack scrollStack, ItemStack inputStack, CrucibleRecipe recipe) {
        scrollStack.set(InitDataComponents.RECIPE_CONDITIONS, new RecipeConditions(recipe.oneTime(), recipe.minPolarity(), recipe.maxPolarity()));
        RandomSource random = level.getRandom();
        // 等级以炼金输出为准
        IntIntImmutablePair minMaxLevel = recipe.level().getMinMax(level, recipe.getOtherSideItemStack());
        int count = random.nextInt(minMaxLevel.leftInt(), minMaxLevel.rightInt() + 1);
        AlchemySlotGenerator.generate(scrollStack, count, random);
        scrollStack.set(InitDataComponents.MAGIC_NUMBER, random.nextInt());
    }

    @Override
    public CrucibleRecipe getRecipe(Level level, ItemStack itemStack) {
        return CrucibleRecipeManager.findMatchTrans(level, itemStack);
    }
}
