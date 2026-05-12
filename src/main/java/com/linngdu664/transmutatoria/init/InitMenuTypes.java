package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.block.MenuTransmutationCrucible;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<MenuTransmutationCrucible>> TRANSMUTATION_CRUCIBLE_MENU =
            MENU_TYPES.register("transmutation_crucible",
                    () -> new MenuType<>(MenuTransmutationCrucible::new, FeatureFlags.DEFAULT_FLAGS));
}
