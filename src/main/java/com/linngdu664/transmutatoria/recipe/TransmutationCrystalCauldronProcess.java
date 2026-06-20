package com.linngdu664.transmutatoria.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

/** Shared definition for the emerald-powered water-cauldron crafting process. */
public final class TransmutationCrystalCauldronProcess {
    public static final int RESULT_COUNT = 3;
    public static final List<Requirement> REQUIREMENTS = List.of(
            new Requirement(Items.GUNPOWDER, 1),
            new Requirement(Items.GLOWSTONE_DUST, 1),
            new Requirement(Items.REDSTONE, 1)
    );

    private TransmutationCrystalCauldronProcess() {
    }

    public record Requirement(Item item, int count) {
    }
}
