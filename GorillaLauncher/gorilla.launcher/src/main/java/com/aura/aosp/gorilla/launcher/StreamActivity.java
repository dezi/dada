package com.aura.aosp.gorilla.launcher;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.StreamItemMessage;
import com.aura.aosp.gorilla.launcher.store.StreamStore;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.content.StreamAdapter;
import com.aura.aosp.gorilla.launcher.ui.content.StreamView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterAdapter;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;

import org.json.JSONObject;

/**
 * Main activity, i.e. the "launcher" screen
 */
public class StreamActivity extends LauncherActivity {

    private final static String LOGTAG = StreamActivity.class.getSimpleName();

    //    public static List<ChatProfile> chatProfiles = new ArrayList<>();

    private StreamStore streamStore;

    private StreamView streamView;
    private RecyclerView streamRecyclerView;
    private StreamAdapter streamAdapter;
    private SmartScrollableLayoutManager streamLayoutManager;

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

        // Create "stream" view by inflating xml, adding an item adapter
        // and attach it to main content container
        LayoutInflater inflater = LayoutInflater.from(this);
        streamView = (StreamView) inflater.inflate(R.layout.func_stream, mainContentContainer, false);
        streamView.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        streamView.setHasFixedSize(true);

        // Add view to container
        mainContentContainer.addView(streamView);

        // Get references to main child view components
        streamRecyclerView = launcherView.findViewById(R.id.streamRecycler);

        // Subscribe to Gorilla listener
        GorillaClient.getInstance().subscribeGorillaListener(listener);
    }

    /**
     * Create main stream items
     */
    protected void createStream() {

        // use a linear layout manager
        streamLayoutManager = new SmartScrollableLayoutManager(this);
        streamRecyclerView.setLayoutManager(streamLayoutManager);

        // Register layout manger with "main content smart scrollable layout managers"
        mcSmartScrollableLayoutManagers.add(streamLayoutManager);

        // TODO: Replace with aggregated stream items from "Gorilla Content Stream Atoms"
        // Create initial content stream items and specify adapter
        streamStore = new StreamStore(getApplicationContext());

        refreshStreamItems();
    }

    /**
     * Create main stream items
     * TODO: Add "intelligent" refresh methods taking advantage of scoring
     */
    protected void refreshStreamItems() {

        streamAdapter = new StreamAdapter(streamStore.getItemsForAtomContext("aura.uxtream.launcher", getOwnerIdent()), this, this);
        streamRecyclerView.setAdapter(streamAdapter);
    }

    /**
     * ACTION: "Stream"
     */
    public void onOpenStream() {

        // Create and display stream items
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                streamView.fadeIn();
            }
        }, 600);
    }

    /**
     * Remove active func views and action cluster layers and start
     * with refreshed stream view.
     */
    public void onReturnToStream() {
        removeMainFuncView();
//        deactivateAllActionsClusterViews();
        activateMainContentView();
        refreshStreamItems();
    }

    /**
     * ACTION: "Open Content Composer"
     */
    public void onOpenContentComposer(@Nullable Identity identity) {

//        Effects.fadeOutView(actionClusterContainer, this, null);
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(actionClusterContainer);
//        constraintSet.connect(R.id.actionClusterRecycler, ConstraintSet.TOP, R.id.editText, ConstraintSet.BOTTOM, 0);
//        constraintSet.applyTo(actionClusterContainer);

        setMainFuncView(R.layout.func_content_composer, true);
        Log.d(LOGTAG, String.format("onOpenContentComposer for contactIdentity <%s>", identity.getNick()));

        actionClusterStore.setContext(this);

        // TODO: Fix, solve generically by using an actionClusterManager which know about current hierarchy and context!
        ActionClusterView activeActionClusterView = getBaseActionClusterView(false);

        ActionClusterAdapter actionClusterAdapter = (ActionClusterAdapter) activeActionClusterView.getAdapter();
        ActionCluster cocoActionCluster = actionClusterStore.getClusterForSelectedIdentity("com.aura.aosp.gorilla.func.content_composer", identity);

        actionClusterAdapter.setActionItems(cocoActionCluster.getActionItems());

        activeActionClusterView.setSticky(true);

        // TODO: This is hacked! Use e.g. constraints to reposition and/or perform a view transition
        activeActionClusterView.setY(40f);

//        actionClusterStore = new ActionClusterStore(this);
//
//        ActionCluster itemActionCluster = actionClusterStore.getClusterForSelectedIdentity(
//                "com.aura.aosp.gorilla.func.content_composer", identity);
//
//        createActionClusterView(itemActionCluster, null, true);

    }

    /**
     * ACTION: "Open Content Composer"
     */
    public void onSendMessage(Identity identity) {

        Log.d(LOGTAG, String.format("onSendMessage for contactIdentity <%s>", identity.getNick()));
        Log.d(LOGTAG, String.format("onSendMessage ownerIdentity <%s>", getOwnerIdent().getNick()));

        EditText editTextView = (EditText) findViewById(R.id.editText);

        String messageText = editTextView.getText().toString();

        Log.d(LOGTAG, String.format("onSendMessage message <%s>", messageText));

        StreamItemMessage streamItemMessage = new StreamItemMessage(getOwnerIdent(), messageText);
        streamItemMessage.shareWith(identity);

        // TODO: CONTINNUE HERE! Read messages from goatoms and put into stream!
        // TODO: CONTINNUE HERE! Cleanup all this hide/show stuff!
        hideKeyboard(this);
        hideStatusAndActionBar();
        onReturnToStream();
    }

    /**
     * ACTION: "Open Calendar"
     */
    public void onOpenSimpleCalendar() {

        setMainFuncView(R.layout.func_simple_calendar, true);
    }

    /**
     * ACTION: "Alarm Clock"
     */
    public void onOpenAlarmClock() {

        onOpenSimpleCalendar();
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
        onOpenStream();
    }

    @Override
    protected void onPause() {
        super.onPause();
        streamView.fadeOut();
    }

    /**
     * Gorilla Listener for receiving/sending actions and atoms
     */
    private final GorillaListener listener = new GorillaListener() {

        @Override
        public void onOwnerReceived(GorillaOwner owner) {
            Log.d(LOGTAG, "onOwnerReceived: +++++ CURRENT +++++ owner=" + owner.toString());

            String ownerUUID = owner.getOwnerUUIDBase64();

            ownerIdent = Contacts.getContact(ownerUUID);

            if (ownerIdent != null) {

                Log.d(LOGTAG, "onOwnerReceived: +++++ CONTACT +++++ nick=" + ownerIdent.getNick());
                createStream();
                onOpenStream();
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
