package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.inventory.TransmutationSigilScrollMenu;
import com.linngdu664.transmutatoria.recipe.AlchemicalRecipeManager;
import com.linngdu664.transmutatoria.recipe.IAlchemicalRecipe;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TransmutationSigilScrollItem extends AbstractTransmutationScrollItem {
    public TransmutationSigilScrollItem(Identifier id) {
        super(id);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) -> new TransmutationSigilScrollMenu(containerId, playerInventory, stack),
                    stack.getHoverName()
            ));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public IAlchemicalRecipe getRecipe(Level level, ItemStack itemStack) {
        return AlchemicalRecipeManager.findMatchRep(level, itemStack);
    }
}
