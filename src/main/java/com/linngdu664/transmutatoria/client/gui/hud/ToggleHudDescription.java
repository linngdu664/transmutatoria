package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.event.RenderGuiEventHandler;
import com.linngdu664.transmutatoria.client.init.InitKeyMappings;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AlchemistStorageBoxItem;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
        // what can I say
        String keyName = InitKeyMappings.TOGGLE_CRUCIBLE_HUD.getTranslatedKeyMessage().getString().replace("Control", "Ctrl");
        if (RenderGuiEventHandler.isHudManuallyHidden) {
            ItemStack boxStack = null;
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            if (mainHand.getItem() instanceof AlchemistStorageBoxItem) {
                boxStack = mainHand;
            } else if (offHand.getItem() instanceof AlchemistStorageBoxItem) {
                boxStack = offHand;
            }
            if (boxStack == null) {
                setState(null, Component.translatable("gui.transmutatoria.crucible_hint.hud_on", keyName), false);
            } else {
                int componentRotation = boxStack.getOrDefault(InitDataComponents.ROTATION, 0);
                int selectedSlot = Math.floorMod(componentRotation + 6, 12);
                EssenceMetal selectedEssence = EssenceMetal.values()[selectedSlot];
                String essenceName = Component.translatable("item.transmutatoria." + selectedEssence.getKey()).getString();
                setState(null, Component.translatable("gui.transmutatoria.crucible_hint.hud_on_with_essence", essenceName, keyName), false);
            }
        } else {
            setState(null, Component.translatable("gui.transmutatoria.crucible_hint.hud_off", keyName), false);
        }
    }
}
