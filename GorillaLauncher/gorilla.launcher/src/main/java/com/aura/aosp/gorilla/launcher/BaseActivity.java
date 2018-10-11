package com.aura.aosp.gorilla.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import com.aura.aosp.gorilla.launcher.ui.common.FuncViewManager;
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

public class BaseActivity extends AppCompatActivity {

    private final static String LOGTAG = BaseActivity.class.getSimpleName();

    protected static Boolean svlink;
    protected static Boolean uplink;

    public static Identity ownerIdent;

    protected BaseView launcherView;
    protected StatusBar statusBar;
    protected ConstraintLayout funcContainer;
    protected FrameLayout actionClusterContainer;
    protected ToggleClusterButton toggleClusterButton;
    protected ConstraintLayout contentstreamContainer;

    protected Float clusterElevationPerLevel;
    protected SmartScrollableLayoutManager contentstreamLayoutManager;
    protected List<ActionClusterView> activeActionClusterViews = new ArrayList<>();
    protected FuncViewManager funcViewManager = new FuncViewManager();

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

        // Check for owner identity and start "Gorilla SysApp" if not given yet
        // TODO: To be replaced with "Identity Manager" later on.
        if (ownerIdent == null) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp");
            Simple.startActivity(this, launchIntent);
        }

        // Set outer base content view
        setContentView(R.layout.activity_base);

        // Get references to main child view components
        launcherView = findViewById(R.id.launcher);
        statusBar = findViewById(R.id.statusBar);
        funcContainer = findViewById(R.id.funcContainer);
//
//        // Hide status and action bars
//        hideStatusAndActionBar();

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
    }

    /**
     * Set the main content/func view which is added (attached) to the "func" container.
     *
     * @param layoutResID
     */
    public void setMainFuncView(int layoutResID) {

        // Create "func" view by inflating xml, adding an item adapter
        // and attach it to launcher (main) view
        LayoutInflater inflater = LayoutInflater.from(this);
        FuncBaseView funcBaseView = (FuncBaseView) inflater.inflate(layoutResID, funcContainer, false);
        funcBaseView.setVisibility(View.INVISIBLE);
        funcBaseView.fadeIn();

        // Add view to launcher
        launcherView.addView(funcBaseView);
        funcViewManager.addFuncView(FuncBaseView.FuncType.FULLSCREEN, funcBaseView);
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

            deactivateView((ViewGroup) invokingActionClusterView.getParent());
        } else {
            deactivateBackgroundView();
            toggleClusterButton.minimize();
//            toggleClusterButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp, getTheme()));
        }

        actionClusterView.fadeIn();

        // Put View into list of active Action Cluster Views
        activeActionClusterViews.add(actionClusterView);
    }

    /**
     * Hide action cluster
     */
    public void activateBackgroundView() {

//        contentstreamView.setLayoutFrozen(true);
        contentstreamContainer.setEnabled(true);
        contentstreamLayoutManager.setScrollEnabled(true);
        Blurry.delete(contentstreamContainer);
    }

    /**
     * Hide action cluster
     */
    public void deactivateBackgroundView() {

        contentstreamLayoutManager.setScrollEnabled(false);
        contentstreamContainer.setEnabled(false);
//        contentstreamView.setLayoutFrozen(true);

        Blurry.with(this)
                .radius(blurRadius)
                .sampling(blurSampliong)
//                .color(R.color.color_transparent)
                .animate(blurTransisitionDuration)
                .onto(contentstreamContainer);
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
