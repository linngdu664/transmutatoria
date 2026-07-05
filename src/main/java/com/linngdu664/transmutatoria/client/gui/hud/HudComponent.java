package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public interface HudComponent {
    void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta);
    void render(GuiGraphicsExtractor guiGraphics);
}
