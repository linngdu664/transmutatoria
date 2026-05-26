package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.network.to_server.ChangeCrucibleSelectedSlotPayload;
import com.linngdu664.transmutatoria.network.to_server.RotateStorageBoxPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

// 处理手持炼金术士储物盒对准炼金锅时的滚轮事件，旋转外圈刻度
@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class InputEventHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        Player player = mc.player;

        // 必须指向炼金锅
        HitResult hit = mc.hitResult;
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockHitResult blockHit = (BlockHitResult) hit;
        if (!(player.level().getBlockState(blockHit.getBlockPos()).is(InitBlocks.TRANSMUTATION_CRUCIBLE))) {
            return;
        }

        if (player.isShiftKeyDown()) {
            ClientPacketDistributor.sendToServer(new ChangeCrucibleSelectedSlotPayload(blockHit.getBlockPos(), event.getScrollDeltaY() < 0));

            // 取消原版滚轮事件（切换快捷栏）
            event.setCanceled(true);
            return;
        }

        // 确定玩家哪只手持有炼金术士储物盒
        ItemStack boxStack = null;
        int hand = 0;

        if (player.getMainHandItem().getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = player.getOffhandItem();
            hand = 1;
        }

        if (boxStack == null) {
            return;
        }

        // 取消原版滚轮事件（切换快捷栏）
        event.setCanceled(true);

        // 计算新旋转值：滚轮方向 ±1，模12环绕
        int current = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
        int dir = (int) Math.signum(event.getScrollDeltaY());
        int newRotation = Math.floorMod(current + dir, 12);

        // 更新客户端 ItemStack 组件，并同步到服务端
        boxStack.set(InitDataComponents.ROTATION, newRotation);
        ClientPacketDistributor.sendToServer(new RotateStorageBoxPayload(hand, newRotation));
    }
}
