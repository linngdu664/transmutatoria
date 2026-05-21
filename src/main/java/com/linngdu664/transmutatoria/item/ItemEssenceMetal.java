package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEssenceMetal extends Item {
    private final EssenceMetal essenceMetal;
    private final int state;

    public ItemEssenceMetal(EssenceMetal essenceMetal, int state) {
        this.essenceMetal = essenceMetal;
        this.state = state;
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ArsTransmutatoria.makeMyIdentifier(essenceMetal.getKeyWithPrefix(state)))));
    }

    public EssenceMetal getEssenceMetal() {
        return essenceMetal;
    }

    public int getState() {
        return state;
    }

    public ItemStack change(int stateDelta) {
        return essenceMetal.getItemStack(state + stateDelta);
    }

    public EssenceMetal.Relation getRelation(EssenceMetal essenceMetal) {
        return this.essenceMetal.getRelationTo(essenceMetal);
    }
}
