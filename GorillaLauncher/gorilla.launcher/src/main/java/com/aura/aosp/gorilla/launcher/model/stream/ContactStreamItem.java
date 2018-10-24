package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Contact item
 * TODO: Implement peristable/sharable interfaces!
 */
public class ContactStreamItem extends StreamItem implements StreamItemInterface {

    protected User contactUser;
    protected boolean isOwnerIdentity;

    public ContactStreamItem(@NonNull User ownerUser, @NonNull User contactUser) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_CONTACT, contactUser.getIdentity().getNick(), contactUser.getIdentity().getFull(), R.drawable.ic_person_black_24dp);
        setContactUser(contactUser);
    }

    public User getContactUser() {
        return contactUser;
    }

    public void setContactUser(User contactUser) {
        this.contactUser = contactUser;
    }

    public boolean isOwnerUser() {
        return contactUser.getIdentity().equals(ownerUser.getIdentity());
    }

    @Override
    public void onPreviewViewed(User viewedByUser) {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }

    @Override
    public void onFullyViewed(User viewedByUser) {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }
}
