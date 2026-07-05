package com.linngdu664.transmutatoria.client.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class InitKeyMappings {
    public static final KeyMapping.Category TRANSMUTATORIA_CATEGORY = new KeyMapping.Category(ArsTransmutatoria.makeMyIdentifier("main"));

    public static final KeyMapping TOGGLE_CRUCIBLE_HUD = new KeyMapping(
            "key.transmutatoria.toggle_crucible_hud",
            GLFW.GLFW_KEY_LEFT_CONTROL,
            TRANSMUTATORIA_CATEGORY
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_CRUCIBLE_HUD);
    }
}
