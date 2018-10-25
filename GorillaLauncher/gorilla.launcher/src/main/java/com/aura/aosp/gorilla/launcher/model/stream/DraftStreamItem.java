package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.gorilla.atoms.GorillaAtom;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.GorillaPersistable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Note item
 */
public class DraftStreamItem extends AbstractStreamItem implements GorillaPersistable, StreamItemInterface {

    /**
     * Note item construction
     *
     * @param ownerUser
     * @param text
     */
    public DraftStreamItem(@NonNull User ownerUser, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_DRAFT, null, text, R.drawable.ic_note_black_24dp);
    }

    /**
     * Create item from gorilla atom.
     *
     * @param gorillaMessage
     */
    public DraftStreamItem(@NonNull User ownerUser, @NonNull GorillaMessage gorillaMessage) {

        super(ownerUser, ItemType.TYPE_STREAMITEM_MESSAGE, ownerUser.getIdentity().getNick(), gorillaMessage.getMessageText(), R.drawable.ic_note_black_24dp);

        setTitle(ownerUser.getIdentity().getNick());
        setImagePlaceholderId(R.drawable.ic_note_black_24dp);
        setTimeCreated(gorillaMessage.getTime());
    }

    @Override
    public GorillaAtom persist() {

        GorillaMessage noteMessage = new GorillaMessage();

        noteMessage.setUUID(UID.randomUUIDBase64());
        noteMessage.setTime(System.currentTimeMillis());
        noteMessage.setType("aura.note");
        noteMessage.setMessageText(getText());

        GorillaClient.getInstance().putAtom(noteMessage.getAtom());

        return noteMessage;
    }

    @Override
    public Integer getImageId() {
        return null;
    }

    @Override
    public boolean isFullyViewed() {
        return false;
    }

    @Override
    public boolean isPreviewViewed() {
        return false;
    }

    @Override
    public void onPreviewViewed() {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }

    @Override
    public void onFullyViewed() {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }
}
