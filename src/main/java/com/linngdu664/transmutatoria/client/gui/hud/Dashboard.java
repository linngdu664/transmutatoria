package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.HudUtil;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Dashboard implements HudComponent {
    private static final float MAX_POLARITY = 50.0f;
    private static final float MAX_POINTER_DEGREES = 90.0f;

    private final SmoothValue dashboardPolarity = new SmoothValue();
    private int topLeftX;
    private int topLeftY;
    private float remainingRatio;
    private float minPolarityDegree;
    private float maxPolarityDegree;

    @Override
    public void prepare(Player player, TransmutationCrucibleBlockEntity crucible, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();
        int screenW = window.getGuiScaledWidth();
        int screenH = window.getGuiScaledHeight();
        topLeftX = Math.max(12, Math.round(screenW * 0.975F)) - Textures.DASHBOARD_BG.width();
        topLeftY = Math.max(12, Math.round(screenH * 0.04F));

        ItemStack catalyst = crucible.getCatalyst();
        RecipeConditions conditions = catalyst.getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
        minPolarityDegree = polarityToDegrees(conditions.minPolarity());
        maxPolarityDegree = polarityToDegrees(conditions.maxPolarity());
        dashboardPolarity.moveTo(polarityToDegrees(crucible.getPolarity()), delta, 0.05F);

        ExpireInfo expireInfo = catalyst.get(InitDataComponents.EXPIRE_INFO);
        if (expireInfo == null || expireInfo.period() <= 0) {
            remainingRatio = -1.0F;
        } else {
            long clockTime = player.level().getOverworldClockTime();
            long nextExpire = catalyst.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);
            remainingRatio = Mth.clamp((float) ((double) (nextExpire - clockTime) / (double) expireInfo.period()), 0.0F, 1.0F);
        }
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics) {
        Textures.DASHBOARD_BG.render(guiGraphics, topLeftX, topLeftY);

        float centerX = topLeftX + Textures.DASHBOARD_BG.width() * 0.5F;
        float centerY = topLeftY + Textures.DASHBOARD_BG.height() * 0.5F;

        if (remainingRatio >= 0.0F) {
            float upX = centerX - Textures.DASHBOARD_HOURGLASS_UP.width() * 0.5F;
            float upY = centerY - Textures.DASHBOARD_HOURGLASS_UP.height();
            float downX = centerX - Textures.DASHBOARD_HOURGLASS_DOWN.width() * 0.5F;
            float downY = centerY;
            HudUtil.renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_UP, upX, upY, Textures.DASHBOARD_HOURGLASS_UP.height() * remainingRatio);
            HudUtil.renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_DOWN, downX, downY, Textures.DASHBOARD_HOURGLASS_DOWN.height() * (1.0F - remainingRatio));
        }

        HudUtil.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, minPolarityDegree);
        HudUtil.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, maxPolarityDegree);
        HudUtil.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER, centerX, centerY, dashboardPolarity.value());
    }

    private static float polarityToDegrees(float polarity) {
        return Mth.clamp(polarity / MAX_POLARITY, -1.0F, 1.0F) * MAX_POINTER_DEGREES;
    }
}
