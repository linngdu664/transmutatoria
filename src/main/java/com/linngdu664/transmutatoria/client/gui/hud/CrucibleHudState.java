package com.linngdu664.transmutatoria.client.gui.hud;

import com.linngdu664.transmutatoria.client.gui.animation.CrucibleSlotAnimation;
import com.linngdu664.transmutatoria.client.gui.animation.RingRotationState;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothPoint;
import com.linngdu664.transmutatoria.client.gui.animation.SmoothValue;
import net.minecraft.client.DeltaTracker;

public final class CrucibleHudState {
    private final RingRotationState storageBoxRotation = new RingRotationState();
    private final SmoothValue storageBoxExpansion = new SmoothValue();
    private final SmoothPoint selectedSlotHighlight = new SmoothPoint();
    private final SmoothValue hudIntro = new SmoothValue();
    private final SmoothValue dashboardPolarity = new SmoothValue();
    private final CrucibleSlotAnimation crucibleSlotAnimation = new CrucibleSlotAnimation();
    private boolean hudManuallyHidden = true;

    public void updateHudAnimation(boolean isVisible, DeltaTracker delta) {
        hudIntro.moveTo(isVisible ? 1.0f : 0.0f, delta, 0.005f);
    }

    public boolean isHudManuallyHidden() {
        return hudManuallyHidden;
    }

    public void setHudManuallyHidden(boolean hudManuallyHidden) {
        this.hudManuallyHidden = hudManuallyHidden;
    }

    public RingRotationState storageBoxRotation() {
        return storageBoxRotation;
    }

    public SmoothValue storageBoxExpansion() {
        return storageBoxExpansion;
    }

    public SmoothPoint selectedSlotHighlight() {
        return selectedSlotHighlight;
    }

    public SmoothValue hudIntro() {
        return hudIntro;
    }

    public SmoothValue dashboardPolarity() {
        return dashboardPolarity;
    }

    public CrucibleSlotAnimation crucibleSlotAnimation() {
        return crucibleSlotAnimation;
    }
}
