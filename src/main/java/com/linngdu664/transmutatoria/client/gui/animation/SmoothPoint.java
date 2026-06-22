package com.linngdu664.transmutatoria.client.gui.animation;

import net.minecraft.client.DeltaTracker;

public final class SmoothPoint {
    private float x;
    private float y;
    private boolean initialized;

    public void moveTo(float targetX, float targetY, DeltaTracker delta) {
        if (!initialized) {
            x = targetX;
            y = targetY;
            initialized = true;
            return;
        }

        float step = AnimationMath.easeStep(delta);
        x = AnimationMath.approach(x, targetX, step, 0.01f);
        y = AnimationMath.approach(y, targetY, step, 0.01f);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }
}
