package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.client.gui.BSFGuiTool;
import com.linngdu664.transmutatoria.client.gui.PosUtil;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import com.linngdu664.transmutatoria.client.gui.texture.Textures;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.item.component.ExpireInfo;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

final class DashboardRenderer {
    private static final float MAX_POLARITY = 50.0f;
    private static final float MAX_POINTER_DEGREES = 90.0f;

    private DashboardRenderer() {
    }

    static void render(GuiGraphicsExtractor guiGraphics, Window window, TransmutationCrucibleBlockEntity crucible, ItemStack catalyst, DeltaTracker delta, SmoothValue dashboardPolarity) {
        int screenW = window.getGuiScaledWidth();
        int screenH = window.getGuiScaledHeight();
        float centerX = screenW * 0.975f - Textures.DASHBOARD_BG.width() * 0.5f;
        float centerY = screenH * 0.04f + Textures.DASHBOARD_BG.height() * 0.5f;

        Textures.DASHBOARD_BG.render(
                guiGraphics,
                Math.round(centerX - Textures.DASHBOARD_BG.width() * 0.5f),
                Math.round(centerY - Textures.DASHBOARD_BG.height() * 0.5f)
        );
        drawHourglassLiquid(guiGraphics, centerX, centerY + 2, getScrollExpireRemainingRatio(catalyst));

        RecipeConditions conditions = catalyst.getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
        BSFGuiTool.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, polarityToDegrees(conditions.minPolarity()));
        BSFGuiTool.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER_FLAG, centerX, centerY, polarityToDegrees(conditions.maxPolarity()));

        dashboardPolarity.moveTo(crucible.getPolarity(), delta, 0.05f);
        BSFGuiTool.renderRotatedCentered(guiGraphics, Textures.DASHBOARD_BG_POINTER, centerX, centerY, polarityToDegrees(dashboardPolarity.value()));
    }

    private static void drawHourglassLiquid(GuiGraphicsExtractor guiGraphics, float centerX, float centerY, float remainingRatio) {
        if (remainingRatio < 0.0f) {
            return;
        }

        float clampedRemaining = Mth.clamp(remainingRatio, 0.0f, 1.0f);
        float upX = centerX - Textures.DASHBOARD_HOURGLASS_UP.width() * 0.5f;
        float upY = centerY - Textures.DASHBOARD_HOURGLASS_UP.height();
        float downX = centerX - Textures.DASHBOARD_HOURGLASS_DOWN.width() * 0.5f;
        float downY = centerY;

        BSFGuiTool.renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_UP, upX, upY, Textures.DASHBOARD_HOURGLASS_UP.height() * clampedRemaining);
        BSFGuiTool.renderBottomCropped(guiGraphics, Textures.DASHBOARD_HOURGLASS_DOWN, downX, downY, Textures.DASHBOARD_HOURGLASS_DOWN.height() * (1.0f - clampedRemaining));
    }

    private static float polarityToDegrees(float polarity) {
        return Mth.clamp(polarity / MAX_POLARITY, -1.0f, 1.0f) * MAX_POINTER_DEGREES;
    }

    private static float getScrollExpireRemainingRatio(ItemStack catalyst) {
        ExpireInfo expireInfo = catalyst.get(InitDataComponents.EXPIRE_INFO);
        Minecraft mc = Minecraft.getInstance();
        if (expireInfo == null || expireInfo.period() <= 0 || mc.level == null) {
            return -1.0f;
        }
        long clockTime = mc.level.getOverworldClockTime();
        long nextExpire = catalyst.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);
        return (float) Mth.clamp((double) (nextExpire - clockTime) / (double) expireInfo.period(), 0.0, 1.0);
    }
}
