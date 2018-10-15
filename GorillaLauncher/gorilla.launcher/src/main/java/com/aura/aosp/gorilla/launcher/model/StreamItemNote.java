package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.gorilla.atoms.GorillaAtom;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;

/**
 * Note item
 */
public class StreamItemNote extends StreamItem implements GorillaPersistable {

    public StreamItemNote() {
        super(ItemType.TYPE_STREAMITEM_NOTE, null, null, R.drawable.ic_note_black_24dp);
    }

    public StreamItemNote(String text) {
        super(ItemType.TYPE_STREAMITEM_NOTE, extractTitle(text), text, R.drawable.ic_note_black_24dp);
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

    @Override
    public void readFromGorillaAtom(GorillaAtom atom) {
        GorillaMessage message = (GorillaMessage) atom;
        setTitle(extractTitle(message.getMessageText()));
        setText(message.getMessageText());
    }
}
