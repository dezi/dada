package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.aura.aosp.gorilla.launcher.ui.animation.Effects;

/**
 * Launcher button for toggling main action button cluster
 */
public class ToggleClusterButton extends FloatingActionButton {

    public ToggleClusterButton(Context context) {
        super(context);
    }

    public ToggleClusterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToggleClusterButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void fadeIn() {
        Effects.fadeInView(this, getContext(), null);
    }

    public void fadeOut() {
        Effects.fadeOutView(this, getContext(), null);
    }
}
