package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

/**
 *  Contact item
 */
public class StreamItemContact extends StreamItem {

    public Identity contactIdentity;

    public StreamItemContact(Identity identity) {
        super(ItemType.TYPE_STREAMITEM_CONTACT, identity.getNick(), identity.getFull(), R.drawable.ic_account_circle_black_24dp);
        setContactIdentity(identity);
    }

    public Identity getContactIdentity() {
        return contactIdentity;
    }

    public void setContactIdentity(Identity contactIdentity) {
        this.contactIdentity = contactIdentity;
    }
}
