package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.client.gui.BSFGuiTool;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

final class DurabilityProgressRenderer {
    static final int DURABILITY_STRIP_CONTENT_X = 6;
    static final int DURABILITY_STRIP_CONTENT_Y = 25;
    static final int PROGRESS_BAR_CONTENT_X = 16;
    static final int PROGRESS_BAR_CONTENT_Y = 67;

    private DurabilityProgressRenderer() {
    }

    static void renderDurabilityStrip(GuiGraphicsExtractor guiGraphics, ItemStack catalyst, List<AbstractAlchemySlot> alchemySlots, int stripX, int stripY) {
        int maxDurability = catalyst.getMaxDamage();
        if (maxDurability <= 0) {
            return;
        }

        int damage = Mth.clamp(catalyst.getDamageValue(), 0, maxDurability);
        int durability = maxDurability - damage;
        float durabilityHeight = Textures.DURABILITY_STRIP_DURABILITY.height() * durability / (float) maxDurability;
        BSFGuiTool.renderBottomCropped(guiGraphics, Textures.DURABILITY_STRIP_DURABILITY, stripX, stripY, durabilityHeight);

        int predictedDamage = getPredictedScrollDamage(catalyst, alchemySlots);
        if (predictedDamage <= 0 || durability <= 0) {
            return;
        }

        float damageTop = Textures.DURABILITY_STRIP_DAMAGE.height() * damage / (float) maxDurability;
        float damageHeight = Textures.DURABILITY_STRIP_DAMAGE.height() * Math.min(predictedDamage, durability) / (float) maxDurability;
        BSFGuiTool.renderVerticalSlice(guiGraphics, Textures.DURABILITY_STRIP_DAMAGE, stripX, stripY, damageTop, damageHeight);
    }

    static void renderProgressBar(GuiGraphicsExtractor guiGraphics, List<AbstractAlchemySlot> alchemySlots, int barX, int barY) {
        if (alchemySlots.isEmpty()) {
            return;
        }

        int unlockedSlots = 0;
        for (AbstractAlchemySlot slot : alchemySlots) {
            if (slot.isShowEssence()) {
                unlockedSlots++;
            }
        }

        float visibleHeight = Textures.PROGRESS_BAR_CONTENT.height() * unlockedSlots / (float) alchemySlots.size();
        BSFGuiTool.renderTopCropped(guiGraphics, Textures.PROGRESS_BAR_CONTENT, barX, barY, visibleHeight);
    }

    private static int getPredictedScrollDamage(ItemStack catalyst, List<AbstractAlchemySlot> alchemySlots) {
        int entropy = catalyst.getOrDefault(InitDataComponents.ENTROPY, 0);
        int damagePerSlot = Math.max(1, 1 + entropy / 4);
        return alchemySlots.size() * damagePerSlot;
    }
}
