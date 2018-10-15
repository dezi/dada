package com.aura.aosp.gorilla.launcher.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;

import java.util.List;

/**
 * Message item
 * TODO: This may be subject to be merged with "StreamItemNote"
 */
public class StreamItemMessage extends StreamItemNote implements GorillaSharable {

    final static String LOGTAG = StreamItemMessage.class.getSimpleName();

    public StreamItemMessage(@NonNull Identity ownerIdentity, @NonNull String text) {
        super(ownerIdentity, text);
    }

    @Override
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
    public void shareWith(List<Identity> remoteIdentities) {
        for (Identity identity : remoteIdentities) {
            shareWith(identity);
        }
    }

    @Override
    public void unshareWith(Identity remoteIdentity) {
        // TODO: Implement
    }
}
