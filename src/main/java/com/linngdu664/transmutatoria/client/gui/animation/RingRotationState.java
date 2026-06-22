package com.linngdu664.transmutatoria.client.gui.animation;

import net.minecraft.client.DeltaTracker;

public final class RingRotationState {
    private float smoothRotation;
    private float unboundedTarget;
    private int lastComponentRotation;
    private boolean initialized;

    public float update(int componentRotation, DeltaTracker delta) {
        if (!initialized) {
            smoothRotation = componentRotation;
            unboundedTarget = componentRotation;
            lastComponentRotation = componentRotation;
            initialized = true;
        }

        if (componentRotation != lastComponentRotation) {
            int rotationDelta = componentRotation - lastComponentRotation;
            if (rotationDelta > 6) {
                rotationDelta -= 12;
            } else if (rotationDelta < -6) {
                rotationDelta += 12;
            }
            unboundedTarget += rotationDelta;
            lastComponentRotation = componentRotation;
        }

        smoothRotation = AnimationMath.approach(smoothRotation, unboundedTarget, AnimationMath.easeStep(delta), 0.01f);

        if (Math.abs(smoothRotation) > 12f) {
            float shift = 12f * Math.round(smoothRotation / 12f);
            smoothRotation -= shift;
            unboundedTarget -= shift;
        }

        return smoothRotation;
    }
}
