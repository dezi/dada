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
public class DraftStreamItem extends StreamItem implements GorillaPersistable, StreamItemInterface {

    /**
     * Note item construction
     *
     * @param ownerUser
     * @param text
     */
    public DraftStreamItem(@NonNull User ownerUser, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_DRAFT, null, text, R.drawable.ic_note_black_24dp);
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
