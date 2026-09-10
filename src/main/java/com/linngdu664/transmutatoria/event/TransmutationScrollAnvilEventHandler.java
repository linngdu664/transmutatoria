package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public final class TransmutationScrollAnvilEventHandler {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack input = event.getLeft();
        ItemStack output = event.getOutput();
        // 只禁止恢复耐久，保留不修复卷轴的改名等操作。
        if (input.getItem() instanceof AbstractTransmutationScrollItem
                && !output.isEmpty()
                && output.is(input.getItem())
                && output.getDamageValue() < input.getDamageValue()) {
            event.setCanceled(true);
        }
    }
}
