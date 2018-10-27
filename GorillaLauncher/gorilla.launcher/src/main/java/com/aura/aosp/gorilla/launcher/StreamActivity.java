package com.aura.aosp.gorilla.launcher;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;
import com.aura.aosp.gorilla.launcher.model.GorillaHelper;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.InvokerActionItem;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.MessageStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface;
import com.aura.aosp.gorilla.launcher.model.user.User;
import com.aura.aosp.gorilla.launcher.store.StreamStore;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;
import com.aura.aosp.gorilla.launcher.ui.common.SmartScrollableLayoutManager;
import com.aura.aosp.gorilla.launcher.ui.common.UserAvatarImage;
import com.aura.aosp.gorilla.launcher.ui.content.StreamAdapter;
import com.aura.aosp.gorilla.launcher.ui.content.StreamView;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterAdapter;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main activity, i.e. the "launcher" screen
 */
public class StreamActivity extends LauncherActivity {

    private final static String LOGTAG = StreamActivity.class.getSimpleName();

    private String currentAtomContext = StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES;

    private StreamStore streamStore;

    private StreamView streamView;
    private FilteredStream filteredStream;
    private StreamAdapter streamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.d("onCreate: ...");

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
        streamView.setMyUser(getMyUser());
        streamView.setVisibility(View.INVISIBLE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        streamView.setHasFixedSize(true);

        // Add view to container
        mainContentContainer.addView(streamView);

        // use a linear layout manager
        SmartScrollableLayoutManager streamLayoutManager = new SmartScrollableLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);

        streamView.setLayoutManager(streamLayoutManager);

        // Register layout manger with "main content smart scrollable layout managers"
        mcSmartScrollableLayoutManagers.add(streamLayoutManager);

        // Create initial stream store
        streamStore = new StreamStore(getApplicationContext());

        // Create initial content stream items (empty list)
//        filteredStream = streamStore.getItemsForAtomContext(getCurrentAtomContext(), getMyUser());
        filteredStream = new FilteredStream();

        // Create list view adapter and attach to stream view
        streamAdapter = new StreamAdapter(filteredStream, this, this);
        streamView.setAdapter(streamAdapter);

