package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.BlockTransmutationCrucible;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.ItemAlchemistStorageBox;
import com.linngdu664.transmutatoria.network.RotateStorageBoxPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

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

        // 确定玩家哪只手持有炼金术士储物盒
        ItemStack boxStack = null;
        int hand = 0;

        if (player.getMainHandItem().getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = player.getMainHandItem();
            hand = 0;
        } else if (player.getOffhandItem().getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = player.getOffhandItem();
            hand = 1;
        }

        if (boxStack == null) {
            return;
        }

        // 必须指向炼金锅
        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit
                && player.level().getBlockState(blockHit.getBlockPos()).getBlock() instanceof BlockTransmutationCrucible)) {
            return;
        }

        // 按住 Shift 时留给内圈旋转，不处理外圈
        if (InputConstants.isKeyDown(mc.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(mc.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
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

        if (mc.getConnection() != null) {
            mc.getConnection().send(new RotateStorageBoxPayload(hand, newRotation).toVanillaServerbound());
        }
    }
}
