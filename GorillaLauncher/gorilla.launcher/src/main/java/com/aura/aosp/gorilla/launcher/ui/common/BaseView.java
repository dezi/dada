package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.status.StatusBar;

/**
 * Main surrounding base view providing a full screen layout with status bar.
 */
public class BaseView extends ConstraintLayout {

    protected TextView overlayTextView;
    protected StatusBar statusBar;

    public BaseView(Context context) {
        super(context);
        init();
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Identify and reference child elements
        overlayTextView = findViewById(R.id.overlayText);
        statusBar = findViewById(R.id.statusBar);
    }
}
