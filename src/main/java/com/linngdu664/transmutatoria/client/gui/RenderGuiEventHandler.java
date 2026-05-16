package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.item.ItemAlchemistStorageBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
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
        GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();

        ItemStack boxStack = null;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (mainHand.getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = mainHand;
        } else if (offHand.getItem() instanceof ItemAlchemistStorageBox) {
            boxStack = offHand;
        }

        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();

            if (player.level().getBlockState(blockPos).getBlock() == InitBlocks.TRANSMUTATION_CRUCIBLE.get()) {
                BlockEntity be = player.level().getBlockEntity(blockPos);
                if (boxStack != null) {
                    GuiHandler.renderCrucibleStorageBoxHud(guiGraphics, boxStack, event.getPartialTick());
                }
                GuiHandler.renderCrucibleCommonHud(guiGraphics, be);
            }
        }
    }
}
