package com.linngdu664.transmutatoria;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ArsTransmutatoria.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class ArsTransmutatoriaClient {
    public ArsTransmutatoriaClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
