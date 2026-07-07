package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.HudUtil;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.util.alchemy_slots.AbstractAlchemySlot;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemBar implements HudComponent {
    private int leftCenterX;
    private int centerY;
    private float durabilityHeight;
    private float damageTop;
    private float damageHeight;
    private float progressVisibleHeight;
    private ItemStack catalyst;
    private ItemStack input;
    private ItemStack output;

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();

        leftCenterX = Math.max(12, Math.round(window.getGuiScaledWidth() * 0.025F));
        centerY = Math.round(window.getGuiScaledHeight() * 0.5F);

        catalyst = crucible.getCatalyst();
        input = crucible.getInput();
        output = crucible.getOutput();

        float durabilityHeight1 = 0;
        float damageTop1 = 0;
        float damageHeight1 = 0;
        int maxDurability = catalyst.getMaxDamage();
        if (maxDurability > 0) {
            int damage = Mth.clamp(catalyst.getDamageValue(), 0, maxDurability);
            int durability = maxDurability - damage;
            durabilityHeight1 = Textures.DURABILITY_STRIP_DURABILITY.height() * ((float) durability / maxDurability);
            int predictedDamage = AbstractTransmutationScrollItem.getPredictedScrollDamage(catalyst);
            if (predictedDamage > 0 && durability > 0) {
                damageTop1 = Textures.DURABILITY_STRIP_DAMAGE.height() * ((float) damage / maxDurability);
                damageHeight1 = Textures.DURABILITY_STRIP_DAMAGE.height() * ((float) Math.min(predictedDamage, durability) / maxDurability);
            }
        }
        durabilityHeight = durabilityHeight1;
        damageTop = damageTop1;
        damageHeight = damageHeight1;

        List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
        if (alchemySlots.isEmpty()) {
            progressVisibleHeight = 0;
        } else {
            int unlockedSlots = 0;
            for (AbstractAlchemySlot slot : alchemySlots) {
                if (slot.isShowEssence()) {
                    unlockedSlots++;
                }
            }
            progressVisibleHeight = Textures.PROGRESS_BAR_CONTENT.height() * ((float) unlockedSlots / alchemySlots.size());
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics) {
        int stripY = centerY - Textures.DURABILITY_STRIP.wholeHeight() / 2;
        Textures.DURABILITY_STRIP.render(guiGraphics, leftCenterX, stripY);

        int stripContentX = leftCenterX + 6;
        int stripContentY = stripY + 25;
        HudUtil.renderBottomCropped(guiGraphics, Textures.DURABILITY_STRIP_DURABILITY, stripContentX, stripContentY, durabilityHeight);
        HudUtil.renderVerticalSlice(guiGraphics, Textures.DURABILITY_STRIP_DAMAGE, stripContentX, stripContentY, damageTop, damageHeight);

        int barX = leftCenterX + 25;
        int barY = centerY - Textures.PROGRESS_BAR.wholeHeight() / 2;
        Textures.PROGRESS_BAR.render(guiGraphics, barX, barY);
        HudUtil.renderTopCropped(guiGraphics, Textures.PROGRESS_BAR_CONTENT, barX + 16, barY + 67, progressVisibleHeight);

        int itemX = barX + 13;
        guiGraphics.item(catalyst, itemX, barY + 19);
        guiGraphics.item(input, itemX, barY + 47);
        guiGraphics.item(output, itemX, barY + 169);
    }
}
