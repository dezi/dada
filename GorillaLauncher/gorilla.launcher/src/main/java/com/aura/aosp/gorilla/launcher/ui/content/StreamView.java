package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;

/**
 * "Content Stream" view (child of "Launcher" view)
 */
public class StreamView extends FuncBaseView {

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

    public void fadeIn() {
        Integer duration = getContext().getResources().getInteger(R.integer.streamview_fadein_transition_duration);
        super.fadeIn(duration);
    }

    public void fadeOut() {
        Integer duration = getContext().getResources().getInteger(R.integer.streamview_fadeout_transition_duration);
        super.fadeOut(duration);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("onTouchEvent Action: |%10d|", ev.getAction());
        Log.d("onTouchEvent Axis X: |%10f|", ev.getAxisValue(MotionEvent.AXIS_X));
        Log.d("onTouchEvent Axis Y: |%10f|", ev.getAxisValue(MotionEvent.AXIS_Y));
        return super.onTouchEvent(ev);
    }
}
