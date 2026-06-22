package com.linngdu664.transmutatoria.client.gui.animation;

import net.minecraft.client.DeltaTracker;

public final class SmoothValue {
    private float value;
    private boolean initialized;

    public void moveTo(float target, DeltaTracker delta, float snapDistance) {
        if (!initialized) {
            value = target;
            initialized = true;
            return;
        }

        value = AnimationMath.approach(value, target, AnimationMath.easeStep(delta), snapDistance);
    }

    public float value() {
        return value;
    }
}
