package com.aura.aosp.gorilla.launcher.model;

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

    public StreamItemMessage(String text) {
        super(text);
    }

    @Override
    public void shareWith(Identity remoteIdentity) {

        GorillaPayloadResult result = GorillaClient.getInstance().sendPayload(remoteIdentity.getUserUUID().toString(),
                remoteIdentity.getDeviceUUID().toString(), getText());

        if (result == null) return;

        Long time = result.getTime();
        if (time == null) return;

        String uuid = result.getUUIDBase64();
        if (uuid == null) return;

        // TODO: How to change an usual atom into a shared atom?
        GorillaMessage message = new GorillaMessage();

        message.setUUID(uuid);
        message.setTime(time);
        message.setType("aura.chat.message");
        message.setMessageText(getText());

        GorillaClient.getInstance().putAtomSharedWith(remoteIdentity.getUserUUID().toString(), message.getAtom());
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
