package com.linngdu664.transmutatoria.init;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class InitCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Fluid.BLOCK,
                InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(),
                (be, side) -> be.getWaterHandler()
        );
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(),
                (be, side) -> {
                    if (side == null) {
                        return null;
                    }
                    return switch (side) {
                        case UP, DOWN -> be.getUpDownItemHandler();
                        default -> be.getSideItemHandler();
                    };
                }
        );
    }
}
