package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import com.linngdu664.transmutatoria.inventory.TransmutationEquationScrollMenu;
import com.linngdu664.transmutatoria.inventory.TransmutationSigilScrollMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.linngdu664.transmutatoria.ArsTransmutatoria.MODID;

public class InitMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<AlchemistStorageBoxMenu>> ALCHEMIST_STORAGE_BOX_MENU =
            MENU_TYPES.register("alchemist_storage_box",
                    () -> new MenuType<>(AlchemistStorageBoxMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<AlchemistStorageBoxMenu>> NIGREDO_ALCHEMIST_STORAGE_BOX_MENU =
            MENU_TYPES.register("nigredo_alchemist_storage_box",
                    () -> new MenuType<>((containerId, inv) -> new AlchemistStorageBoxMenu(containerId, inv, -1), FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<AlchemistStorageBoxMenu>> ALBEDO_ALCHEMIST_STORAGE_BOX_MENU =
            MENU_TYPES.register("albedo_alchemist_storage_box",
                    () -> new MenuType<>((containerId, inv) -> new AlchemistStorageBoxMenu(containerId, inv, 1), FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<AlchemistStorageBoxMenu>> CITRINITAS_ALCHEMIST_STORAGE_BOX_MENU =
            MENU_TYPES.register("citrinitas_alchemist_storage_box",
                    () -> new MenuType<>((containerId, inv) -> new AlchemistStorageBoxMenu(containerId, inv, 2), FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<TransmutationSigilScrollMenu>> TRANSMUTATION_SIGIL_SCROLL_MENU =
            MENU_TYPES.register("transmutation_sigil_scroll",
                    () -> new MenuType<>(TransmutationSigilScrollMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<TransmutationEquationScrollMenu>> TRANSMUTATION_EQUATION_SCROLL_MENU =
            MENU_TYPES.register("transmutation_equation_scroll",
                    () -> new MenuType<>(TransmutationEquationScrollMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