        // Subscribe to Gorilla listener
        GorillaClient.getInstance().subscribeGorillaListener(listener);
    }

    /**
     * Create main stream items
     * TODO: Add "intelligent" refresh methods taking advantage of scoring
     */
    protected void reloadStreamItems(@Nullable String atomContext) {

        if (atomContext == null) {
            atomContext = getCurrentAtomContext();
        }

        filteredStream = streamStore.getItemsForAtomContext(atomContext, getMyUser());
        streamAdapter.setStreamItems(filteredStream);
        streamView.setAdapter(streamAdapter);
//        streamAdapter = new StreamAdapter(filteredStream, this, this);
//        streamView.setAdapter(streamAdapter);
        streamAdapter.notifyDataSetChanged();
    }

    /**
     * Remove active func views and action cluster layers and start
     * with refreshed stream view.
     */
    public void onReturnToStream(boolean reloadItems) {

        removeMainFuncView();
        deactivateAllActionsClusterViews();
        activateMainContentView();

        if (reloadItems) {
            reloadStreamItems(null);

            if (getCurrentAtomContext().equals(StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES)) {
                streamView.smoothScrollToStreamEnd();
            }
        }
    }

    /**
     * ACTION: "Stream"
     */
    public void onOpenStream() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                streamView.fadeIn();
                streamView.smoothScrollToStreamEnd();
            }
        }, getResources().getInteger(R.integer.streamview_onopen_effects_delay));
    }

    /**
     * ACTION: "Open Content Composer"
     */
    public void onOpenContentComposer(@Nullable User contactUser) {

        Log.d("contactUser <%s>", contactUser.getIdentity().getNick());

//        Effects.fadeOutView(actionClusterContainer, this, null);
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(actionClusterContainer);
//        constraintSet.connect(R.id.actionClusterRecycler, ConstraintSet.TOP, R.id.editText, ConstraintSet.BOTTOM, 0);
//        constraintSet.applyTo(actionClusterContainer);

        setMainFuncView(R.layout.func_content_composer, true);

        ConstraintLayout recipientListContainer = mainFuncView.findViewById(R.id.recipientListContainer);

        // Create avatar image manually
        UserAvatarImage contactAvatarImage = new UserAvatarImage(this, contactUser);

        recipientListContainer.addView(contactAvatarImage);
        Effects.fadeInView(contactAvatarImage, this, null);

        // Create extra action cluster (disabled avatar items only) for content editor:
        List<ActionItem> recipientActionItems = new ArrayList<>();

        try {
            recipientActionItems.add(new ActionItem(
                    contactUser.getIdentity().getFull(),
                    R.drawable.ic_person_black_24dp,
                    1.0f
            ));

            recipientActionItems.add(new InvokerActionItem(
                    getResources().getString(R.string.actions_openCalendar),
                    R.drawable.ic_add_a_photo_black_24dp,
                    0.870f,
                    StreamActivity.class.getMethod("onOpenSimpleCalendar")
            ));
        } catch (NoSuchMethodException e) {

            Log.e("No such action invocation invokeMethod found: <%s>",
                    e.getMessage());
        }

        ActionCluster recipientActionCluster = new ActionCluster("recipients", recipientActionItems);
//        ActionClusterView recipientAvatarClusterView = createActionClusterView(recipientActionCluster, null, false);

        final ActionClusterView recipientAvatarClusterView = (ActionClusterView) getLayoutInflater().inflate(
                R.layout.fragment_actioncluster_horizontal, recipientListContainer, false);

        recipientAvatarClusterView.setVisibility(View.INVISIBLE);

//        // use a linear layout manager
//        SmartScrollableLayoutManager layoutManager = new SmartScrollableLayoutManager(actionClusterContainer.getContext(), nextLayOrientation, true);
//        actionClusterView.setLayoutManager(layoutManager);

        // specify adapter
        ActionClusterAdapter recipientActionClusterAdapter = new ActionClusterAdapter(
                recipientActionCluster.getItemsByRelevance(), this, this);

        recipientAvatarClusterView.setAdapter(recipientActionClusterAdapter);

        recipientListContainer.addView(recipientAvatarClusterView);

        recipientAvatarClusterView.fadeIn();


        // Get base action cluster for current view
        // TODO: Fix, solve generically by using an actionClusterManager which know about current hierarchy and context!
        actionClusterStore.setContext(this);

        ActionClusterView activeActionClusterView = getBaseActionClusterView(false);

        ActionClusterAdapter actionClusterAdapter = (ActionClusterAdapter) activeActionClusterView.getAdapter();
        ActionCluster cocoActionCluster = actionClusterStore.getClusterForAction(
                "func.content_composer", contactUser);

        actionClusterAdapter.setActionItems(cocoActionCluster.getActionItems());

        activeActionClusterView.setSticky(true);

//        // For creating a new action cluster view instead of transitioning existing one:
//        createActionClusterView(cocoActionCluster, null, true);

        // TODO: This is hacked! Use e.g. constraints to reposition and/or perform a view transition
        activeActionClusterView.setY(42f);
    }

    /**
     * ACTION: "Open Content Composer"
     */
    public void onSendMessage(User contactUser) {

        EditText editTextView = (EditText) findViewById(R.id.editText);
        String messageText = editTextView.getText().toString();

        Log.d("contactUser <%s>", contactUser.getIdentity().getNick());
        Log.d("message <%s>", messageText);

        MessageStreamItem messageStreamItem = new MessageStreamItem(getMyUser() , getMyUser(), messageText);
        messageStreamItem.shareWith(contactUser);

        int nextPos = filteredStream.size();
        filteredStream.add(nextPos, messageStreamItem);
//        filteredStream.sortyByCreateTime(true);
        streamAdapter.notifyItemInserted(nextPos);

        // TODO: Cleanup (generalize) all this hide/show stuff for SDK default control/status views!
        hideKeyboard(this);
        hideStatusAndActionBar();

        setCurrentAtomContext(StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES);
        onReturnToStream(false);
        streamView.smoothScrollToStreamEnd();
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

    public String getCurrentAtomContext() {
        return currentAtomContext;
    }

    public void setCurrentAtomContext(String nextAtomContext) {
        if (! currentAtomContext.equals(nextAtomContext)) {
            currentAtomContext = nextAtomContext;
            onReturnToStream(true);
        } else {
            onReturnToStream(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        streamView.fadeIn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        streamView.fadeOut();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onReturnToStream(false);
    }

    /**
     * Gorilla Listener: Listen for owner change (my identity) and atom send + receive events
     */
    private final GorillaListener listener = new GorillaListener() {

        @Override
        public void onOwnerReceived(GorillaOwner owner) {
            Log.d("owner=" + owner.toString());

            String myUUID = owner.getOwnerUUIDBase64();
            Identity myIdentity = Contacts.getContact(myUUID);

            if (getMyUser() == null || (!getMyUser().getIdentity().getDeviceUUIDBase64().equals(myIdentity.getUserUUIDBase64()))) {

                Log.d("nick=" + myIdentity.getNick());
                Log.d("uuid=" + UID.convertUUIDToString(myIdentity.getUserUUID()));

                setMyUser(new User(myIdentity));

                if (statusBar != null) {
                    statusBar.setProfileInfo(myUser.getIdentity().getNick());
                }
            }

            reloadStreamItems(null);
            onOpenStream();
        }

        @Override
        public void onPayloadReceived(GorillaPayload payload) {

            Log.d("payload=" + payload.toString());

            GorillaMessage message = convertPayloadToMessageAndPersist(payload);
            if (message == null) return;

            final String remoteUserUUID = payload.getSenderUUIDBase64();

            User ownerUser = new User(Contacts.getContact(remoteUserUUID));

            int nextPos = filteredStream.size();
            filteredStream.add(nextPos, new MessageStreamItem(getMyUser(), ownerUser, message));
//            filteredStream.sortyByCreateTime(true);
            streamAdapter.notifyItemInserted(nextPos);

            if (getCurrentAtomContext().equals(StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES)) {
                streamView.smoothScrollToStreamEnd();
            }
        }

        @Override
        public void onPayloadResultReceived(GorillaPayloadResult result) {
            Log.d("result=" + result.toString());

            String uuid = result.getUUIDBase64();

            FilteredStream shadowFilteredStream;

            shadowFilteredStream = streamStore.getItemsForAtomContext(StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES, getMyUser());

//            if (getCurrentAtomContext() != StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES) {
//                shadowFilteredStream = streamStore.getItemsForAtomContext(StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES, getMyUser());
//            } else {
//                shadowFilteredStream = filteredStream;
//            }

            // TODO: Encapsulate: Load atom via store, modify (database/file) item and
            // TODO: look for current stream item occurences for refresh!
            for (int i = 0; i < shadowFilteredStream.size(); i++) {
                StreamItemInterface streamItem = shadowFilteredStream.get(i);

                if (streamItem.getType() == StreamItemInterface.ItemType.TYPE_STREAMITEM_MESSAGE)
                {
                    MessageStreamItem messageStreamItem = (MessageStreamItem) streamItem;

                    if (messageStreamItem.getAtomUUID().equals(uuid)) {
                        messageStreamItem.dispatchShareWithResult(result);
                        break;
                    }

                    streamAdapter.notifyItemChanged(i);
                }
            }
        }

        @Nullable
        private GorillaMessage convertPayloadToMessageAndPersist(GorillaPayload payload) {

            Long time = payload.getTime();
            String uuid = payload.getUUIDBase64();
            String text = payload.getPayload();
            String remoteUserUUID = payload.getSenderUUIDBase64();
            String ownerDeviceUUID = getMyUser().getIdentity().getDeviceUUIDBase64();

            if ((time == null) || (uuid == null) || (text == null) || (remoteUserUUID == null)) {
                Log.e("invalid payload=" + payload.toString());
                return null;
            }

            if (ownerDeviceUUID == null) {
                Log.e("unknown owner device");
                return null;
            }

            GorillaMessage message = new GorillaMessage();

            message.setType(GorillaHelper.ATOMTYPE_CHAT_MESSAGE);
            message.setTime(time);
            message.setUUID(uuid);
            message.setMessageText(text);
            message.setStatusTime("received", ownerDeviceUUID, System.currentTimeMillis());

            if (!GorillaClient.getInstance().putAtomSharedBy(remoteUserUUID, message.getAtom())) {
                return null;
            }

            return message;
        }
    };
}
