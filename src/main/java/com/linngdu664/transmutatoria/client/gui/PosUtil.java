package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.world.phys.Vec2;

public class PosUtil {
    public static int heightFrameCenter(Window window, int height) {
        return heightFrameRatio(window, height, 0.5f);
    }

    public static int heightFrameRatio(Window window, int height, float heightRatio) {
        return (int) (((float) window.getHeight() / window.getGuiScale() - height) * heightRatio);
    }

    public static int heightWinRatio(Window window, float heightRatio) {
        return heightFrameRatio(window, 0, heightRatio);
    }

    public static int widthFrameCenter(Window window, int width) {
        return widthFrameRatio(window, width, 0.5f);
    }

    public static int widthFrameRatio(Window window, int width, float widthRatio) {
        return (int) (((float) window.getWidth() / window.getGuiScale() - width) * widthRatio);
    }

    public static int widthWinRatio(Window window, float widthRatio) {
        return widthFrameRatio(window, 0, widthRatio);
    }

    public static V2I v2IRatio(Window window, int width, int height, float widthRatio, float heightRatio) {
        return v2IRatio(window, width, height, widthRatio, heightRatio, 0, 0);
    }

    public static V2I v2IRatio(Window window, int width, int height, float widthRatio, float heightRatio, int xOffset, int yOffset) {
        return new V2I(widthFrameRatio(window, width, widthRatio) + xOffset, heightFrameRatio(window, height, heightRatio) + yOffset);
    }

    public static V2I v2IRatio(Window window, float widthRatio, float heightRatio) {
        return new V2I((int) (window.getWidth() * widthRatio / window.getGuiScale()), (int) (window.getHeight() * heightRatio / window.getGuiScale()));
    }

    public static boolean isInScreen(Vec2 point, Window window) {
        return point.x > 0 && point.y > 0 && point.x < window.getGuiScaledWidth() && point.y < window.getGuiScaledHeight();
    }
}
