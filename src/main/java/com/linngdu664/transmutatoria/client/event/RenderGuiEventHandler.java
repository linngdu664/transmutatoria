package com.linngdu664.transmutatoria.client.event;

import com.linngdu664.transmutatoria.ArsTransmutatoria;
import com.linngdu664.transmutatoria.client.gui.hud.CrucibleCommonHudRenderer;
import com.linngdu664.transmutatoria.client.gui.hud.CrucibleHudState;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = ArsTransmutatoria.MODID, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    static final CrucibleHudState state = new CrucibleHudState();

    private static final int HINT_PADDING = 6;
    private static final int HINT_BG_COLOR = 0xb0181116;
    private static final int HINT_BORDER_COLOR = 0xc0d6b47b;
    private static final int HINT_TEXT_COLOR = 0xffe2ddd0;

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
        boolean hudActive = isLookingAtCrucible && !state.isHudManuallyHidden();
        state.updateHudAnimation(hudActive, delta);

        if (isLookingAtCrucible) {
            GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
            if (hudActive || state.hudIntro().value() > 0.001f) {
                CrucibleCommonHudRenderer.render(guiGraphics, crucibleBe, boxStack, delta, state);
            }
            renderToggleHint(guiGraphics, mc.font, mc.getWindow());
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        state.setHudManuallyHidden(true);
    }

    private static void renderToggleHint(GuiGraphicsExtractor guiGraphics, Font font, Window window) {
        String keyName = ModKeyMappings.TOGGLE_CRUCIBLE_HUD.getTranslatedKeyMessage().getString();
        Component hint = state.isHudManuallyHidden()
                ? Component.translatable("gui.transmutatoria.crucible_hint.hud_on", keyName)
                : Component.translatable("gui.transmutatoria.crucible_hint.hud_off", keyName);

        int textWidth = font.width(hint);
        int panelWidth = textWidth + HINT_PADDING * 2;
        int panelHeight = font.lineHeight + HINT_PADDING * 2;
        int x = Math.round(window.getGuiScaledWidth() * 0.025f);
        int y = Math.round(window.getGuiScaledHeight() * 0.96f) - panelHeight;

        guiGraphics.fill(x, y, x + panelWidth, y + panelHeight, HINT_BG_COLOR);
        guiGraphics.outline(x, y, panelWidth, panelHeight, HINT_BORDER_COLOR);
        guiGraphics.text(font, hint, x + HINT_PADDING, y + HINT_PADDING, HINT_TEXT_COLOR, true);
    }
}
