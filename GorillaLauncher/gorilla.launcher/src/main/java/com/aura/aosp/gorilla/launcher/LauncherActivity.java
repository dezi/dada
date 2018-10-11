package com.aura.aosp.gorilla.launcher;

import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.store.ActionClusterStore;
import com.aura.aosp.gorilla.launcher.store.StreamStore;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;
import com.aura.aosp.gorilla.launcher.ui.animation.drawable.ExpandingCircleDrawable;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.content.ContentComposerView;
import com.aura.aosp.gorilla.launcher.ui.content.StreamAdapter;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;
import com.aura.aosp.gorilla.launcher.ui.content.SimpleCalendarView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ClusterButtonView;

/**
 * Main activity, i.e. the "launcher" screen
 */
public class LauncherActivity extends BaseActivity {

    private final static String LOGTAG = LauncherActivity.class.getSimpleName();

    private static final boolean SHOW_STARTUP_ANIMATIONS = false;

    //    public static List<ChatProfile> chatProfiles = new ArrayList<>();

    private StreamStore contentStreamStore;
    private ActionClusterStore actionClusterStore;

    private static ExpandingCircleDrawable mCircle;

    private RecyclerView contentstreamView;
    private StreamAdapter contentStreamAdapter;

    private ConstraintLayout actionClusterMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: ...");

        // Register generic "launcher opened" event with Gorilla
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GorillaClient.getInstance().registerActionEvent(getPackageName());
            }
        }, 2000);

        // Set main content view
//        setContentView(R.layout.func_stream);

        setMainFuncView(R.layout.func_stream);

        // Get references to main child view components
        launcherView = findViewById(R.id.launcher);
        statusBar = findViewById(R.id.statusBar);

        // Hide status and action bars
        hideStatusAndActionBar();

        contentstreamContainer = launcherView.findViewById(R.id.contentstreamContainer);
        contentstreamView = launcherView.findViewById(R.id.contentstream);

        actionClusterContainer = launcherView.findViewById(R.id.actionClusterContainer);
        toggleClusterButton = launcherView.findViewById(R.id.toggleClusterButton);

        actionClusterMask = actionClusterContainer.findViewById(R.id.actionClusterMask);
        actionClusterMask.setVisibility(View.VISIBLE);

        // Hide status and action bars
        hideStatusAndActionBar();

//        // Testing drawables:
//        Drawable ringDrawable = new RingDrawable(getApplicationContext());
//        mCircle = new ExpandingCircleDrawable(400, R.color.color_ai_circle);
//        launcherView.setForeground(mCircle);

        //
        // Create main content stream items
        //
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        contentstreamView.setHasFixedSize(true);

        // use a linear layout manager
        contentstreamLayoutManager = new SmartScrollableLayoutManager(this);
        contentstreamView.setLayoutManager(contentstreamLayoutManager);

        // TODO: Replace with aggregated stream items from "Gorilla Content Stream Atoms"
        // Create initial content stream items and specify adapter
        contentStreamStore = new StreamStore(getApplicationContext());

//        contentStreamAdapter = new StreamAdapter(SampleData.getDummyStreamData(), this);
        contentStreamAdapter = new StreamAdapter(contentStreamStore.getItemsForAtomContext("aura.uxtream.launcher", getOwnerIdent()), this, this);
        contentstreamView.setAdapter(contentStreamAdapter);

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
                        actionClusterStore = new ActionClusterStore(getApplicationContext());

                        ActionCluster initialActionCluster = actionClusterStore.getClusterForActionEvent(getPackageName());
//                        ActionCluster initialActionCluster = SampleData.getLauncherActionCluster(toggleClusterButton.getContext());

                        createActionClusterView(initialActionCluster, null, false);
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
                    activateActionClusterView(actionClusterView);
                    Effects.doClusterDemoAnimation(actionClusterView);
                }
            }, 800);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                public void run() {
                    ActionClusterView actionClusterView = findViewById(R.id.actionCluster);
                    deactivateActionClusterView(actionClusterView);
                }
            }, 2500);
        }
    }

    /**
     * Hide action cluster
     *
     * @param actionClusterView
     */
    public void deactivateActionClusterView(ActionClusterView actionClusterView) {

        ClusterButtonView invokingActionButtonView = actionClusterView.getInvokingActionButtonView();

        if (invokingActionButtonView != null) {
            actionClusterView.fadeOut();
            activateView((ViewGroup) invokingActionButtonView.getParent());
            actionClusterContainer.removeView((ViewGroup) actionClusterView.getParent());
            Log.d(LOGTAG, String.format("Removed Action Cluster <%s>", ((ViewGroup) actionClusterView.getParent()).getId()));
        } else {
//            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp, getTheme()));
            toggleClusterButton.maximize();
            actionClusterView.fadeOut();
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
                deactivateActionClusterView(actionClusterView);
                break;

            case View.GONE:
            case View.INVISIBLE:
                activateActionClusterView(actionClusterView);
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Integer rawX = (int) ev.getRawX();
        Integer rawY = (int) ev.getRawY();

        // If action buttons cluster is active and touch happened outside action cluster view,
        // toggle action button cluster and return to normal content stream view state

        switch (ev.getAction()) {

            case MotionEvent.ACTION_UP:

                Log.d(LOGTAG, String.format("activeActionClusterViews.size() <%d>", activeActionClusterViews.size()));

                if (activeActionClusterViews.size() > 0) {

                    ActionClusterView actionClusterView = activeActionClusterViews.get(activeActionClusterViews.size() - 1);

                    Rect viewRect = new Rect();
                    actionClusterView.getGlobalVisibleRect(viewRect);

                    if (!viewRect.contains(rawX, rawY)) {
                        deactivateActionClusterView(actionClusterView);
                    }
                }

                // Check for active "function view(s)" and close them if touch happened outside visible rect
                for (final FuncBaseView funcView : funcViewManager.getFuncViews().values()) {
                    Rect viewRect = new Rect();
                    View innerView = funcView.findViewById(R.id.funcInnerView);
                    innerView.getGlobalVisibleRect(viewRect);

                    if (!viewRect.contains(rawX, rawY)) {
                        funcView.fadeOut();
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
     * ACTION: "Open Content Composer"
     */
    public void onOpenContentComposer(@Nullable Identity identity) {

        Log.d(LOGTAG, "onOpenContentComposer user nick is " + identity.getNick());

        // Create calendar view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        ContentComposerView contentComposerView = (ContentComposerView) inflater.inflate(R.layout.func_content_composer, launcherView, false);
        contentComposerView.setVisibility(View.INVISIBLE);
        Effects.fadeInView(contentComposerView, this, null);

        // Add view to launcher
        launcherView.addView(contentComposerView);
        funcViewManager.addFuncView(FuncBaseView.FuncType.OVERLAY, contentComposerView);

        // Just to remember how to create an Intent with putExtra:
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
        SimpleCalendarView simpleCalendarView = (SimpleCalendarView) inflater.inflate(R.layout.func_simple_calendar, launcherView, false);
        simpleCalendarView.setVisibility(View.INVISIBLE);
        simpleCalendarView.fadeIn();

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
}
