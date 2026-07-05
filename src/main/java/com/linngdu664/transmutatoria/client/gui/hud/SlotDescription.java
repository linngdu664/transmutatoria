package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.SlotType;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SlotDescription extends AbstractDescription {
    @Override
    protected int calcX(int panelWidth) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledWidth() * 0.975f)) - panelWidth;
    }

    @Override
    protected int calcY(int panelHeight) {
        Window window = Minecraft.getInstance().getWindow();
        return Math.max(12, Math.round(window.getGuiScaledHeight() * 0.96f)) - panelHeight;
    }

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        ItemStack catalyst = crucible.getCatalyst();
        if (crucible.getTargetTimer() == 0 && !crucible.hasAnyOutput()) {
            if (catalyst.isEmpty()) {
                setState(
                        Component.translatable("gui.transmutatoria.crucible_hint.catalyst.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.catalyst.description"),
                        true
                );
                return;
            }
            if (crucible.getInput().isEmpty() && crucible.requiresTransformationInput()) {
                setState(
                        Component.translatable("gui.transmutatoria.crucible_hint.input.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.input.description"),
                        true
                );
                return;
            }
            int requiredWater = crucible.getRequiredWater();
            if (crucible.getWaterAmount() < requiredWater) {
                setState(
                        Component.translatable("gui.transmutatoria.crucible_hint.water.title"),
                        Component.translatable("gui.transmutatoria.crucible_hint.water.description", requiredWater),
                        true
                );
                return;
            }
        }

        List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
        if (alchemySlots.isEmpty()) {
            setState(null, null, false);
            return;
        }

        int selectedSlotIndex = crucible.getSelectedSlot();
        if (selectedSlotIndex < 0 || selectedSlotIndex >= alchemySlots.size()) {
            setState(null, null, false);
            return;
        }

        AbstractAlchemySlot slot = alchemySlots.get(selectedSlotIndex);
        SlotType slotType = slot.getType();
        if (!slot.isShowType() || slotType == SlotType.NORMAL) {
            setState(null, null, false);
            return;
        }

        String slotKey = slotType.getSerializedName();
        setState(
                Component.translatable("gui.transmutatoria.alchemy_slot." + slotKey),
                Component.translatable("gui.transmutatoria.alchemy_slot.description." + slotKey),
                false
        );
    }
}
