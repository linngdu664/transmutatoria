package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ItemTransmutationCrucible extends BlockItem {
    public ItemTransmutationCrucible(Identifier id) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id)));
    }
}
