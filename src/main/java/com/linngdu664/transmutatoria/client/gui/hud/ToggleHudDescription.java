package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.init.InitKeyMappings;
import com.linngdu664.transmutatoria.client.event.RenderGuiEventHandler;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ToggleHudDescription extends AbstractDescription {
    @Override
    protected int calcX(int panelWidth) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledWidth() * 0.025f));
    }

    @Override
    protected int calcY(int panelHeight) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledHeight() * 0.96f)) - panelHeight;
    }

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        String keyName = InitKeyMappings.TOGGLE_CRUCIBLE_HUD.getTranslatedKeyMessage().getString();
        setState(null, RenderGuiEventHandler.isHudManuallyHidden
                ? Component.translatable("gui.transmutatoria.crucible_hint.hud_on", keyName)
                : Component.translatable("gui.transmutatoria.crucible_hint.hud_off", keyName), false);
    }
}
