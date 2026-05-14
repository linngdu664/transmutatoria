package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.BlockTransmutationCrucible;
import com.linngdu664.transmutatoria.item.ItemAlchemistStorageBox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        Player player = mc.player;
        if (player == null) {
            return;
        }

        ItemStack boxStack = null;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (mainHand.getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = mainHand;
        } else if (offHand.getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = offHand;
        }

        if (boxStack == null) {
            return;
        }

        HitResult hit = mc.hitResult;
        if (hit instanceof BlockHitResult blockHit) {
            if (player.level().getBlockState(blockHit.getBlockPos()).getBlock() instanceof BlockTransmutationCrucible) {
                GuiHandler.renderStorageBoxHud(event.getGuiGraphics(), boxStack);
            }
        }
    }
}
