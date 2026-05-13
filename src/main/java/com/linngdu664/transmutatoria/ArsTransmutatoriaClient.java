package com.linngdu664.transmutatoria;

import com.linngdu664.transmutatoria.block.ScreenTransmutationCrucible;
import com.linngdu664.transmutatoria.init.InitMenuTypes;
import com.linngdu664.transmutatoria.item.ScreenAlchemistStorageBox;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ArsTransmutatoria.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class ArsTransmutatoriaClient {
    public ArsTransmutatoriaClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        ArsTransmutatoria.LOGGER.info("HELLO FROM CLIENT SETUP");
        ArsTransmutatoria.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(InitMenuTypes.TRANSMUTATION_CRUCIBLE_MENU.get(), ScreenTransmutationCrucible::new);
        event.register(InitMenuTypes.ALCHEMIST_STORAGE_BOX_MENU.get(), ScreenAlchemistStorageBox::new);
    }
}
