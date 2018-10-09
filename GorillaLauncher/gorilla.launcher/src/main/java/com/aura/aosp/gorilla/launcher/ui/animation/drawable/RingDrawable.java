package com.aura.aosp.gorilla.launcher.ui.animation.drawable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Test drawable
 */
public class RingDrawable extends Drawable {

    private Context context;

    public RingDrawable(Context context) {
        this.context = context;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Paint mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(24);
//                mPaint.setPathEffect(new PathEffect());
        // Setting the color of the circle
        mPaint.setColor(context.getResources().getColor(R.color.color_ai_expading_circle, context.getTheme()));
        mPaint.setAntiAlias(true);

        this.setAlpha(75);

        // Draw the circle at (x,y) with radius 250
        int radius = 230;
        int mX = 300;
        int mY = 600;

        canvas.drawCircle(mX, mY, radius, mPaint);

//        mPaint.setColor(Color.YELLOW);
//        mPaint.setDither(true);                    // set the dither to true
//        mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
//        mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
//        mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
//        mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
//        mPaint.setAntiAlias(true);
//
//        RectF oval = new RectF(mX - radius, mY - radius, mX + radius, mY + radius);
//        canvas.drawArc(oval, -90, 90, false, mPaint);
//        mPaint.setColor(Color.RED);
//        canvas.drawArc(oval, -90, 89, false, mPaint);
//
//        //Redraw the canvas
//        invalidateSelf();
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
