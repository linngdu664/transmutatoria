package com.linngdu664.transmutatoria.client.gui.animation;

import com.linngdu664.transmutatoria.client.tool.Easing;
import net.minecraft.client.DeltaTracker;
import net.minecraft.util.Mth;

final class AnimationMath {
    static final float LERP_SPEED = 1.2f;
    static final float EASE_DURATION_TICKS = 6.931472f / LERP_SPEED;

    private AnimationMath() {
    }

    static float easeStep(DeltaTracker delta) {
        float t = Mth.clamp(delta.getGameTimeDeltaTicks(), 0.0f, EASE_DURATION_TICKS);
        return Easing.EXPO_OUT.ease(t, 0.0f, 1.0f, EASE_DURATION_TICKS);
    }

    static float approach(float current, float target, float step, float snapDistance) {
        if (Math.abs(target - current) <= snapDistance) {
            return target;
        }
        float next = Mth.lerp(step, current, target);
        return Math.abs(target - next) <= snapDistance ? target : next;
    }
}
