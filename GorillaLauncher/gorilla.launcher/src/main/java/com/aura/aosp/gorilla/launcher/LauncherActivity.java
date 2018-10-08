package com.aura.aosp.gorilla.launcher;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.crypter.Utils;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;
import com.aura.aosp.gorilla.launcher.model.TimelineItem;
import com.aura.aosp.gorilla.launcher.ui.animation.drawable.ExpandingCircleAnimationDrawable;
import com.aura.aosp.gorilla.launcher.ui.animation.easing.Easing;
import com.aura.aosp.gorilla.launcher.ui.animation.easing.EasingInterpolator;
import com.aura.aosp.gorilla.launcher.ui.common.FuncViewManager;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;
import com.aura.aosp.gorilla.launcher.ui.content.LauncherView;
import com.aura.aosp.gorilla.launcher.ui.content.SimpleCalendarView;
import com.aura.aosp.gorilla.launcher.ui.content.TimelineAdapter;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterAdapter;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ClusterButtonView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ToggleClusterButton;
import com.aura.aosp.gorilla.launcher.ui.status.LauncherStatusBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
;
import jp.wasabeef.blurry.Blurry;

/**
 * Main activity, i.e. the "launcher" screen
 */
public class LauncherActivity extends AppCompatActivity {

    private final static String LOGTAG = LauncherActivity.class.getSimpleName();

    private static final float INITIAL_CLUSTER_BUTTON_ALPHA = 0.8f;
    private static final boolean SHOW_STARTUP_ANIMATIONS = false;

    public static Identity ownerIdent;
//    public static List<ChatProfile> chatProfiles = new ArrayList<>();

    private static Boolean svlink;
    private static Boolean uplink;

    private Float clusterElevationPerLevel;

    private static int blurSampliong;
    private static int blurRadius;
    private static int blurTransisitionDuration;
    private static int toggleActionClusterTransitionDuration;
    private static int showFunctionViewTransitionDuration;

    private static ExpandingCircleAnimationDrawable mCircle;

    private LauncherView launcherView;

    private LauncherStatusBar statusBar;
    private ConstraintLayout timelineContainer;
    private RecyclerView timelineView;
    private TimelineAdapter timelineAdapter;
    private SmartScrollableLayoutManager timelineLayoutManager;

    private ToggleClusterButton toggleClusterButton;

    private FrameLayout actionClusterContainer;
    private ConstraintLayout actionClusterMask;

    private List<ActionClusterView> activeActionClusterViews = new ArrayList<>();

    private FuncViewManager funcViewManager = new FuncViewManager();

    @Nullable
    public static Identity getOwnerIdent() {
        return ownerIdent;
    }

    @Nullable
    public static String getOwnerDeviceBase64() {
        if (ownerIdent == null) return null;
        return ownerIdent.getDeviceUUIDBase64();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: ...");

        // Initialize Gorilla application
        Simple.initialize(this.getApplication());

        // Check for owner identity:
        if (ownerIdent == null) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp");
            Simple.startActivity(LauncherActivity.this, launchIntent);
        }

        // Subscribe to Gorilla listener
        GorillaClient.getInstance().subscribeGorillaListener(listener);

        // Register generic "launcher opened" event with Gorilla
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                GorillaClient.getInstance().registerActionEvent(getPackageName());
            }
        }, 2000);

        // Get some resource values
        blurSampliong = getResources().getInteger(R.integer.launcher_blur_sampling);
        blurRadius = getResources().getInteger(R.integer.launcher_blur_radius);
        blurTransisitionDuration = getResources().getInteger(R.integer.launcher_blur_transition_duration);
        toggleActionClusterTransitionDuration = getResources().getInteger(R.integer.toggle_actioncluster_transition_duration);
        showFunctionViewTransitionDuration = getResources().getInteger(R.integer.show_function_view_transition_duration);
        clusterElevationPerLevel = getResources().getDimension(R.dimen.clusterbutton_elevationPerLevel);

        // Identify main layout and components
        setContentView(R.layout.activity_launcher);
        launcherView = findViewById(R.id.launcher);

        statusBar = findViewById(R.id.statusBar);

        timelineContainer = launcherView.findViewById(R.id.timelineContainer);
        timelineView = launcherView.findViewById(R.id.timeline);

        actionClusterContainer = launcherView.findViewById(R.id.actionClusterContainer);
        toggleClusterButton = launcherView.findViewById(R.id.toggleClusterButton);

        actionClusterMask = actionClusterContainer.findViewById(R.id.actionClusterMask);
        actionClusterMask.setVisibility(View.VISIBLE);

        // Hide status and action bars
        hideStatusAndActionBar();

