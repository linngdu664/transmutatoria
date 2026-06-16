package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.screens.ScreenAlchemistStorageBox;
import com.linngdu664.transmutatoria.client.gui.screens.ScreenEmeraldTablet;
import com.linngdu664.transmutatoria.client.gui.screens.ScreenTransmutationScroll;
import com.linngdu664.transmutatoria.init.InitMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class InitScreens {
    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(InitMenuTypes.ALCHEMIST_STORAGE_BOX_MENU.get(), ScreenAlchemistStorageBox::new);
        event.register(InitMenuTypes.NIGREDO_ALCHEMIST_STORAGE_BOX_MENU.get(), ScreenAlchemistStorageBox::new);
        event.register(InitMenuTypes.ALBEDO_ALCHEMIST_STORAGE_BOX_MENU.get(), ScreenAlchemistStorageBox::new);
        event.register(InitMenuTypes.CITRINITAS_ALCHEMIST_STORAGE_BOX_MENU.get(), ScreenAlchemistStorageBox::new);
        event.register(InitMenuTypes.TRANSMUTATION_SIGIL_SCROLL_MENU.get(), ScreenTransmutationScroll::new);
        event.register(InitMenuTypes.TRANSMUTATION_EQUATION_SCROLL_MENU.get(), ScreenTransmutationScroll::new);
        event.register(InitMenuTypes.EMERALD_TABLET_MENU.get(), ScreenEmeraldTablet::new);
    }
}
