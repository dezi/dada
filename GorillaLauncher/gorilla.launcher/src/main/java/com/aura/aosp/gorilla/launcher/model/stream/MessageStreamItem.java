package com.aura.aosp.gorilla.launcher.model.stream;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
public class MessageStreamItem extends DraftStreamItem implements GorillaSharable, StreamItemInterface {

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

        super(ownerUser, text);

        setTitle(ownerUser.getIdentity().getNick());
        setImageId(R.drawable.ic_message_black_24dp);
        setType(ItemType.TYPE_STREAMITEM_MESSAGE);
    }

    /**
     * Create item from gorilla message atom.
     *
     * @param ownerUser
     * @param gorillaMessage
     */
    public MessageStreamItem(@NonNull User ownerUser, @NonNull GorillaMessage gorillaMessage) {

        super(ownerUser, gorillaMessage.getMessageText());

        setGorillaMessage(gorillaMessage);

        setTitle(ownerUser.getIdentity().getNick());
        setImageId(R.drawable.ic_message_black_24dp);
        setType(ItemType.TYPE_STREAMITEM_MESSAGE);

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
     * TODO: Decouple from model and move to separate service class
     *
     * @param remoteIdentity
     */
    protected void shareWith(Identity remoteIdentity) {

        GorillaPayloadResult result = gorillaClient.sendPayload(
                remoteIdentity.getUserUUIDBase64(), remoteIdentity.getDeviceUUIDBase64(), getText());

        // TODO: Result and result fields may be null, how to handle?
        Long time = result.getTime();
        String uuid = result.getUUIDBase64();

        // TODO: How to convert an note atom into a shared atom?
        GorillaMessage message = new GorillaMessage();

        message.setUUID(uuid);
        message.setTime(time);
        message.setType("aura.chat.message");
        message.setMessageText(getText());

        gorillaClient.putAtomSharedWith(remoteIdentity.getUserUUIDBase64(), message.getAtom());

        setGorillaMessage(message);

        // Note shared with is not peristed within atom or some related json file yet
        // but only identifiable through directory structure reflecting ownership and share state
        // Will probably change as soon as the underlying storage concept is reworked.
        setSharedWithUser(new User(remoteIdentity));
    }

    /**
     * Mark messages as "read" on "preview viewed" event
     *
     * @param user
     */
    @Override
    public void onPreviewViewed(User user) {

        if (getGorillaMessage() == null || user.equals(getOwnerUser())) {
            return;
        }

        setAtomTimeRead(System.currentTimeMillis());
    }

    @Override
    public void onFullyViewed(User user) {
        // TODO: Fix, differentiate!
        onPreviewViewed(user);
    }

    /**
     * @param remoteUser
     */
    @Override
    public void unshareWith(User remoteUser) {
        // TODO: Implement (for instant "chat" messages we should probably solve
        // TODO: it in WhatsApp style: revokable for 2 min or so...)
    }

    public GorillaMessage getGorillaMessage() {
        return gorillaMessage;
    }

    public void setGorillaMessage(GorillaMessage gorillaMessage) {
        this.gorillaMessage = gorillaMessage;
    }

    public User getSharedWithUser() {
        return sharedWithUser;
    }

    public void setSharedWithUser(User sharedWithUser) {
        this.sharedWithUser = sharedWithUser;
    }

    /**
     * @return
     */
    @Nullable
    public Long getAtomTimeReadFrom() {

        if (getGorillaMessage() == null) {
            return null;
        }

        return getGorillaMessage().getStatusTime("read");
    }

    /**
     * @param timeRead
     */
    public void setAtomTimeRead(Long timeRead) {

        if (getGorillaMessage() == null || getAtomTimeReadFrom() != null) {
            return;
        }

        String ownerDeviceUUID = getOwnerUser().getIdentity().getDeviceUUIDBase64();

        getGorillaMessage().setStatusTime("read", ownerDeviceUUID, timeRead);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String remoteUserUUID = getSharedWithUser().getIdentity().getUserUUIDBase64();
                String remoteDeviceUUID = getSharedWithUser().getIdentity().getDeviceUUIDBase64();

                boolean ok = gorillaClient.sendPayloadRead(remoteUserUUID, remoteDeviceUUID, uuid);

                if (ok) {
                    gorillaClient.putAtomSharedBy(remoteUserUUID, getGorillaMessage().getAtom());
                }
            }

        }, 1000);
    }

//    public Long getTimeReceived() {
//        return timeReceived;
//    }
//
//    public void setTimeReceived(Long timeReceived) {
//        this.timeReceived = timeReceived;
//    }
//
//    public Long getTimePersisted() {
//        return timePersisted;
//    }
//
//    public void setTimePersisted(Long timePersisted) {
//        this.timePersisted = timePersisted;
//    }
//
//    public Long getTimeSent() {
//        return timeSent;
//    }
//
//    public void setTimeSent(Long timeSent) {
//        this.timeSent = timeSent;
//    }
//
//    public Long getTimeQeueud() {
//        return timeQeueud;
//    }
//
//    public void setTimeQeueud(Long timeQeueud) {
//        this.timeQeueud = timeQeueud;
//    }
}
