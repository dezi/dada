package com.aura.aosp.gorilla.launcher.model;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

/**
 * Contact item
 * TODO: Implement peristable/sharable interfaces!
 */
public class StreamItemContact extends StreamItem {

    public Identity contactIdentity;

    public StreamItemContact(@NonNull Identity ownerIdentity, @NonNull Identity contactIdentity) {
        setOwnerIdentity(ownerIdentity);
        setContactIdentity(contactIdentity);
        setType(ItemType.TYPE_STREAMITEM_CONTACT);
        setImageId(R.drawable.ic_account_circle_black_24dp);
        setTitle(contactIdentity.getNick());
        setText(contactIdentity.getFull());
    }

    public Identity getContactIdentity() {
        return contactIdentity;
    }

    public void setContactIdentity(Identity contactIdentity) {
        this.contactIdentity = contactIdentity;
    }
}
