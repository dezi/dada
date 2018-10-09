package com.aura.aosp.gorilla.launcher.ui.animation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;

/**
 * Effects collection
 */
public class Effects {

    /**
     * Standard fade in transition for views
     *
     * @param view
     * @param context
     * @param duration
     */
    public static void fadeInView(View view, Context context, @Nullable Integer duration) {

        if (duration == null) {
            duration = context.getResources().getInteger(R.integer.effects_default_view_fadein_transition_duration);
        }

        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .setDuration(duration)
                .start();
    }

    /**
     * Standard fade out transition for views
     *
     * @param view
     */
    public static void fadeOutView(final View view, Context context, @Nullable Integer duration) {

        if (duration == null) {
            duration = context.getResources().getInteger(R.integer.effects_default_view_fadeout_transition_duration);
        }

        view.animate()
                .alpha(0.f)
                .scaleX(0.f).scaleY(0.f)
                .setDuration(duration)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }
}
