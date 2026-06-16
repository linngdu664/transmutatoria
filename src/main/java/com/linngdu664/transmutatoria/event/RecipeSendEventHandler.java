package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class RecipeSendEventHandler {
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(InitRecipes.ALCHEMICAL_REPLICATION_PRECISE_TYPE.get());
        event.sendRecipes(InitRecipes.ALCHEMICAL_REPLICATION_TYPE.get());
        event.sendRecipes(InitRecipes.ALCHEMICAL_TRANSFORMATION_PRECISE_TYPE.get());
        event.sendRecipes(InitRecipes.ALCHEMICAL_TRANSFORMATION_TYPE.get());
    }
}
