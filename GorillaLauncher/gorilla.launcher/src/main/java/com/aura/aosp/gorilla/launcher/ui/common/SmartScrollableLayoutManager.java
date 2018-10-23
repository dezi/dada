package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Layout manager which allows disabling/enabling vertical and horizontal
 * scrolling dynamically.
 */
public class SmartScrollableLayoutManager extends LinearLayoutManager {

    private boolean isVerticalScrollEnabled = true;
    private boolean isHorizontalScrollEnabled = true;
    private Context mContext;
    private static final float MILLISECONDS_PER_INCH = 50f;

    public SmartScrollableLayoutManager(Context context) {
        super(context);
        mContext = context;
    }

    public SmartScrollableLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mContext = context;
    }

    public SmartScrollableLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
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
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {

            //This controls the direction in which smoothScroll looks for your view
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return SmartScrollableLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            //This returns the milliseconds it takes to roll one pixel.
            @Override
            protected float calculateSpeedPerPixel (DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
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
