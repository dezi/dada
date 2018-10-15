package com.aura.aosp.gorilla.launcher.model;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaAtom;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;

/**
 * Note item
 */
public class StreamItemNote extends StreamItem implements GorillaPersistable {

    /**
     * Note item construction
     *
     * @param ownerIdentity
     * @param text
     */
    public StreamItemNote(@NonNull Identity ownerIdentity, @NonNull String text) {
        super(ownerIdentity, ItemType.TYPE_STREAMITEM_NOTE, extractTitle(text), text, R.drawable.ic_note_black_24dp);
    }

    /**
     * Note item construction from Gorilla atom.
     *
     * @param atom
     */
    public StreamItemNote(GorillaAtom atom) {
        this(Contacts.getContact(atom.getUUIDBase64()), ((GorillaMessage) atom).getMessageText());
    }

    /**
     * TODO: Extract to util class
     *
     * @param text
     * @return
     */
    private static String extractTitle(String text) {

        String tryTitle = null;
        String useTitle = null;

        if (text.contains("\n")) {
            tryTitle = text.substring(0, text.indexOf("\n") - 1);
            if (tryTitle.length() < 32) {
                useTitle = tryTitle;
            }
        } else if (text.contains(" ")) {
            // TODO: Get some more words up to a max of 32 chars
            tryTitle = text.substring(0, text.indexOf(" ") - 1);
            if (tryTitle.length() < 32) {
                useTitle = tryTitle;
            }
        }

        if (useTitle == null) {
            if (text.length() > 32) {
                useTitle = text.substring(0, 31);
            } else {
                useTitle = text;
            }
        }

        return useTitle;
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
}
