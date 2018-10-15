package com.aura.aosp.gorilla.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

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
import com.aura.aosp.gorilla.launcher.store.ActionClusterStore;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;
import com.aura.aosp.gorilla.launcher.ui.animation.drawable.ExpandingCircleDrawable;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.common.BaseView;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterAdapter;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ClusterButtonView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ToggleClusterButton;
import com.aura.aosp.gorilla.launcher.ui.status.StatusBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class LauncherActivity extends AppCompatActivity {

    private final static String LOGTAG = LauncherActivity.class.getSimpleName();

    private static final boolean SHOW_STARTUP_ANIMATIONS = false;

    protected static Boolean svlink;
    protected static Boolean uplink;

    public static Identity ownerIdent;

    protected ActionClusterStore actionClusterStore;

    protected BaseView launcherView;
    protected StatusBar statusBar;
    protected ConstraintLayout mainContentContainer;

    protected ConstraintLayout funcContainer;
    protected FuncBaseView mainFuncView;

    protected FrameLayout actionClusterContainer;
    protected ConstraintLayout actionClusterMask;
    protected ToggleClusterButton toggleClusterButton;

    private static ExpandingCircleDrawable mCircle;

    protected Float clusterElevationPerLevel;

    protected List<SmartScrollableLayoutManager> mcSmartScrollableLayoutManagers = new ArrayList<>();

    protected List<ActionClusterView> activeActionClusterViews = new ArrayList<>();

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    protected static int blurSampliong;
    protected static int blurRadius;
    protected static int blurTransisitionDuration;

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

        // Set outer base content view
        setContentView(R.layout.activity_launcher);

        // Get references to main child view components
        launcherView = findViewById(R.id.launcher);
        statusBar = findViewById(R.id.statusBar);
        mainContentContainer = findViewById(R.id.mainContentContainer);
        funcContainer = findViewById(R.id.funcContainer);

        actionClusterContainer = launcherView.findViewById(R.id.actionClusterContainer);
        toggleClusterButton = launcherView.findViewById(R.id.toggleClusterButton);

        actionClusterMask = actionClusterContainer.findViewById(R.id.actionClusterMask);
        actionClusterMask.setVisibility(View.VISIBLE);

        // Hide status and action bars
        hideStatusAndActionBar();

        // Initialize Gorilla application
        Simple.initialize(this.getApplication());

        // Subscribe to Gorilla listener
        GorillaClient.getInstance().subscribeGorillaListener(listener);

        // Get some generic resource values
        // TODO: Move to UI components, pass to Effects!
        blurSampliong = getResources().getInteger(R.integer.launcher_blur_sampling);
        blurRadius = getResources().getInteger(R.integer.launcher_blur_radius);
        blurTransisitionDuration = getResources().getInteger(R.integer.launcher_blur_transition_duration);
        clusterElevationPerLevel = getResources().getDimension(R.dimen.clusterbutton_elevationPerLevel);

//        // Testing drawables:
//        Drawable ringDrawable = new RingDrawable(getApplicationContext());
//        mCircle = new ExpandingCircleDrawable(400, R.color.color_ai_circle);
//        launcherView.setForeground(mCircle);

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
                        createInitialActionClusterView(false);
                        // Remove listener when done
                        toggleClusterButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );
    }

    /**
     * Set the main content/func view which is added (attached) to the "func" container.
     *
     * @param layoutResID
     */
    public void setMainFuncView(int layoutResID) {

        if (mainFuncView != null) {
            removeMainFuncView();
        }

        // Create "func" view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        mainFuncView = (FuncBaseView) inflater.inflate(layoutResID, funcContainer, false);
        mainFuncView.setVisibility(View.INVISIBLE);

        // Add view to container
        funcContainer.addView(mainFuncView);

        mainFuncView.fadeIn(null);
    }

    /**
     * Set the main content/func view which is added (attached) to the "func" container.
     * TODO Fix func view management (add/remove)
     */
    public void removeMainFuncView() {

        if (mainFuncView == null) {
            return;
        }

        mainFuncView.fadeOut(null);

        // Completely remove view
        funcContainer.removeView(mainFuncView);
        mainFuncView = null;
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

    /**
     * Create initial action button cluster (attach it to "root" container)
     */
    public void createInitialActionClusterView(boolean instantShow) {

        actionClusterStore = new ActionClusterStore(getApplicationContext());
        ActionCluster initialActionCluster = actionClusterStore.getClusterForActionEvent(getPackageName());
        createActionClusterView(initialActionCluster, null, instantShow);
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
        Integer nextLayout = R.layout.fragment_actioncluster_horizontal;

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
                    nextLayout = R.layout.fragment_actioncluster_horizontal;
                    break;

                case LinearLayoutManager.HORIZONTAL:
                    nextLayOrientation = LinearLayoutManager.VERTICAL;
                    nextLayout = R.layout.fragment_actioncluster_vertical;
                    break;
            }
        } else {
            nextXPos = 0;
            nextYPos = toggleClusterButton.getY();
            nextElevation += clusterElevationPerLevel;
        }

        // Inflate action cluster layout
        LayoutInflater inflater = LayoutInflater.from(this);
        ActionClusterView actionClusterView = (ActionClusterView) inflater.inflate(nextLayout, actionClusterContainer, false);
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

        actionClusterView.setElevation(nextElevation);

        // Add view to root container
        actionClusterContainer.addView(actionClusterView);

        if (instantShow) {
            activateActionClusterView(actionClusterView);
        }

        Log.d(LOGTAG, String.format("Added action cluster <%s>", actionCluster.getName()));
    }

    /**
     * Show action cluster
     *
     * @param actionClusterView
     */
    public void activateActionClusterView(final ActionClusterView actionClusterView) {

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

            ((ActionClusterView) invokingActionClusterView.getParent()).fadeToBack();
        } else {
            deactivateMainContentView();
            toggleClusterButton.minimize();
//            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp, getTheme()));
        }

        actionClusterView.fadeIn();

        // Put View into list of active Action Cluster Views
        activeActionClusterViews.add(actionClusterView);
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
     * Deactivate (hide/remove) all action cluster views.
     */
    public void deactivateAllActionsClusterViews() {
        for (ActionClusterView actionClusterView : activeActionClusterViews) {
            deactivateActionClusterView(actionClusterView);
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
            ((ActionClusterView) invokingActionButtonView.getParent()).restore();
            actionClusterContainer.removeView(actionClusterView);
            Log.d(LOGTAG, String.format("Removed Action Cluster <%s>", actionClusterView.getId()));
        } else {
//            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp, getTheme()));
            toggleClusterButton.maximize();
            actionClusterView.fadeOut();
            activateMainContentView();
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

        if (actionClusterView == null) {
            createInitialActionClusterView(true);
        } else {
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
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Integer rawX = (int) ev.getRawX();
        Integer rawY = (int) ev.getRawY();

        // If action buttons cluster is active and touch happened outside action cluster view,
        // toggle action button cluster and return to normal content stream view state

        switch (ev.getAction()) {

            case MotionEvent.ACTION_UP:

                if (mainFuncView != null) {
                    // Check for active "function view" and close it if touch happened outside visible rect
                    Rect viewRect = new Rect();
                    View innerView = mainFuncView.findViewById(R.id.funcInnerView);
                    innerView.getGlobalVisibleRect(viewRect);

                    // TODO Fix func view management (add/remove)
                    if (!viewRect.contains(rawX, rawY)) {
                        removeMainFuncView();
                    }
                } else {
                    // Check for action button cluster layers and close the topmost if not sticky
                    if (activeActionClusterViews.size() > 0) {

                        Log.d(LOGTAG, String.format("activeActionClusterViews.size() <%d>", activeActionClusterViews.size()));

                        ActionClusterView actionClusterView = activeActionClusterViews.get(activeActionClusterViews.size() - 1);

                        if (!actionClusterView.isSticky()) {

                            Rect viewRect = new Rect();
                            actionClusterView.getGlobalVisibleRect(viewRect);

                            if (!viewRect.contains(rawX, rawY)) {
                                deactivateActionClusterView(actionClusterView);
                            }
                        }
                    }
                }

                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * Activate background view
     */
    public void activateMainContentView() {

        for (SmartScrollableLayoutManager mcSmartScrollableLayoutManager : mcSmartScrollableLayoutManagers) {
            mcSmartScrollableLayoutManager.setScrollEnabled(true);
        }

        Blurry.delete(mainContentContainer);
    }

    /**
     * Deactivate background view
     */
    public void deactivateMainContentView() {

        for (SmartScrollableLayoutManager mcSmartScrollableLayoutManager : mcSmartScrollableLayoutManagers) {
            mcSmartScrollableLayoutManager.setScrollEnabled(false);
        }

        Blurry.with(this)
                .radius(blurRadius)
                .sampling(blurSampliong)
//                .color(R.color.color_transparent)
                .animate(blurTransisitionDuration)
                .onto(mainContentContainer);
    }

    /**
     * Hide the soft keyboard
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    @Override
    protected void onStart() {
        super.onStart();

        // Check for owner contactIdentity and start "Gorilla SysApp" if not given yet
        // TODO: To be replaced by discovering UID from "Identity Manager" later on.
        if (getOwnerIdent() == null) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp");
            Simple.startActivity(this, launchIntent);
        }
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
        }

        @Override
        public void onUplinkChange(boolean connected) {
            Log.d(LOGTAG, "onUplinkChange: connected=" + connected);

            if (statusBar != null) {
                statusBar.setUplink(connected);
            }
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
            Json.put(received, StreamActivity.getOwnerDeviceBase64(), System.currentTimeMillis());
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
