package com.linngdu664.transmutatoria.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class AbstractItemTransmutationSigilScroll extends Item {
    public AbstractItemTransmutationSigilScroll(Identifier id) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)).stacksTo(1));
    }
}
