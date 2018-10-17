package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.aura.aosp.gorilla.launcher.LauncherActivity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;

/**
 * Main view which holds and manages the "Action Cluster"
 */
public class ActionClusterView extends RecyclerView implements View.OnTouchListener {

    private final static String LOGTAG = ActionClusterView.class.getSimpleName();

    private float dX, dY;

    protected FuncBaseView invokingFuncView = null;
    protected ClusterButtonView invokingActionButtonView = null;
    protected ConstraintLayout blurLayer = null;

    protected boolean sticky;

    protected static float clusterElevationPerLevel;

    protected static int blurSampling;
    protected static int blurRadius;
    protected static int blurTransisitionDuration;

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

        // Get some generic resource values
        clusterElevationPerLevel = getResources().getDimension(R.dimen.clusterbutton_elevationPerLevel);
        blurSampling = getResources().getInteger(R.integer.launcher_blur_sampling);
        blurRadius = getResources().getInteger(R.integer.launcher_blur_radius);
        blurTransisitionDuration = getResources().getInteger(R.integer.launcher_blur_transition_duration);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ActionClusterView, defStyle, 0);

        a.recycle();
    }

    /**
     * Create action button cluster view with certain parameters based on invoking view by inflating xml.
     *
     * @param invokingActionButtonView
     * @param instantShow
     */
    public void createByInvokingActionButtonView(ClusterButtonView invokingActionButtonView, ViewGroup rootView, @Nullable boolean instantShow) {

        Integer nextLayOrientation = LinearLayoutManager.HORIZONTAL;
        Integer nextLayout = R.layout.fragment_actioncluster_horizontal;

        float nextXPos;
        float nextYPos;
        float nextElevation = clusterElevationPerLevel;

        // Position new cluster on top of invoking button
        nextXPos = invokingActionButtonView.getX();
        nextYPos = invokingActionButtonView.getY();
        nextElevation += invokingActionButtonView.getElevation();

        ViewGroup invokingViewGroup = (ViewGroup) invokingActionButtonView.getParent();

        if (invokingViewGroup instanceof ActionClusterView) {
            ActionClusterView invokingActionClusterView = (ActionClusterView) invokingViewGroup;
            // Add view as a child to given parent action cluster view or to "actionClusterContainer" which serves as a root
            LinearLayoutManager layoutManager = (LinearLayoutManager) invokingActionClusterView.getLayoutManager();

            switch (layoutManager.getOrientation()) {

                case LinearLayoutManager.VERTICAL:
                    nextLayOrientation = LinearLayoutManager.HORIZONTAL;
                    nextLayout = R.layout.fragment_actioncluster_horizontal;
                    break;

                case LinearLayoutManager.HORIZONTAL:
                    nextLayOrientation = LinearLayoutManager.VERTICAL;
                    nextLayout = R.layout.fragment_actioncluster_vertical;
                    break;
            }
        } else {
            nextLayOrientation = LinearLayoutManager.HORIZONTAL;
            nextLayout = R.layout.fragment_actioncluster_horizontal;
        }

        // Inflate action cluster layout
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        final ActionClusterView actionClusterView = (ActionClusterView) inflater.inflate(nextLayout, rootView, false);

        inflate(getContext(), nextLayout, rootView);

        setInvokingActionButtonView(invokingActionButtonView);
        setVisibility(View.INVISIBLE);

        // use a linear layout manager
        SmartScrollableLayoutManager layoutManager = new SmartScrollableLayoutManager(rootView.getContext(), nextLayOrientation, true);
        setLayoutManager(layoutManager);

        setX(nextXPos);
        setY(nextYPos);
        setElevation(nextElevation);

        Log.d(LOGTAG, String.format("actionClusterView nextElevation <%f>", nextElevation));
        Log.d(LOGTAG, String.format("actionClusterView nextXPos <%f>", nextXPos));
        Log.d(LOGTAG, String.format("actionClusterView nextYPos <%f>", nextYPos));

//        // Add events for additional layers
//        if (invokingActionButtonView != null) {
//            // Wait for layout before initializing top level action cluster. Otherwise getting
//            // current coordinates of toggle cluster button will fail!
//            actionClusterView.getViewTreeObserver().addOnGlobalLayoutListener(
//                    new ViewTreeObserver.OnGlobalLayoutListener() {
//                        @Override
//                        public void onGlobalLayout() {
//                            SmartScrollableLayoutManager layoutManager = (SmartScrollableLayoutManager) actionClusterView.getLayoutManager();
//                            Float addX = actionClusterView.getWidth() / 2.0f;
//                            Float addY = actionClusterView.getHeight() / 2.0f;
//
//                            Log.d(LOGTAG, String.format("onGlobalLayout actionClusterWidth <%d>", actionClusterView.getWidth()));
//                            Log.d(LOGTAG, String.format("onGlobalLayout actionClusterHeight <%d>", actionClusterView.getHeight()));
//
//                            switch (layoutManager.getOrientation()) {
//
//                                case LinearLayoutManager.VERTICAL:
//                                    Log.d(LOGTAG, String.format("VERTICAL addY <%f>", addY));
////                                    actionClusterView.setX(actionClusterView.getX() + addX);
//                                    break;
//
//                                case LinearLayoutManager.HORIZONTAL:
//                                    Log.d(LOGTAG, String.format("HORIZONTAL addX <%f>", addX));
////                                    actionClusterView.setY(actionClusterView.getY() + addY);
//                                    break;
//                            }
//
//                            // Remove listener when done
//                            actionClusterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                        }
//                    }
//            );
//        }

        // Add Action Cluster Views to root container
        rootView.addView(this);

        // Activate (position + register) Action Cluster View
        if (instantShow) {
            Effects.fadeInView(this, getContext(), null);
        }
    }

//    /**
//     * Show action cluster: If there is a parent cluster (identified by invokingActionClusterButtonView)
//     *
//     * @param actionClusterView
//     */
//    public void activateActionClusterView(final ActionClusterView actionClusterView) {
//
//        ClusterButtonView invokingActionClusterView = actionClusterView.getInvokingActionButtonView();
//
//        if (invokingActionClusterView != null) {
//            ((ActionClusterView) invokingActionClusterView.getParent()).fadeToBack();
//        } else {
//            deactivateMainContentView();
//            toggleClusterButton.minimize();
////            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp, getTheme()));
//        }
//
//        actionClusterView.fadeIn();
//
//        // Put View into list of active (visible) Action Cluster Views
//        activeActionClusterViews.add(actionClusterView);
//    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
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

//        if (blurLayer != null) {
//            Blurry.delete(blurLayer);
//            removeView(blurLayer);
//            blurLayer = null;
//        }
    }

    /**
     * Deactivate view
     */
    public void fadeToBack() {

        setEnabled(false);
        setAlpha(0.3f);

//        blurLayer = new ConstraintLayout(getContext());
//        blurLayer.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
//        addView(blurLayer);
//
//        Blurry.with(getContext())
//                .radius(blurRadius)
//                .sampling(blurSampling)
////                .color(R.color.color_transparent)
//                .animate(blurTransisitionDuration)
//                .onto(blurLayer);
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

    public View getInvokingFuncView() {
        return invokingFuncView;
    }

    public void setInvokingFuncView(FuncBaseView invokingFuncView) {
        this.invokingFuncView = invokingFuncView;
    }

    public ClusterButtonView getInvokingActionButtonView() {
        return invokingActionButtonView;
    }

    public void setInvokingActionButtonView(ClusterButtonView invokingActionButtonView) {
        this.invokingActionButtonView = invokingActionButtonView;
    }
}
