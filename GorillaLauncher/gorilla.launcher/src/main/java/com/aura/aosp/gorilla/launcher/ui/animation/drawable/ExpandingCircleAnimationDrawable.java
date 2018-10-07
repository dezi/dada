package com.aura.aosp.gorilla.launcher.ui.animation.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.AnimationUtils;

public class ExpandingCircleAnimationDrawable extends Drawable implements Animatable, Runnable {

    private Paint mPaint;
    private float mRadius;

    private long mStartTicks = 0;
    private boolean mIsRunning = false;

    public ExpandingCircleAnimationDrawable(float radius, int color) {
        super();

        mRadius = radius;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
//        mPaint.setColor(0xfff3f3f3);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        float loopPercent = calculateCurrentLoopPercent();

        float alpha = -(loopPercent * loopPercent) + 1;

        mPaint.setAlpha((int) (255 * alpha));

        float radius = loopPercent * mRadius;

        Rect bounds = getBounds();
        float x = ((bounds.right - bounds.left) / 2f) + bounds.left;
        float y = ((bounds.bottom - bounds.top) / 2f) + bounds.top;

        canvas.drawCircle(x, y, radius, mPaint);
    }

    private float calculateCurrentLoopPercent() {
        float loopPercent = 0.5f;

        if (isRunning()) {
            float loopMillis = 7000;

            loopPercent = (AnimationUtils.currentAnimationTimeMillis() - mStartTicks) / loopMillis;

            while (loopPercent > 1) {
                loopPercent -= 1;
                mStartTicks += loopMillis;
            }
        }

        return loopPercent;
    }

    @Override
    public void run() {
        invalidateSelf();
        scheduleSelf(this, AnimationUtils.currentAnimationTimeMillis() + (1000/60));
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            mIsRunning = true;
            mStartTicks = AnimationUtils.currentAnimationTimeMillis();
            run();
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(this);
            mIsRunning = false;
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColorFilter(ColorFilter arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (2 * mRadius);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (2 * mRadius);
    }

    @Override
    public int getMinimumHeight() {
        return (int) (2 * mRadius);
    }

    @Override
    public int getMinimumWidth() {
        return (int) (2 * mRadius);
    }
}
