package com.linngdu664.transmutatoria.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.recipe.CrucibleRecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID)
public class TagsUpdatedEventHandler {
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        // 数据包注册表更新时清空配方缓存（服务端 /reload + 客户端同步均会触发）
        CrucibleRecipeManager.invalidateCache();
    }
}
