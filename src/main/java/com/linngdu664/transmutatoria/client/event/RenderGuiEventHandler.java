package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import com.linngdu664.transmutatoria.client.gui.hud.*;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix3x2fStack;

import java.util.List;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    private static final List<HudComponent> HUD_COMPONENTS = List.of(new EssenceSlotGraph(), new StorageBoxRing(), new ItemBar(), new Dashboard(), new SlotDescription());
    private static final ToggleHudDescription TOGGLE_HUD_DESCRIPTION = new ToggleHudDescription();
    private static final ExpandStorageBoxRingDescription EXPAND_STORAGE_BOX_RING_DESCRIPTION = new ExpandStorageBoxRingDescription();
    private static final SmoothValue HUD_INTRO = new SmoothValue();
    public static boolean isHudManuallyHidden = true;

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

        HitResult hit = mc.hitResult;
        TransmutationCrucibleBlockEntity crucible = null;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();
            if (player.level().getBlockEntity(blockPos) instanceof TransmutationCrucibleBlockEntity crucible1) {
                crucible = crucible1;
            }
        }

        boolean hudActive = crucible != null && !isHudManuallyHidden;
        DeltaTracker delta = event.getPartialTick();
        HUD_INTRO.moveTo(hudActive ? 1.0f : 0.0f, delta, 0.005f);

        if (crucible != null) {
            GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
            float introValue = HUD_INTRO.value();
            if (introValue > 0.001f) {
                Matrix3x2fStack pose = guiGraphics.pose();
                if (introValue < 0.999f) {
                    Window window = mc.getWindow();
                    int sw = window.getGuiScaledWidth();
                    int sh = window.getGuiScaledHeight();
                    pose.pushMatrix();
                    pose.translate(sw * 0.5f, sh * 0.5f);
                    pose.scale(introValue, introValue);
                    pose.translate(sw * -0.5f, sh * -0.5f);
                }

                for (HudComponent component : HUD_COMPONENTS) {
                    component.prepare(player, crucible, delta);
                    component.render(guiGraphics);
                }

                if (introValue < 0.999f) {
                    pose.popMatrix();
                }
            }

            TOGGLE_HUD_DESCRIPTION.prepare(player, crucible, delta);
            TOGGLE_HUD_DESCRIPTION.render(guiGraphics);

            EXPAND_STORAGE_BOX_RING_DESCRIPTION.prepare(player, crucible, delta);
            EXPAND_STORAGE_BOX_RING_DESCRIPTION.render(guiGraphics);
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        isHudManuallyHidden = true;
    }
}
