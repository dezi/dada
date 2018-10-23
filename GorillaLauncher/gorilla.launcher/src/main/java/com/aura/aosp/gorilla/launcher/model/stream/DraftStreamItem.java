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
public class DraftStreamItem extends StreamItem implements GorillaPersistable {

    private static final int MAX_EXTRACT_LENGTH = 42;

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
//     * Create item from gorilla message atom.
//     *
//     * @param gorillaAtom
//     */
//    public DraftStreamItem(GorillaAtom gorillaAtom) {
//        super(new User(Contacts.getContact(gorillaAtom.getUUIDBase64())), gorillaAtom.getLoad().get("text"));
//        setCreateTime(gorillaAtom.getTime());
//    }

    /**
     * TODO: Extract to util class
     *
     * @param text
     * @return
     */
    private static String extractTitle(String text) {

        if (text.length() <= MAX_EXTRACT_LENGTH) {
            return text;
        }

        String tryTitle = "";
        String useTitle = null;

        if (text.contains("\n")) {

            tryTitle = text.substring(0, text.indexOf("\n"));

            if (tryTitle.length() <= MAX_EXTRACT_LENGTH) {
                useTitle = tryTitle;
            }

        } else if (text.contains(" ")) {

            int startPos = 0;

            while (true) {

                int endPos = text.indexOf(" ", startPos);

                if (endPos >= 0 && startPos < MAX_EXTRACT_LENGTH - 2) {
                    tryTitle += text.substring(startPos, endPos) + " ";
                    startPos = endPos + 1;
                    continue;
                }

                useTitle = tryTitle.trim();

                break;
            }
        }

        if (useTitle == null) {
            useTitle = text.substring(0, MAX_EXTRACT_LENGTH - 3) + "...";
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
