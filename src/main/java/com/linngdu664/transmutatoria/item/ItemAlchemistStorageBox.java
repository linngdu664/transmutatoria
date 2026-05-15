package com.linngdu664.transmutatoria.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemAlchemistStorageBox extends Item {
    private final int state;

    public ItemAlchemistStorageBox(Identifier id, int state) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
        this.state = state;
    }

    public int getBoxState() {
        return state;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) ->
                            new MenuAlchemistStorageBox(containerId, playerInventory, stack, state),
                    stack.getHoverName()));
        }
        return InteractionResult.SUCCESS;
    }
}
