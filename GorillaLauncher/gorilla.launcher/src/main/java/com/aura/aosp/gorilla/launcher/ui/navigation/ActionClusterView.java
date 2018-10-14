package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;

import jp.wasabeef.blurry.Blurry;

/**
 * Main view which holds and manages the "Action Cluster"
 */
public class ActionClusterView extends RecyclerView implements View.OnTouchListener {

    private float dX, dY;
//    private Drawable bgDrawable;
    private ClusterButtonView invokingActionButtonView = null;

    public ActionClusterView(Context context) {
        super(context);
    }

    public ActionClusterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ActionClusterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Create default action cluster view including hidden layer of default actions
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ActionClusterView, defStyle, 0);

//        if (a.hasValue(R.styleable.ActionClusterView_bgDrawable)) {
//            bgDrawable = a.getDrawable(R.styleable.ActionClusterView_bgDrawable);
//            bgDrawable.setCallback(this);
//        }

        a.recycle();
    }

    /**
     * Fade in view
     */
    public void fadeIn() {
        Integer duration = getContext().getResources().getInteger(R.integer.actioncluster_fadein_transition_duration);
        Effects.fadeInView(this, getContext(), duration);
    }

    /**
     * Fade out view
     */
    public void fadeOut() {
        Integer duration = getContext().getResources().getInteger(R.integer.actioncluster_fadein_transition_duration);
        Effects.fadeOutView(this, getContext(), duration);
    }

    /**
     * Activate view
     */
    public void restore() {

        setEnabled(true);
        setAlpha(1f);
        Blurry.delete(this);
    }

    /**
     * Deactivate view
     */
    public void fadeToBack() {

        setEnabled(false);
        setAlpha(0.3f);

//        Blurry.with(this)
//                .radius(blurRadius)
//                .sampling(blurSampliong)
////                .color(R.color.color_transparent)
//                .animate(blurTransisitionDuration)
//                .onto(viewGroup);
    }

    /**
     * Allow action cluster to be moved around.
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                v.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                return false;
        }
        return true;
    }

    public ClusterButtonView getInvokingActionButtonView() {
        return invokingActionButtonView;
    }

    public void setInvokingActionButtonView(ClusterButtonView invokingActionButtonView) {
        this.invokingActionButtonView = invokingActionButtonView;
    }
}
