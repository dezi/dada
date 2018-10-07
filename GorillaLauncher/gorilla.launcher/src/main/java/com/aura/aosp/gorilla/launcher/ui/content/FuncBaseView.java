package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

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
}