//        // Testing:
//        addHolisticAiCircle();

        //
        // Create main timeline list items
        //
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        timelineView.setHasFixedSize(true);

        // use a linear layout manager
        timelineLayoutManager = new SmartScrollableLayoutManager(this);
        timelineView.setLayoutManager(timelineLayoutManager);

        // specify adapter
        // TODO: Replace with aggregated stream items from "Gorilla Content/Timeline Atoms"
        timelineAdapter = new TimelineAdapter(SampleData.getStreamData(), this);
        timelineAdapter = new TimelineAdapter(new ArrayList<TimelineItem>(), this);
        timelineView.setAdapter(timelineAdapter);

        // Create action cluster toggle button
        // Programmatically define background colors for states because XML definition leads to obscure error with
        // "Invalid drawable added to LayerDrawable! Drawable already belongs to another owner but does not expose a constant state"
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };
        int[] colors = new int[]{
                getColor(R.color.color_togglebutton_enabled),
                getColor(R.color.color_togglebutton_pressed)
        };

        ColorStateList fabColorList = new ColorStateList(states, colors);
        toggleClusterButton.setBackgroundTintList(fabColorList);

        // Wait for layout before initializing top level action cluster. Otherwise getting
        // current coordinates of toggle cluster button will fail!
        toggleClusterButton.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Create initial action button cluster (attach it to "root" container)
                        List<ActionItem> initialActionItems = SampleData.getLauncherActionItems(toggleClusterButton.getContext());
                        createActionClusterView(new ActionCluster("AC-ROOT", initialActionItems), null, false);
                        // Remove listener when done
                        toggleClusterButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );
    }

    /**
     * Do first time startup animations to gain user's focus on available controls
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (SHOW_STARTUP_ANIMATIONS && hasFocus) {

//            doTestAnimation();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    ActionClusterView actionClusterView = findViewById(R.id.actionCluster);
                    activateActionClusterView(actionClusterView, 350);
                    doClusterDemoAnimation(actionClusterView);
                }
            }, 800);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                public void run() {
                    ActionClusterView actionClusterView = findViewById(R.id.actionCluster);
                    deactivateActionClusterView(actionClusterView, 450);
                }
            }, 2500);
        }
    }

    /**
     * Create action button cluster view by inflating xml, adding an item adapter
     * and attach it to launcher (main) view
     *
     * @param actionCluster
     * @param invokingActionButtonView
     * @param instantShow
     */
    public void createActionClusterView(ActionCluster actionCluster, @Nullable ClusterButtonView invokingActionButtonView, boolean instantShow) {

        Integer nextLayOrientation = LinearLayoutManager.HORIZONTAL;
        Integer nextLayout = R.layout.action_cluster_horizontal;

        float nextXPos;
        float nextYPos;

        float nextElevation = clusterElevationPerLevel;

        if (invokingActionButtonView != null) {

            // Position new cluster on top of invoking button
            nextXPos = invokingActionButtonView.getX();
            nextYPos = invokingActionButtonView.getY();

            ActionClusterView invokingActionClusterView = (ActionClusterView) invokingActionButtonView.getParent();

            nextElevation += invokingActionButtonView.getElevation();

            // Add view as a child to given parent action cluster view or to "actionClusterContainer" which serves as a root
            LinearLayoutManager layoutManager = (LinearLayoutManager) invokingActionClusterView.getLayoutManager();

            switch (layoutManager.getOrientation()) {

                case LinearLayoutManager.VERTICAL:
                    nextLayOrientation = LinearLayoutManager.HORIZONTAL;
                    nextLayout = R.layout.action_cluster_horizontal;
                    break;

                case LinearLayoutManager.HORIZONTAL:
                    nextLayOrientation = LinearLayoutManager.VERTICAL;
                    nextLayout = R.layout.action_cluster_vertical;
                    break;
            }
        } else {
            nextXPos = 0;
            nextYPos = toggleClusterButton.getY();
            nextElevation += clusterElevationPerLevel;
        }

        // Inflate action cluster layout
        LayoutInflater inflater = LayoutInflater.from(this);
        FrameLayout actionClusterFrame = (FrameLayout) inflater.inflate(nextLayout, actionClusterContainer, false);
        ActionClusterView actionClusterView = actionClusterFrame.findViewById(R.id.actionCluster);
        actionClusterView.setInvokingActionButtonView(invokingActionButtonView);
        actionClusterView.setVisibility(View.INVISIBLE);

        // use a linear layout manager
        SmartScrollableLayoutManager layoutManager = new SmartScrollableLayoutManager(actionClusterContainer.getContext(), nextLayOrientation, true);
        actionClusterView.setLayoutManager(layoutManager);

        // specify adapter
        ActionClusterAdapter actionClusterAdapter = new ActionClusterAdapter(actionCluster.getItemsByRelevance(), this, this);
        actionClusterView.setAdapter(actionClusterAdapter);

        Log.d(LOGTAG, String.format("nextElevation <%f>", nextElevation));
        Log.d(LOGTAG, String.format("nextXPos <%f>", nextXPos));
        Log.d(LOGTAG, String.format("nextYPos <%f>", nextYPos));

        actionClusterView.setX(nextXPos);
        actionClusterView.setY(nextYPos);

        actionClusterFrame.setElevation(nextElevation);

        // Add view to root container
        actionClusterContainer.addView(actionClusterFrame);

        if (instantShow) {
            activateActionClusterView(actionClusterView, null);
        }

        Log.d(LOGTAG, String.format("Added action cluster <%s>", actionCluster.getName()));
    }

    /**
     * Show action cluster
     *
     * @param actionClusterView
     * @param duration
     */
    public void activateActionClusterView(final ActionClusterView actionClusterView, @Nullable Integer duration) {

        ClusterButtonView invokingActionClusterView = actionClusterView.getInvokingActionButtonView();

        if (invokingActionClusterView != null) {

            // Wait for layout before initializing top level action cluster. Otherwise getting
            // current coordinates of toggle cluster button will fail!
            actionClusterView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            // Create initial action button cluster (attach it to "root" container)
                            List<ActionItem> initialActionItems = SampleData.getLauncherActionItems(actionClusterView.getContext());

                            LinearLayoutManager layoutManager = (LinearLayoutManager) actionClusterView.getLayoutManager();
                            Float addX = actionClusterView.getWidth() / 2.0f;
                            Float addY = actionClusterView.getHeight() / 2.0f;

                            Log.d(LOGTAG, String.format("actionClusterWidth <%d>", actionClusterView.getWidth()));
                            Log.d(LOGTAG, String.format("actionClusterHeight <%d>", actionClusterView.getHeight()));

                            switch (layoutManager.getOrientation()) {

                                case LinearLayoutManager.VERTICAL:
                                    Log.d(LOGTAG, String.format("VERTICAL addY <%f>", addY));
//                                    actionClusterView.setX(actionClusterView.getX() + addX);
                                    break;

                                case LinearLayoutManager.HORIZONTAL:
                                    Log.d(LOGTAG, String.format("HORIZONTAL addX <%f>", addX));
//                                    actionClusterView.setY(actionClusterView.getY() + addY);
                                    break;
                            }

                            // Remove listener when done
                            actionClusterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
            );

            deactivateView((ViewGroup) invokingActionClusterView.getParent());
        } else {
            deactivateBackgroundView();
        }

        fadeInView(actionClusterView, duration);
        toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp, getTheme()));

        // Put View into list of active Action Cluster Views
        activeActionClusterViews.add(actionClusterView);
    }

    /**
     * Hide action cluster
     *
     * @param actionClusterView
     * @param duration
     */
    public void deactivateActionClusterView(ActionClusterView actionClusterView, @Nullable Integer duration) {

        ClusterButtonView invokingActionButtonView = actionClusterView.getInvokingActionButtonView();

        if (invokingActionButtonView != null) {
            fadeOutView(actionClusterView, duration);
            activateView((ViewGroup) invokingActionButtonView.getParent());
            actionClusterContainer.removeView((ViewGroup) actionClusterView.getParent());
            Log.d(LOGTAG, String.format("Removed Action Cluster <%s>", ((ViewGroup) actionClusterView.getParent()).getId()));
        } else {
            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp, getTheme()));
            fadeOutView(actionClusterView, duration);
            activateBackgroundView();
            actionClusterContainer.removeView(actionClusterView);
            Log.d(LOGTAG, String.format("Removed Action Cluster <%s>", actionClusterView.getId()));
        }

        activeActionClusterViews.remove(actionClusterView);
    }

    /**
     * Open action button cluster
     */
    public void toggleActionCluster(View view) {

        ActionClusterView actionClusterView = findViewById(R.id.actionCluster);

        switch (actionClusterView.getVisibility()) {
            case View.VISIBLE:
                deactivateActionClusterView(actionClusterView, null);
                break;

            case View.GONE:
            case View.INVISIBLE:
                activateActionClusterView(actionClusterView, null);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Integer rawX = (int) ev.getRawX();
        Integer rawY = (int) ev.getRawY();

        // If action buttons cluster is active and touch happened outside action cluster view,
        // toggle action button cluster and return to normal timeline view state

        switch (ev.getAction()) {

            case MotionEvent.ACTION_UP:

                Log.d(LOGTAG, String.format("activeActionClusterViews.size() <%d>", activeActionClusterViews.size()));

                if (activeActionClusterViews.size() > 0) {

                    ActionClusterView actionClusterView = activeActionClusterViews.get(activeActionClusterViews.size() - 1);

                    Rect viewRect = new Rect();
                    actionClusterView.getGlobalVisibleRect(viewRect);

                    if (!viewRect.contains(rawX, rawY)) {
                        deactivateActionClusterView(actionClusterView, null);
                    }
                }

                // Check for active "function view(s)" and close them if touch happened outside visible rect
                for (final View funcView : funcViewManager.getFuncViews().values()) {
                    Rect viewRect = new Rect();
                    View innerView = funcView.findViewById(R.id.funcInnerView);
                    innerView.getGlobalVisibleRect(viewRect);

                    if (!viewRect.contains(rawX, rawY)) {
                        fadeOutView(funcView, null);
                        // Completely remove view
//                        launcherView.removeView(funcView);
                        funcViewManager.removeFuncView(funcView);
                    }
                }

                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * ACTION: "Open Message Composer"
     */
    public void onOpenMessageComposer() {

//        Intent intent = new Intent();
//        intent.setPackage(view.getContext().getPackageName());
//        intent.setAction("de.matthiaslienau.c3po.action.MESSAGE_COMPOSE");
//        intent.putExtra("foo", "bar");
//        view.getContext().sendBroadcast(intent);
    }

    /**
     * ACTION: "Open Calendar"
     */
    public void onOpenSimpleCalendar() {

//        ActionClusterView actionClusterView = findViewById(R.id.actionCluster);
//        deactivateActionClusterView(actionClusterView, null);

        // Create calendar view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        SimpleCalendarView simpleCalendarView = (SimpleCalendarView) inflater.inflate(R.layout.action_simple_calendar, launcherView, false);
        simpleCalendarView.setVisibility(View.INVISIBLE);
        fadeInView(simpleCalendarView, null);

        // Add view to launcher
        launcherView.addView(simpleCalendarView);
        funcViewManager.addFuncView(FuncBaseView.FuncType.OVERLAY, simpleCalendarView);
    }

    /**
     * ACTION: "Pick Date"
     */
    public void onPickDate() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Bold"
     */
    public void onMarkSelectedTextBold() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Italic"
     */
    public void onMarkSelectedTextItalic() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Mark Selected Text Underlined"
     */
    public void onMarkSelectedTextUnderlined() {

        onOpenSimpleCalendar();
    }

    /**
     * ACTION: "Align Justify Selected Text"
     */
    public void onMarkSelectedTextAlignJustify() {

        onOpenSimpleCalendar();
    }

    /**
     * Standard fade in animations for "function views"
     *
     * @param view
     */
    private void fadeInView(View view, @Nullable Integer duration) {

        if (duration == null) {
            if (view.getClass() == ActionClusterView.class) {
                duration = showFunctionViewTransitionDuration;
            } else {
                duration = toggleActionClusterTransitionDuration;
            }
        }

        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .setDuration(duration)
                .start();
    }

    /**
     * Standard fade out animations for "function views"
     *
     * @param view
     */
    private void fadeOutView(final View view, @Nullable Integer duration) {

        if (duration == null) {
            if (view.getClass() == ActionClusterView.class) {
                duration = showFunctionViewTransitionDuration;
            } else {
                duration = toggleActionClusterTransitionDuration;
            }
        }

        view.animate()
                .alpha(0.f)
                .scaleX(0.f).scaleY(0.f)
                .setDuration(duration)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Hide action cluster
     */
    public void activateBackgroundView() {

//        timelineView.setLayoutFrozen(true);
        timelineContainer.setEnabled(true);
        timelineLayoutManager.setScrollEnabled(true);
        Blurry.delete(timelineContainer);
    }

    /**
     * Hide action cluster
     */
    public void deactivateBackgroundView() {

        timelineLayoutManager.setScrollEnabled(false);
        timelineContainer.setEnabled(false);
//        timelineView.setLayoutFrozen(true);

        Blurry.with(this)
                .radius(blurRadius)
                .sampling(blurSampliong)
//                .color(R.color.color_transparent)
                .animate(blurTransisitionDuration)
                .onto(timelineContainer);
    }


    /**
     * Hide action cluster
     */
    public void activateView(ViewGroup viewGroup) {

        viewGroup.setEnabled(true);
        viewGroup.setAlpha(1f);
        Blurry.delete(viewGroup);
    }

    /**
     * Hide action cluster
     */
    public void deactivateView(ViewGroup viewGroup) {

        viewGroup.setEnabled(false);
        viewGroup.setAlpha(0.3f);

//        Blurry.with(this)
//                .radius(blurRadius)
//                .sampling(blurSampliong)
////                .color(R.color.color_transparent)
//                .animate(blurTransisitionDuration)
//                .onto(viewGroup);
    }


    /**
     * Bounce animation
     *
     * @param targetView
     */
    private void doClusterDemoAnimation(View targetView) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationX", 450, 150, 0);
        animator.setInterpolator(new EasingInterpolator(Easing.BACK_IN));
        animator.setStartDelay(40);
        animator.setDuration(1500);
        animator.start();
    }

    /**
     * Testing different kind of view animations...
     */
    private void doTestAnimation() {

        // Animation set for action cluster view
        AnimationSet abcAnimationSet = new AnimationSet(true);

        Animation fabAlphaAnimation = new AlphaAnimation(0.2f, INITIAL_CLUSTER_BUTTON_ALPHA);
        fabAlphaAnimation.setDuration(1200);

//        final Interpolator abcInterpolator = PathInterpolatorCompat.create(0.550f, 0.055f, 0.675f, 0.19f);
//        Interpolator bi = new BounceInterpolator();
//        EasingInterpolator ioInterpolator = new EasingInterpolator(Easing.BOUNCE_IN_OUT);

        ScaleAnimation abcScaleAnimationGrow = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f);
        abcScaleAnimationGrow.setInterpolator(new BounceInterpolator());
        abcScaleAnimationGrow.setDuration(600);

        ScaleAnimation abcScaleAnimationShrink = new ScaleAnimation(1.2f, 1f, 1.2f, 1f);
        abcScaleAnimationShrink.setInterpolator(new BounceInterpolator());
        abcScaleAnimationShrink.setDuration(600);

        abcAnimationSet.addAnimation(fabAlphaAnimation);
        abcAnimationSet.addAnimation(abcScaleAnimationGrow);
        abcAnimationSet.addAnimation(abcScaleAnimationShrink);

        ActionClusterView actionClusterView = findViewById(R.id.actionCluster);
        actionClusterView.setAnimation(abcAnimationSet);
        abcAnimationSet.start();
    }

    /**
     * PLAYGROUND: Put fancy-holistic AI bubble animation discretely on top of background
     */
    private void addHolisticAiCircle() {

//        Drawable ringDrawable = this.getRingDrawable();
        mCircle = new ExpandingCircleAnimationDrawable(400, R.color.color_ai_circle);
        launcherView.setForeground(mCircle);

    }

    /**
     * PLAYGROUND: Get drawable from canvas
     *
     * @return
     */
    private Drawable getRingDrawable() {

        return new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                Paint mPaint = new Paint();

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(24);
//                mPaint.setPathEffect(new PathEffect());
                // Setting the color of the circle
                mPaint.setColor(getResources().getColor(R.color.color_ai_expading_circle, getTheme()));
                mPaint.setAntiAlias(true);

                this.setAlpha(75);

                // Draw the circle at (x,y) with radius 250
                int radius = 230;
                int mX = 300;
                int mY = 600;

                canvas.drawCircle(mX, mY, radius, mPaint);

//                mPaint.setColor(Color.YELLOW);
//                mPaint.setDither(true);                    // set the dither to true
//                mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
//                mPaint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
//                mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
//                mPaint.setPathEffect(new CornerPathEffect(50) );   // set the path effect when they join.
//                mPaint.setAntiAlias(true);

//                RectF oval = new RectF(mX - radius, mY - radius, mX + radius, mY + radius);
//                canvas.drawArc(oval, -90, 90, false, mPaint);
//                mPaint.setColor(Color.RED);
//                canvas.drawArc(oval, -90, 89, false, mPaint);

                // Redraw the canvas
//                invalidateSelf();
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
        };
    }

    /**
     * Go fullscreen: Hide the status and action bar
     */
    protected void hideStatusAndActionBar() {

//        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        this.getSupportActionBar().hide();

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        launcherView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide status and action bars
        hideStatusAndActionBar();

//        mCircle.start();
    }

    @Override
    protected void onPause() {
//        mCircle.stop();
        super.onPause();
    }

    /**
     * Gorilla Listener for receiving/sending actions and atoms
     */
    private final GorillaListener listener = new GorillaListener() {
        @Override
        public void onServiceChange(boolean connected) {
            Log.d(LOGTAG, "onServiceChange: connected=" + connected);

            if (statusBar != null) {
                statusBar.setSvLink(connected);
            }

//            updateTitle();
//
//            for (ChatProfile chatProfile : chatProfiles)
//            {
//                chatProfile.activity.setStatus(svlink, uplink);
//                chatProfile.activity.updateTitle();
//            }
        }

        @Override
        public void onUplinkChange(boolean connected) {
            Log.d(LOGTAG, "onUplinkChange: connected=" + connected);

            if (statusBar != null) {
                statusBar.setUplink(connected);
            }

//            updateTitle();
//
//            for (ChatProfile chatProfile : chatProfiles)
//            {
//                chatProfile.activity.setStatus(svlink, uplink);
//                chatProfile.activity.updateTitle();
//            }
        }

        @Override
        public void onOwnerReceived(GorillaOwner owner) {
            Log.d(LOGTAG, "onOwnerReceived: +++++ CURRENT +++++ owner=" + owner.toString());

            String ownerUUID = owner.getOwnerUUIDBase64();

            ownerIdent = Contacts.getContact(ownerUUID);

            if (ownerIdent != null) {

                Log.d(LOGTAG, "onOwnerReceived: +++++ CONTACT +++++ nick=" + ownerIdent.getNick());

                String nick = ownerIdent.getNick();

                if (statusBar != null) {
                    statusBar.setProfileInfo(nick);
                }
            }

//            updateTitle();
//
//            for (ChatProfile chatProfile : chatProfiles)
//            {
//                chatProfile.activity.finish();
//            }
        }

        @Override
        public void onPayloadReceived(GorillaPayload payload) {
            Log.d(LOGTAG, "onPayloadReceived: payload=" + payload.toString());

//            displayMessageInList(payload);

            JSONObject atom = convertMessageToAtomAndPersists(payload);

            String remoteUserUUID = payload.getSenderUUIDBase64();
            String remoteDeviceUUID = payload.getDeviceUUIDBase64();

//            for (ChatProfile chatProfile : chatProfiles)
//            {
//                if (! chatProfile.remoteUserUUID.equals(remoteUserUUID)) continue;
//                if (! chatProfile.remoteDeviceUUID.equals(remoteDeviceUUID)) continue;
//
//                chatProfile.activity.dispatchMessage(atom);
//
//                break;
//            }
        }

        private JSONObject convertMessageToAtomAndPersists(GorillaPayload payload) {
            Long time = payload.getTime();
            String uuid = payload.getUUIDBase64();
            String text = payload.getPayload();
            String remoteUserUUID = payload.getSenderUUIDBase64();

            JSONObject atomLoad = new JSONObject();
            Json.put(atomLoad, "message", text);

            JSONObject received = new JSONObject();
            Json.put(received, LauncherActivity.getOwnerDeviceBase64(), System.currentTimeMillis());
            Json.put(atomLoad, "received", received);

            JSONObject atom = new JSONObject();

            Json.put(atom, "uuid", uuid);
            Json.put(atom, "time", time);
            Json.put(atom, "type", "aura.chat.message");
            Json.put(atom, "load", atomLoad);

            GorillaClient.getInstance().putAtomSharedBy(remoteUserUUID, atom);

            return atom;
        }

        @Override
        public void onPayloadResultReceived(GorillaPayloadResult result) {
            Log.d(LOGTAG, "onPayloadResultReceived: result=" + result.toString());

//            for (ChatProfile chatProfile : chatProfiles)
//            {
//                chatProfile.activity.dispatchResult(result);
//            }
        }
    };
}
