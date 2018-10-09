package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;

/**
 * Base View Class for "Func Views"
 */
public abstract class FuncBaseView extends ConstraintLayout {

    public enum FuncType {
        FULLSCREEN,
        OVERLAY
    };

    public FuncBaseView(Context context) {
        super(context);
    }

    public FuncBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FuncBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void fadeIn() {
        Integer duration = getContext().getResources().getInteger(R.integer.funcview_fadein_transition_duration);
        Effects.fadeInView(this, getContext(), duration);
    }

    public void fadeOut() {
        Integer duration = getContext().getResources().getInteger(R.integer.funcview_fadeout_transition_duration);
        Effects.fadeOutView(this, getContext(), duration);
    }
}
