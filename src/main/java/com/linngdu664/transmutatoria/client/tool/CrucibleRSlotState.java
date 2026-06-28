package com.linngdu664.transmutatoria.client.tool;

import com.linngdu664.transmutatoria.client.renderer.state.blockentity.CrucibleRSlotPose;
import net.minecraft.util.Mth;

public class CrucibleRSlotState {
    private final float x;
    private final float y;
    private final float z;
    private final float originR;
    private final float r;
    private final float theta;
    private final float pitch;
    private final float yaw;

    public CrucibleRSlotState(float x, float y, float z, float originR, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.originR = originR;
        this.r = (float) Math.sqrt(x * x + z * z);
        this.theta = (float) Math.atan2(z, x);
        this.pitch = pitch;
        this.yaw = yaw;
    }

    private CrucibleRSlotState(float x, float y, float z, float originR, float r, float theta, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.originR = originR;
        this.r = r;
        this.theta = theta;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public CrucibleRSlotState tick(float omega, float spinOmega, float startupPercent) {
        float newTheta = wrapAngle(theta + omega * startupPercent);
        float breathe = Mth.sin(newTheta * 3.0f) * 0.008f + Mth.cos(newTheta * 5.0f + 1.7f) * 0.006f;
        float newR = Mth.clamp(originR + breathe, Math.max(0, originR - 0.1f), originR + 0.1f);
        float newX = Mth.clamp(newR * Mth.cos(newTheta), -0.3f, 0.3f);
        float newZ = Mth.clamp(newR * Mth.sin(newTheta), -0.3f, 0.3f);
        float newYaw = wrapAngle(yaw + spinOmega * startupPercent);
        return new CrucibleRSlotState(newX, y, newZ, originR, newR, newTheta, pitch, newYaw);
    }

    public CrucibleRSlotPose lerp(CrucibleRSlotState other, float partialTicks, float scale) {
        return new CrucibleRSlotPose(
                Mth.lerp(partialTicks, x, other.x),
                Mth.lerp(partialTicks, y, other.y),
                Mth.lerp(partialTicks, z, other.z),
                Mth.rotLerpRad(partialTicks, pitch, other.pitch),
                Mth.rotLerpRad(partialTicks, yaw, other.yaw),
                scale
        );
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
}
