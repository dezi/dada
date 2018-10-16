package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;

import jp.wasabeef.blurry.Blurry;

/**
 * Base View Class for "Func Views"
 */
public class FuncBaseView extends ConstraintLayout {

    public enum FuncType {
        FULLSCREEN,
        OVERLAY
    }

    public FuncBaseView(Context context) {
        super(context);
    }

    public FuncBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FuncBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Fade in view.
     *
     * @param duration
     */
    public void fadeIn(@Nullable Integer duration) {
        if (duration == null) {
            duration = getContext().getResources().getInteger(R.integer.funcview_fadein_transition_duration);
        }

        Effects.fadeInView(this, getContext(), duration);
    }

    /**
     * Fade out view.
     *
     * @param duration
     */
    public void fadeOut(@Nullable Integer duration) {
        if (duration == null) {
            duration = getContext().getResources().getInteger(R.integer.funcview_fadeout_transition_duration);
        }

        Effects.fadeOutView(this, getContext(), duration);
    }

    /**
     * Activate view
     */
    public void restore() {

        setEnabled(true);
        setAlpha(1f);
        Blurry.delete(this);
    }

    /**
     * Deactivate view
     */
    public void fadeToBack() {

        setEnabled(false);
        setAlpha(0.3f);

//        Blurry.with(this)
//                .radius(blurRadius)
//                .sampling(blurSampling)
////                .color(R.color.color_transparent)
//                .animate(blurTransisitionDuration)
//                .onto(viewGroup);
    }
}
