package com.aura.aosp.gorilla.sysapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.aura.aosp.aura.common.simple.Log;

import java.util.Random;

public class DrawView extends View
{
    Random random = new Random();
    final static Handler handler = new Handler();
    final static Paint paint;
    Path path;
    static int count;
    static int last;
    static int numshapes;

    static
    {
        paint = new Paint();

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, 300, 0xffff0000, 0xff888888, Shader.TileMode.CLAMP));

    }

    public DrawView(Context context)
    {
        super(context);
        setScaleX(0.5f);
        setScaleY(0.5f);
        init();
    }

    public DrawView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        path = new Path();

        path.moveTo(160, 0);
        path.cubicTo(250, 0, 260, 100, 260, 150);
        path.cubicTo(300, 200, 210, 300, 160, 300);
        path.cubicTo(110, 300, 60, 200, 60, 150);
        path.cubicTo(60, 100, 110, 0, 160, 0);

        handler.postDelayed(modifiy, 40);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
        count++;
    }

    int smallRand()
    {
        return random.nextInt(20) - 10;
    }

    private final Runnable modifiy = new Runnable()
    {
        @Override
        public void run()
        {
            path = new Path();

            path.moveTo(160, 0);
            path.cubicTo(250 + smallRand(), 0 + smallRand(), 260 + smallRand(), 100 + smallRand(), 260, 150);
            path.cubicTo(300 + smallRand(), 200 + smallRand(), 210 + smallRand(), 300 + smallRand(), 160, 300);
            path.cubicTo(110 + smallRand(), 300 + smallRand(), 60 + smallRand(), 200 + smallRand(), 60, 150);
            path.cubicTo(60 + smallRand(), 100 + smallRand(), 110 + smallRand(), 0 + smallRand(), 160, 0);

            invalidate();

            handler.postDelayed(modifiy, 0);
        }
    };

    public static final Runnable showcount = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d("FPS: count=" + ((count - last) / DrawView.numshapes));

            handler.postDelayed(showcount, 1000);

            last = count;
        }
    };
}