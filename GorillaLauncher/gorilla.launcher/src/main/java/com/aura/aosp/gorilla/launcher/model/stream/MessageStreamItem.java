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
import com.aura.aosp.gorilla.launcher.model.GorillaHelper;
import com.aura.aosp.gorilla.launcher.model.GorillaSharable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Message item (chat messages shared via Gorilla)
 */
public class MessageStreamItem extends AbstractStreamItem implements GorillaSharable, StreamItemInterface {

    final static String LOGTAG = MessageStreamItem.class.getSimpleName();

    private final GorillaClient gorillaClient = GorillaClient.getInstance();

    private GorillaMessage gorillaMessage;
    private User sharedWithUser;

    /**
     * Create item with owner user and text
     *
     * @param myUser
     * @param ownerUser
     * @param text
     */
    public MessageStreamItem(@NonNull User myUser, @NonNull User ownerUser, @NonNull String text) {
        super(myUser, ownerUser, ItemType.TYPE_STREAMITEM_MESSAGE, ownerUser.getIdentity().getNick(), text, R.drawable.ic_chat_bubble_outline_black_24dp);
    }

    /**
     * Create item from gorilla message atom.
     *
     * @param myUser
     * @param ownerUser
     * @param gorillaMessage
     */
    public MessageStreamItem(@NonNull User myUser, @NonNull User ownerUser, @NonNull GorillaMessage gorillaMessage) {
        super(myUser, ownerUser, ItemType.TYPE_STREAMITEM_MESSAGE, ownerUser.getIdentity().getNick(), gorillaMessage.getMessageText(), R.drawable.ic_chat_bubble_outline_black_24dp);

        setGorillaMessage(gorillaMessage);
        setTimeCreated(gorillaMessage.getTime());
    }

    /**
     * Share message with given remote user.
     *
     * @param remoteUser
     */
    public void shareWith(User remoteUser) {
        shareWith(remoteUser.getIdentity());
    }

    /**
     * Share current message with given remote identity.
     * Don't allow sharing with own identity.
     *
     * @param remoteIdentity
     */
    private void shareWith(Identity remoteIdentity) {

        if (!isMyItem()) {
            return;
        }

        String remoteUserUUID = remoteIdentity.getUserUUIDBase64();
        String remoteUserDeviceUUID = remoteIdentity.getDeviceUUIDBase64();

        GorillaPayloadResult result = gorillaClient.sendPayload(remoteUserUUID, remoteUserDeviceUUID, getText());
        if (result == null) return;

        Long time = result.getTime();
        if (time == null) return;

        String uuid = result.getUUIDBase64();
        if (uuid == null) return;

        GorillaMessage message = new GorillaMessage();

        message.setUUID(uuid);
        message.setTime(time);
        message.setType(GorillaHelper.ATOMTYPE_CHAT_MESSAGE);
        message.setMessageText(getText());

        gorillaClient.putAtomSharedWith(remoteUserUUID, message.getAtom());

        setGorillaMessage(message);
        setSharedWithUser(new User(remoteIdentity));

        dispatchShareWithResult(result);
    }

    /**
     * Unshare current message with given remote identity (revoke).
     *
     * @param remoteUser
     */
    @Override
    public void unshareWith(User remoteUser) {
        // TODO: Implement (for instant "chat" messages we should probably solve
        // TODO: it in WhatsApp style: revokable for 2 min or so...)
    }

    /**
     * Dispatch payload result received from a gorilla send (share) action.
     *
     * @param result
     */
    public void dispatchShareWithResult(GorillaPayloadResult result) {

        if (!isMyItem()) {
            return;
        }

        Long time = result.getTime();
        String uuid = result.getUUIDBase64();
        String status = result.getStatus();

        if ((time == null) || (uuid == null) || (status == null)) return;
        Log.d("uuid=" + uuid + " status=" + status + " owner nick=" + getOwnerUser().getIdentity().getNick() + " message text=" + getText());

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
        return shareIsRead();
    }

    @Override
    public boolean isFullyViewed() {
        // TODO: Adjust!
        return isPreviewViewed();
    }

    @Override
    public void onPreviewViewed() {

        if (shareIsRead()) {
            return;
        }

        setAtomStatusTime("read", System.currentTimeMillis());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String remoteUserUUID = getOwnerUser().getIdentity().getUserUUIDBase64();
                String remoteDeviceUUID = getOwnerUser().getIdentity().getDeviceUUIDBase64();

                if (gorillaClient.sendPayloadRead(remoteUserUUID, remoteDeviceUUID, getGorillaMessage().getUUIDBase64())) {
                    gorillaClient.putAtomSharedBy(remoteUserUUID, getGorillaMessage().getAtom());
                }
            }
        }, 2000);
    }

    @Override
    public void onFullyViewed() {
        // TODO: Adjust!
        onPreviewViewed();
    }

    private GorillaMessage getGorillaMessage() {
        return gorillaMessage;
    }

    private void setGorillaMessage(GorillaMessage gorillaMessage) {
        this.gorillaMessage = gorillaMessage;
    }

    /**
     * Check if message is marked as read or assumed to be read due to ownership of device user identity.
     *
     * @return
     */
    public boolean shareIsQueued() {

        return isMyItem()
                || getSharedWithUser() == null
                || getAtomStatusTime("queued") != null;
    }

    /**
     * Check if message is marked as read or assumed to be read due to ownership of device user identity.
     *
     * @return
     */
    public boolean shareIsSent() {

        return isMyItem()
                || getSharedWithUser() == null
                || getAtomStatusTime("send") != null;
    }

    /**
     * Check if message is marked as read or assumed to be read due to ownership of device user identity.
     *
     * @return
     */
    public boolean shareIsPersisted() {

        return isMyItem()
                || getSharedWithUser() == null
                || getAtomStatusTime("persisted") != null;
    }

    /**
     * Check if message is marked as read or assumed to be read due to ownership of device user identity.
     *
     * @return
     */
    public boolean shareIsReceived() {

        return isMyItem()
                || getSharedWithUser() == null
                || getAtomStatusTime("received") != null;
    }

    /**
     * Check if message is marked as read or assumed to be read due to ownership of device user identity.
     *
     * @return
     */
    public boolean shareIsRead() {

        return isMyItem()
                || getSharedWithUser() == null
                || getAtomStatusTime("read") != null;
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
    private Long getAtomStatusTime(@NonNull String status) {

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
    private void setAtomStatusTime(@NonNull String status, @NonNull Long time) {

        if (getGorillaMessage() == null || getAtomStatusTime(status) != null) {
            return;
        }

        String ownerDeviceUUID = getOwnerUser().getIdentity().getDeviceUUIDBase64();

        getGorillaMessage().setStatusTime(status, ownerDeviceUUID, time);
    }

    private User getSharedWithUser() {
        return sharedWithUser;
    }

    /**
     * Set the user this message is shared with via Gorilla.
     * <p>
     * Note: shared with is not peristed within atom or some related json file yet
     * but only identifiable through directory structure reflecting ownership and share state
     * Will probably change as soon as the underlying storage concept is reworked.
     *
     * @param sharedWithUser
     */
    public void setSharedWithUser(User sharedWithUser) {
        this.sharedWithUser = sharedWithUser;
    }
}
