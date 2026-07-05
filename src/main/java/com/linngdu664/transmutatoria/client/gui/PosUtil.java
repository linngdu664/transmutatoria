package com.linngdu664.transmutatoria.client.gui;

import com.linngdu664.transmutatoria.util.V2I;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.world.phys.Vec2;

@Deprecated
public class PosUtil {
    public static int heightCenter(Window window, int textureHeight) {
        return heightRatio(window, textureHeight, 0.5f);
    }

    public static int heightRatio(Window window, int textureHeight, float heightRatio) {
        return Math.round(window.getGuiScaledHeight() * heightRatio - textureHeight * 0.5f);
    }

    public static int widthCenter(Window window, int textureWidth) {
        return widthRatio(window, textureWidth, 0.5f);
    }

    public static int widthRatio(Window window, int textureWidth, float widthRatio) {
        return Math.round(window.getGuiScaledWidth() * widthRatio - textureWidth * 0.5f);
    }

    public static V2I v2IRatio(Window window, int textureWidth, int textureHeight, float widthRatio, float heightRatio) {
        return v2IRatio(window, textureWidth, textureHeight, widthRatio, heightRatio, 0, 0);
    }

    public static V2I v2IRatio(Window window, int textureWidth, int textureHeight, float widthRatio, float heightRatio, int xOffset, int yOffset) {
        return new V2I(widthRatio(window, textureWidth, widthRatio) + xOffset, heightRatio(window, textureHeight, heightRatio) + yOffset);
    }

    public static V2I v2IRatio(Window window, float widthRatio, float heightRatio) {
        return new V2I(Math.round(window.getGuiScaledWidth() * widthRatio), Math.round(window.getGuiScaledHeight() * heightRatio));
    }

    public static boolean isInScreen(Vec2 point, Window window) {
        return point.x > 0 && point.y > 0 && point.x < window.getGuiScaledWidth() && point.y < window.getGuiScaledHeight();
    }
}
