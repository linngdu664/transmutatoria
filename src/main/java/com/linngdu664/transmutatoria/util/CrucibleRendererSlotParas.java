package com.linngdu664.transmutatoria.util;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class CrucibleRendererSlotParas {
    private final float x;
    private final float y;
    private final float z;
    private final float originR;
    private final float r;
    private final float theta;
    private final float pitch;
    private final float yaw;
    private final float scale;

    public CrucibleRendererSlotParas(float x, float y, float z, float originR, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.originR = originR;
        this.r = (float) Math.sqrt(x * x + z * z);
        this.theta = (float) Math.atan2(z, x);
        this.pitch = pitch;
        this.yaw = yaw;
        this.scale = 0.25f;
    }

    private CrucibleRendererSlotParas(float x, float y, float z, float originR, float r, float theta, float pitch, float yaw, float scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.originR = originR;
        this.theta = theta;
        this.pitch = pitch;
        this.yaw = yaw;
        this.scale = scale;
    }

    public CrucibleRendererSlotParas tick(float omega, float spinOmega, float startupPercent) {
        float newTheta = wrapAngle(theta + omega * startupPercent);
        float breathe = Mth.sin(newTheta * 3.0f) * 0.008f + Mth.cos(newTheta * 5.0f + 1.7f) * 0.006f;
        float newR = Mth.clamp(originR + breathe, Math.max(0, originR - 0.1f), originR + 0.1f);
        float newX = Mth.clamp(newR * Mth.cos(newTheta), -0.3f, 0.3f);
        float newZ = Mth.clamp(newR * Mth.sin(newTheta), -0.3f, 0.3f);
        float newYaw = wrapAngle(yaw + spinOmega * startupPercent);
        return new CrucibleRendererSlotParas(newX, y, newZ, originR, newR, newTheta, pitch, newYaw, scale);
    }

    private static float wrapAngle(float angle) {
        float normalizedAngle = angle % Mth.TWO_PI;
        if (normalizedAngle >= Mth.PI) {
            normalizedAngle -= Mth.TWO_PI;
        }
        if (normalizedAngle < -Mth.PI) {
            normalizedAngle += Mth.TWO_PI;
        }
        return normalizedAngle;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getOriginR() {
        return originR;
    }

    public float getR() {
        return r;
    }

    public float getTheta() {
        return theta;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getScale() {
        return scale;
    }
}
