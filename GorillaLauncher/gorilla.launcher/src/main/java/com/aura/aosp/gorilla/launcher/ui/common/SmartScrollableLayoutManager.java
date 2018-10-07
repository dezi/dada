package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Layout manager which allows disabling/enabling vertical and horizontal
 * scrolling dynamically.
 */
public class SmartScrollableLayoutManager extends LinearLayoutManager {

    private boolean isVerticalScrollEnabled = true;
    private boolean isHorizontalScrollEnabled = true;

    public SmartScrollableLayoutManager(Context context) {
        super(context);
    }

    public SmartScrollableLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SmartScrollableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setScrollEnabled(boolean flag) {
        isVerticalScrollEnabled = flag;
        isHorizontalScrollEnabled = flag;
    }

    public void setVerticalScrollEnabled(boolean flag) {
        isVerticalScrollEnabled = flag;
    }

    public void setHoriozontalScrollEnabled(boolean flag) {
        isHorizontalScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally() {
        return isHorizontalScrollEnabled && super.canScrollHorizontally();
    }

    @Override
    public boolean canScrollVertically() {
        return isVerticalScrollEnabled && super.canScrollVertically();
    }
}
