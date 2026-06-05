package com.linngdu664.transmutatoria.client.renderer.special;

import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

final class AlchemistStorageBoxItemLidAnimation {
    private static final float OPENNESS_PER_MILLISECOND = 1.0F / 500.0F;
    private static final long MAX_FRAME_TIME_MILLIS = 100L;

    private static @Nullable LocalPlayer animationPlayer;
    private static @Nullable InteractionHand activeHand;
    private static float openness;
    private static long lastUpdateTime = -1L;

    private AlchemistStorageBoxItemLidAnimation() {
    }

    static float getOpenness(ItemStack renderedStack) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            reset();
            return 0.0F;
        }
        if (player != animationPlayer) {
            reset();
            animationPlayer = player;
        }

        InteractionHand menuHand = player.containerMenu instanceof AlchemistStorageBoxMenu menu
                ? menu.getOpeningHand()
                : null;
        if (menuHand != null && menuHand != activeHand) {
            activeHand = menuHand;
            openness = 0.0F;
            lastUpdateTime = Util.getMillis();
        }
        if (activeHand == null || player.getItemInHand(activeHand) != renderedStack) {
            return 0.0F;
        }

        long now = Util.getMillis();
        if (lastUpdateTime < 0L) {
            lastUpdateTime = now;
        }
        long elapsed = Math.min(now - lastUpdateTime, MAX_FRAME_TIME_MILLIS);
        lastUpdateTime = now;

        float direction = menuHand == activeHand ? 1.0F : -1.0F;
        openness = Mth.clamp(openness + direction * elapsed * OPENNESS_PER_MILLISECOND, 0.0F, 1.0F);
        if (menuHand == null && openness == 0.0F) {
            activeHand = null;
            lastUpdateTime = -1L;
        }
        return openness;
    }

    private static void reset() {
        animationPlayer = null;
        activeHand = null;
        openness = 0.0F;
        lastUpdateTime = -1L;
    }
}
