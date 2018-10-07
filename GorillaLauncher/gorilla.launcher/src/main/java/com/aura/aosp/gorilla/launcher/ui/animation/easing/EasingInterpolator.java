package com.aura.aosp.gorilla.launcher.ui.animation.easing;

import android.animation.TimeInterpolator;

public class EasingInterpolator implements TimeInterpolator {

    private final Easing easing;

    public EasingInterpolator(Easing easing) {
        this.easing = easing;
    }

    @Override
    public float getInterpolation(float input) {
        return EasingProvider.get(this.easing, input);
    }

    public Easing getEasing() {
        return easing;
    }
}
