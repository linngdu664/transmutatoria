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

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class InputEventHandler {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        Player player = mc.player;

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

        HitResult hit = mc.hitResult;
        if (!(hit instanceof BlockHitResult blockHit
                && player.level().getBlockState(blockHit.getBlockPos()).getBlock() instanceof BlockTransmutationCrucible)) {
            return;
        }

        if (InputConstants.isKeyDown(mc.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputConstants.isKeyDown(mc.getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            return;
        }

        event.setCanceled(true);

        int current = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
        int dir = (int) Math.signum(event.getScrollDeltaY());
        int newRotation = Math.floorMod(current + dir, 12);

        boxStack.set(InitDataComponents.ROTATION, newRotation);

        if (mc.getConnection() != null) {
            mc.getConnection().send(new RotateStorageBoxPayload(hand, newRotation).toVanillaServerbound());
        }
    }
}
