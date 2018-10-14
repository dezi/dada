package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

/**
 *  The ContactStreamItem
 */
public class ContactStreamItem extends StreamItem {

    public Identity identity;

    public ContactStreamItem(Identity identity) {
        super(ItemType.TYPE_STREAMITEM_CONTACT, identity.getNick(), identity.getFull(), R.drawable.ic_account_circle_black_24dp);
        setIdentity(identity);
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
