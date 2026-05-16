package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.gui.MenuTransmutationCrucible;
import com.linngdu664.transmutatoria.gui.MenuAlchemistStorageBox;
import com.linngdu664.transmutatoria.gui.MenuTransmutationSigilScroll;
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

    public static final DeferredHolder<MenuType<?>, MenuType<MenuAlchemistStorageBox>> ALCHEMIST_STORAGE_BOX_MENU =
            MENU_TYPES.register("alchemist_storage_box",
                    () -> new MenuType<>(MenuAlchemistStorageBox::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<MenuTransmutationSigilScroll>> TRANSMUTATION_SIGIL_SCROLL_MENU =
            MENU_TYPES.register("transmutation_sigil_scroll",
                    () -> new MenuType<>(MenuTransmutationSigilScroll::new, FeatureFlags.DEFAULT_FLAGS));
}
