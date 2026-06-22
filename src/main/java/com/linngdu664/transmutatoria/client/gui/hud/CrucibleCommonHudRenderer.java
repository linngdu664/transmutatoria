package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.PosUtil;
import com.linngdu664.transmutatoria.client.gui.texture.TextureRenderable;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.item.TransmutationEquationScrollItem;
import com.linngdu664.transmutatoria.item.TransmutationSigilScrollItem;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.EssenceMetal;
import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class CrucibleCommonHudRenderer {
    private CrucibleCommonHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor guiGraphics, BlockEntity be, DeltaTracker delta, CrucibleHudState state) {
        if (!(be instanceof TransmutationCrucibleBlockEntity crucible)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        state.crucibleSlotAnimation().update(crucible, delta);

        if (state.hudIntro().value() < 0.995f) {
            int sw = window.getGuiScaledWidth();
            int sh = window.getGuiScaledHeight();
            guiGraphics.pose().pushMatrix();
            guiGraphics.pose().translate(sw / 2f, sh / 2f);
            guiGraphics.pose().scale(state.hudIntro().value(), state.hudIntro().value());
            guiGraphics.pose().translate(-sw / 2f, -sh / 2f);
        }

        ItemStack catalyst = crucible.getCatalyst();
        List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());

        V2I stripCenter = PosUtil.v2IRatio(window, 0.05f, 0.5f);
        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            V2I stripPos = new V2I(stripCenter.x() - Textures.DURABILITY_STRIP.wholeWidth() / 2, stripCenter.y() - Textures.DURABILITY_STRIP.wholeHeight() / 2);
            Textures.DURABILITY_STRIP.render(guiGraphics, stripPos.x(), stripPos.y());
            DurabilityProgressRenderer.renderDurabilityStrip(guiGraphics, catalyst, alchemySlots, stripPos.x() + DurabilityProgressRenderer.DURABILITY_STRIP_CONTENT_X, stripPos.y() + DurabilityProgressRenderer.DURABILITY_STRIP_CONTENT_Y);
        }

        V2I barCenter = new V2I(stripCenter.x() + 35, stripCenter.y());
        V2I barPos = new V2I(barCenter.x() - Textures.PROGRESS_BAR.wholeWidth() / 2, barCenter.y() - Textures.PROGRESS_BAR.wholeHeight() / 2);
        Textures.PROGRESS_BAR.render(guiGraphics, barPos.x(), barPos.y());
        DurabilityProgressRenderer.renderProgressBar(guiGraphics, alchemySlots, barPos.x() + DurabilityProgressRenderer.PROGRESS_BAR_CONTENT_X, barPos.y() + DurabilityProgressRenderer.PROGRESS_BAR_CONTENT_Y);

        guiGraphics.item(catalyst, barPos.x() + 13, barPos.y() + 19);
        guiGraphics.item(crucible.getInput(), barPos.x() + 13, barPos.y() + 47);
        guiGraphics.item(crucible.getOutput(), barPos.x() + 13, barPos.y() + 169);

        DashboardRenderer.render(guiGraphics, window, crucible, catalyst, delta, state.dashboardPolarity());

        drawBackground(guiGraphics, window, catalyst);

        long[] xys = null;
        if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            List<EssenceMetal> essences = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().stream().toList();
            List<ItemStack> inputEssences = crucible.getInputEssences();
            xys = EssenceSlotRenderer.calcPosEssenceMetal(window, essences.size());
            EssenceSlotRenderer.drawSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, _ -> Textures.NORMAL_SLOT, slotIdx -> {
                ItemStack inputEssence = inputEssences.get(slotIdx);
                return inputEssence.isEmpty() ? essences.get(slotIdx).getDefaultTexture() : inputEssence;
            }, state.selectedSlotHighlight(), state.crucibleSlotAnimation());
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
            xys = EssenceSlotRenderer.calcPosCrystal(window);
            EssenceSlotRenderer.drawSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, _ -> Textures.NORMAL_SLOT, essencesInCrucible::get, state.selectedSlotHighlight(), state.crucibleSlotAnimation());
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            xys = EssenceSlotRenderer.calcPosPhilosophersStone(window);
            EssenceSlotRenderer.drawDisplayOnlySlots(guiGraphics, xys, crucible.getOutputEssences(), crucible, delta, state.crucibleSlotAnimation());
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            if (alchemySlots.isEmpty()) {
                if (state.hudIntro().value() < 0.995f) {
                    guiGraphics.pose().popMatrix();
                }
                return;
            }
            List<ItemStack> essencesInCrucible = crucible.hasAnyOutput() ? crucible.getOutputEssences() : crucible.getInputEssences();
            xys = EssenceSlotRenderer.calcPosScroll(window, alchemySlots);
            EssenceSlotRenderer.drawSlotsWithItemsAndSelection(guiGraphics, xys, crucible, delta, slotIdx -> alchemySlots.get(slotIdx).getTexture(), slotIdx -> {
                ItemStack essence = essencesInCrucible.get(slotIdx);
                if (!essence.isEmpty()) return essence;
                AbstractAlchemySlot alchemySlot = alchemySlots.get(slotIdx);
                return alchemySlot.isShowEssence() ? alchemySlot.getEssenceMetal().getDefaultTexture() : null;
            }, state.selectedSlotHighlight(), state.crucibleSlotAnimation());
            ArrowRenderer.render(guiGraphics, xys, alchemySlots, catalyst.getOrDefault(InitDataComponents.MAGIC_NUMBER, 0), crucible, delta, state.crucibleSlotAnimation());
        }

        Player player = mc.player;
        if (xys != null && player != null && player.isShiftKeyDown()) {
            EssenceSlotRenderer.drawNumbers(guiGraphics, mc.font, xys, crucible, delta, state.crucibleSlotAnimation());
        }

        SlotDescriptionRenderer.render(guiGraphics, window, mc.font, catalyst, alchemySlots, crucible);

        if (state.hudIntro().value() < 0.995f) {
            guiGraphics.pose().popMatrix();
        }
    }

    private static void drawBackground(GuiGraphicsExtractor guiGraphics, Window window, ItemStack catalyst) {
        TextureRenderable background;
        if (catalyst.is(Items.ENDER_EYE)) {
            background = Textures.ALCHEMY_ARRAY_2;
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            background = Textures.ALCHEMY_ARRAY_1;
        } else if (catalyst.getItem() instanceof EssenceMetalItem) {
            background = Textures.ALCHEMY_ARRAY_8;
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            background = Textures.ALCHEMY_ARRAY_6;
        } else if (catalyst.getItem() instanceof TransmutationEquationScrollItem) {
            background = Textures.ALCHEMY_ARRAY_5;
        } else if (catalyst.getItem() instanceof TransmutationSigilScrollItem) {
            background = Textures.ALCHEMY_ARRAY_7;
        } else {
            return;
        }
        background.renderRatio(guiGraphics, window, 0.5f, 0.5f);
    }
}
