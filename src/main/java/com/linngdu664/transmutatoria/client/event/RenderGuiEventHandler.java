package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.GuiHandler;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.util.SafeInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    // 是 jade 不讲武德在先的
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = SafeInstance.getMC();
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
        if (mainHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = mainHand;
        } else if (offHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = offHand;
        }

        HitResult hit = mc.hitResult;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();
            Level level = player.level();
            if (level.getBlockState(blockPos).getBlock() == InitBlocks.TRANSMUTATION_CRUCIBLE.get()) {
                BlockEntity be = level.getBlockEntity(blockPos);
                if (boxStack != null) {
                    GuiHandler.renderCrucibleStorageBoxHud(guiGraphics, boxStack, event.getPartialTick());
                }
                GuiHandler.renderCrucibleCommonHud(guiGraphics, be, event.getPartialTick());
            }
        }
    }
}
