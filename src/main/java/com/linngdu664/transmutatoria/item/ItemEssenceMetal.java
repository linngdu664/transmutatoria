package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ItemEssenceMetal extends Item {
    private EssenceMetal essenceMetal;
    private int state;
    public ItemEssenceMetal(EssenceMetal essenceMetal, int state) {
        this.essenceMetal = essenceMetal;
        this.state = state;
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ArsTransmutatoria.makeMyIdentifier(essenceMetal.getKeyWithPrefix(state)))));
    }
    public EssenceMetal.Relation getRelation(EssenceMetal essenceMetal) {
        return this.essenceMetal.getRelationTo(essenceMetal);
    }

}
