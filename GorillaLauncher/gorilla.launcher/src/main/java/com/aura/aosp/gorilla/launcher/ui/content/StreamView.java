package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * "Content Stream" view (child of "Launcher" view)
 */
public class StreamView extends RecyclerView {

    private static final String LOGTAG = StreamView.class.getSimpleName();

    public StreamView(Context context) {
        super(context);
    }

    public StreamView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StreamView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(LOGTAG, String.format("onTouchEvent Action: |%10d|", ev.getAction()));
        Log.d(LOGTAG, String.format("onTouchEvent Axis X: |%10f|", ev.getAxisValue(MotionEvent.AXIS_X)));
        Log.d(LOGTAG, String.format("onTouchEvent Axis Y: |%10f|", ev.getAxisValue(MotionEvent.AXIS_Y)));
        return super.onTouchEvent(ev);
    }

    @Override
    public void dispatchSetActivated(boolean activated) {
        super.dispatchSetActivated(activated);
    }


}
