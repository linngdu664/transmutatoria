package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.hud.CrucibleCommonHudRenderer;
import com.linngdu664.transmutatoria.client.gui.hud.CrucibleHudState;
import com.linngdu664.transmutatoria.client.gui.hud.StorageBoxRingRenderer;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import net.minecraft.client.DeltaTracker;
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
    private static final CrucibleHudState state = new CrucibleHudState();

    // 是 jade 不讲武德在先的
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
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
        if (mainHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = mainHand;
        } else if (offHand.getItem() instanceof AlchemistStorageBoxItem) {
            boxStack = offHand;
        }

        HitResult hit = mc.hitResult;
        boolean isLookingAtCrucible = false;
        BlockEntity crucibleBe = null;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();
            Level level = player.level();
            if (level.getBlockState(blockPos).getBlock() == InitBlocks.TRANSMUTATION_CRUCIBLE.get()) {
                isLookingAtCrucible = true;
                crucibleBe = level.getBlockEntity(blockPos);
            }
        }

        DeltaTracker delta = event.getPartialTick();
        state.updateHudAnimation(isLookingAtCrucible, delta);

        if (isLookingAtCrucible) {
            GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
            CrucibleCommonHudRenderer.render(guiGraphics, crucibleBe, delta, state);
            if (boxStack != null) {
                StorageBoxRingRenderer.render(guiGraphics, boxStack, delta, state.storageBoxRotation(), state.storageBoxExpansion());
            }
        }
    }
}
