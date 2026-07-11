package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.event.RenderGuiEventHandler;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ExpandStorageBoxRingDescription extends AbstractDescription {
    @Override
    protected int calcX(int panelWidth) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledWidth() * 0.025f));
    }

    @Override
    protected int calcY(int panelHeight) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledHeight() * 0.04f));
    }

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        boolean holdingStorageBox = player.getMainHandItem().getItem() instanceof AlchemistStorageBoxItem
                || player.getOffhandItem().getItem() instanceof AlchemistStorageBoxItem;
        setState(null, !RenderGuiEventHandler.isHudManuallyHidden && holdingStorageBox
                ? Component.translatable("gui.transmutatoria.crucible_hint.storage_box_ring_expand", "Alt")
                : null, false);
    }
}
