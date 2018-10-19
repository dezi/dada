package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

/**
 * Contact item
 * TODO: Implement peristable/sharable interfaces!
 */
public class ContactStreamItem extends StreamItem {

    protected Identity contactIdentity;
    protected boolean isOwnerIdentity;

    public ContactStreamItem(@NonNull Identity ownerIdentity, @NonNull Identity contactIdentity) {
        super(ownerIdentity, ItemType.TYPE_STREAMITEM_CONTACT, contactIdentity.getNick(),contactIdentity.getFull(), R.drawable.ic_person_black_24dp);
        setContactIdentity(contactIdentity);
    }

    public Identity getContactIdentity() {
        return contactIdentity;
    }

    public void setContactIdentity(Identity contactIdentity) {
        this.contactIdentity = contactIdentity;
    }

    public boolean isOwnerIdentity() {
        return contactIdentity.equals(ownerIdentity);
    }
}
