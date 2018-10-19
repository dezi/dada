package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.GorillaSharable;
import com.aura.aosp.gorilla.launcher.model.user.User;

import java.util.List;

/**
 * Message item
 * TODO: This may be subject to be merged with "DraftStreamItem"
 */
public class MessageStreamItem extends DraftStreamItem implements GorillaSharable {

    final static String LOGTAG = MessageStreamItem.class.getSimpleName();

    public MessageStreamItem(@NonNull User ownerUser, @NonNull String text) {
        super(ownerUser, text);
        setImageId(R.drawable.ic_message_black_24dp);
    }

    public void shareWith(User remoteUser) {
        shareWith(remoteUser.getIdentity());
    }

    public void shareWith(Identity remoteIdentity) {

        // TODO: Replace with sendPayload(), this is just for testing.
        GorillaPayloadResult result = GorillaClient.getInstance().sendPayloadQuer("com.aura.aosp.gorilla.messenger", remoteIdentity.getUserUUIDBase64(),
                remoteIdentity.getDeviceUUIDBase64(), getText());

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

    @Override
    public void unshareWith(User remoteUser) {
        // TODO: Implement
    }

    public boolean isReceivedByOwner() {
        return true;
        // TODO: Implement
    }

    public boolean ieSentByOwner() {
        return false;
        // TODO: Implement
    }
}
