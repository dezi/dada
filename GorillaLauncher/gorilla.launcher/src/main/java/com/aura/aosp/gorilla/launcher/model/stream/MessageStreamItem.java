package com.aura.aosp.gorilla.launcher.model.stream;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.GorillaSharable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Message item
 * <p>
 * TODO: This may be subject to be merged with "DraftStreamItem"
 */
public class MessageStreamItem extends AbstractStreamItem implements GorillaSharable, StreamItemInterface {

    final static String LOGTAG = MessageStreamItem.class.getSimpleName();

    protected final GorillaClient gorillaClient = GorillaClient.getInstance();

    protected GorillaMessage gorillaMessage;
    protected User sharedWithUser;

//    protected Long timeReceived;
//    protected Long timePersisted;
//    protected Long timeSent;
//    protected Long timeQeueud;

    /**
     * Create item with owner user and text
     *
     * @param ownerUser
     * @param text
     */
    public MessageStreamItem(@NonNull User ownerUser, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_MESSAGE, ownerUser.getIdentity().getNick(), text, R.drawable.ic_message_black_24dp);
    }

    /**
     * Create item from gorilla message atom.
     *
     * @param ownerUser
     * @param gorillaMessage
     */
    public MessageStreamItem(@NonNull User ownerUser, @NonNull GorillaMessage gorillaMessage) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_MESSAGE, ownerUser.getIdentity().getNick(), gorillaMessage.getMessageText(), R.drawable.ic_message_black_24dp);

        setGorillaMessage(gorillaMessage);
        setTimeCreated(gorillaMessage.getTime());

//        setTimeReceived(gorillaMessage.getStatusTime("received"));
//        setTimePersisted(gorillaMessage.getStatusTime("persisted"));
//        setTimeSent(gorillaMessage.getStatusTime("send"));
//        setTimeQeueud(gorillaMessage.getStatusTime("queued"));
    }

    /**
     * TODO: Decouple from model and move to separate service class
     *
     * @param remoteUser
     */
    public void shareWith(User remoteUser) {
        shareWith(remoteUser.getIdentity());
    }

    /**
     * Share current message with given remote identity
     * TODO: Decouple from model and move to separate service class
     *
     * @param remoteIdentity
     */
    protected void shareWith(Identity remoteIdentity) {

        String remoteUserUUID = remoteIdentity.getUserUUIDBase64();
        String remoteUserDeviceUUID = remoteIdentity.getDeviceUUIDBase64();

        GorillaPayloadResult result = gorillaClient.sendPayload(remoteUserUUID, remoteUserDeviceUUID, getText());

        // TODO: Result and result fields may be null, how to handle?
        Long time = result.getTime();
        String uuid = result.getUUIDBase64();

        GorillaMessage message = new GorillaMessage();

        message.setUUID(uuid);
        message.setTime(time);
        message.setType("aura.chat.message");
        message.setMessageText(getText());

        gorillaClient.putAtomSharedWith(remoteUserUUID, message.getAtom());

        setGorillaMessage(message);

        // Note shared with is not peristed within atom or some related json file yet
        // but only identifiable through directory structure reflecting ownership and share state
        // Will probably change as soon as the underlying storage concept is reworked.
        setSharedWithUser(new User(remoteIdentity));
    }

    /**
     * Dispatch payload result received from a gorilla send/receive event
     *
     * @param result
     */
    public void dispatchResult(GorillaPayloadResult result)
    {
        Long time = result.getTime();
        String uuid = result.getUUIDBase64();
        String status = result.getStatus();

        if ((time == null) || (uuid == null) || (status == null)) return;
        Log.d( "uuid=" + uuid + " status=" + status + " owner nick=" + getOwnerUser().getIdentity().getNick() + " message text=" + getText());

        String remoteUserUUID = getSharedWithUser().getIdentity().getUserUUIDBase64();
        String ownerDeviceUUID = getOwnerUser().getIdentity().getDeviceUUIDBase64();

        getGorillaMessage().setStatusTime(status, ownerDeviceUUID, time);

        gorillaClient.putAtomSharedWith(remoteUserUUID, getGorillaMessage().getAtom());
    }

    @Override
    public Integer getImageId() {
        return getOwnerUser().getContactAvatarImageRes();
    }

    @Override
    public boolean isPreviewViewed() {
        // TODO: Adjust!
        return getAtomStatusTime("read") != null;
    }

    @Override
    public boolean isFullyViewed() {
        // TODO: Adjust!
        return isPreviewViewed();
    }

    /**
     * Mark messages as "read" on "preview viewed" event
     */
    @Override
    public void onPreviewViewed() {
        // TODO: Adjust!
        if (getGorillaMessage() == null || !getSharedWithUser().equals(getOwnerUser())) {
            return;
        }

        setAtomStatusTime("read", System.currentTimeMillis());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String remoteUserUUID = getSharedWithUser().getIdentity().getUserUUIDBase64();
                String remoteDeviceUUID = getSharedWithUser().getIdentity().getDeviceUUIDBase64();

                if (gorillaClient.sendPayloadRead(remoteUserUUID, remoteDeviceUUID, getGorillaMessage().getUUIDBase64())) {
                    gorillaClient.putAtomSharedBy(remoteUserUUID, getGorillaMessage().getAtom());
                }
            }

        }, 1000);
    }

    @Override
    public void onFullyViewed() {
        // TODO: Adjust!
        onPreviewViewed();
    }

    /**
     * @param remoteUser
     */
    @Override
    public void unshareWith(User remoteUser) {
        // TODO: Implement (for instant "chat" messages we should probably solve
        // TODO: it in WhatsApp style: revokable for 2 min or so...)
    }

    protected GorillaMessage getGorillaMessage() {
        return gorillaMessage;
    }

    protected void setGorillaMessage(GorillaMessage gorillaMessage) {
        this.gorillaMessage = gorillaMessage;
    }

    public User getSharedWithUser() {
        return sharedWithUser;
    }

    public void setSharedWithUser(User sharedWithUser) {
        this.sharedWithUser = sharedWithUser;
    }

    /**
     * Get UUID of atom
     *
     * @return
     */
    @Nullable
    public String getAtomUUID() {

        if (getGorillaMessage() == null) {
            return null;
        }

        return getGorillaMessage().getUUIDBase64();
    }

    /**
     * Get time for a specific status from atom
     *
     * @param status
     * @return
     */
    @Nullable
    public Long getAtomStatusTime(String status) {

        if (getGorillaMessage() == null) {
            return null;
        }

        return getGorillaMessage().getStatusTime(status);
    }

    /**
     * Set time for a specific status in atom
     *
     * @param status
     * @param time
     */
    public void setAtomStatusTime(@NonNull String status, @NonNull Long time) {

        if (getGorillaMessage() == null || getAtomStatusTime(status) != null) {
            return;
        }

        String ownerDeviceUUID = getOwnerUser().getIdentity().getDeviceUUIDBase64();

        getGorillaMessage().setStatusTime(status, ownerDeviceUUID, time);

    }
}
