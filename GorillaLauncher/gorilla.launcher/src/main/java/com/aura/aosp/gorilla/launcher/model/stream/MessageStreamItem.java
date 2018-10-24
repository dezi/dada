package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.GorillaSharable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Message item
 *
 * TODO: This may be subject to be merged with "DraftStreamItem"
 */
public class MessageStreamItem extends DraftStreamItem implements GorillaSharable, StreamItemInterface {

    final static String LOGTAG = MessageStreamItem.class.getSimpleName();

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
     * @param gorillaMessage
     */
    public MessageStreamItem(@NonNull User ownerUser, GorillaMessage gorillaMessage) {
        super(ownerUser, gorillaMessage.getMessageText());
        setTitle(ownerUser.getIdentity().getNick());
        setCreateTime(gorillaMessage.getTime());
        setImageId(R.drawable.ic_message_black_24dp);
        setType(ItemType.TYPE_STREAMITEM_MESSAGE);

//        setCreateTime(gorillaMessage.getStatusTime("received"));
//        setCreateTime(gorillaMessage.getStatusTime("queued"));
    }

    /**
     * TODO: Decouple from model and move to separate service class
     * @param remoteUser
     */
    public void shareWith(User remoteUser) {
        shareWith(remoteUser.getIdentity());
    }

    /**
     * TODO: Decouple from model and move to separate service class
     * @param remoteIdentity
     */
    public void shareWith(Identity remoteIdentity) {

        GorillaPayloadResult result = GorillaClient.getInstance().sendPayload(
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

        GorillaClient.getInstance().putAtomSharedWith(remoteIdentity.getUserUUIDBase64(), message.getAtom());
    }

    /**
     * TODO: Decouple from model and move to separate service class
     * @param remoteUser
     */
    @Override
    public void unshareWith(User remoteUser) {
        // TODO: Implement
    }

    /**
     * TODO: Decouple from model and move to separate service class
     * @return
     */
    public boolean isReceivedByOwner() {
        return true;
        // TODO: Implement
    }

    /**
     * TODO: Decouple from model and move to separate service class
     * @return
     */
    public boolean ieSentByOwner() {
        return false;
        // TODO: Implement
    }

    /**
     * @return
     */
    public boolean ownerMatchesUser(User user) {
        return getOwnerUser().equals(user);
    }
}
