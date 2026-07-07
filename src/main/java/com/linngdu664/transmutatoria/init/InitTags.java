package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class InitTags {
    public static final TagKey<Item> ALL_ESSENCE_METALS = TagKey.create(
            Registries.ITEM,
            ArsTransmutatoria.makeMyIdentifier("all_essence_metals")
    );
}
