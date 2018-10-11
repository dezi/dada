package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

import com.aura.aosp.gorilla.launcher.R;
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

    public void maximize() {
        Integer duration = getContext().getResources().getInteger(R.integer.toggleclusterbutton_fadein_transition_duration);
        animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(duration)
                .start();
    }

    public void minimize() {
        Integer duration = getContext().getResources().getInteger(R.integer.toggleclusterbutton_fadeout_transition_duration);
        animate()
                .alpha(0.3f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(duration)
                .start();
    }
}
