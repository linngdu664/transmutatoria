package com.linngdu664.transmutatoria.item;

import net.minecraft.world.item.Item;

public class ItemEssenceMetal extends Item {
    private EssenceMetal essenceMetal;
    private int state;
    public ItemEssenceMetal(EssenceMetal essenceMetal, int state) {
        this.essenceMetal = essenceMetal;
        this.state = state;
        super(new Item.Properties());
    }
    public EssenceMetal.Relation getRelation(EssenceMetal essenceMetal) {
        return this.essenceMetal.getRelationTo(essenceMetal);
    }

}
