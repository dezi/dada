package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.gorilla.atoms.GorillaAtom;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.GorillaPersistable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Note item
 */
public class DraftStreamItem extends StreamItem implements GorillaPersistable {

    /**
     * Note item construction
     *
     * @param ownerUser
     * @param text
     */
    public DraftStreamItem(@NonNull User ownerUser, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_DRAFT, extractTitle(text), text, R.drawable.ic_note_black_24dp);
    }

//    /**
//     * Note item construction from Gorilla atom.
//     *
//     * @param atom
//     */
//    public DraftStreamItem(GorillaAtom atom) {
//
//        String text = ((GorillaMessage) atom).getMessageText();
//        User atomOwnerUser = new User(Contacts.getContact(atom.getUUIDBase64()));
//
//        if (text == null) {
//            text = "";
//        }
//
//        setOwnerUser(atomOwnerUser);
//        setType(ItemType.TYPE_STREAMITEM_DRAFT);
//        setTitle(extractTitle(text));
//        setText(text);
//        setImageId(R.drawable.ic_note_black_24dp);
//    }

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
