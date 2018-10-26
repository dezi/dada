package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Contact item
 * TODO: Implement peristable/sharable interfaces!
 */
public class ContactStreamItem extends AbstractStreamItem implements StreamItemInterface {

    protected User contactUser;

    public ContactStreamItem(@NonNull User myUser, @NonNull User ownerUser, @NonNull User contactUser) {
        super(myUser, ownerUser, ItemType.TYPE_STREAMITEM_CONTACT, contactUser.getIdentity().getNick(), contactUser.getIdentity().getFull(), R.drawable.ic_person_black_24dp);
        setContactUser(contactUser);
    }

    public User getContactUser() {
        return contactUser;
    }

    public void setContactUser(User contactUser) {
        this.contactUser = contactUser;
    }

    public boolean isMyIdentity() {
        return contactUser.getIdentity().getUserUUIDBase64().equals(myUser.getIdentity().getUserUUIDBase64());
    }

    @Override
    public Integer getImageId() {
        return getContactUser().getContactAvatarImageRes();
    }

    @Override
    public boolean isFullyViewed() {
        return true;
    }

    @Override
    public boolean isPreviewViewed() {
        return true;
    }

    @Override
    public void onPreviewViewed() {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }

    @Override
    public void onFullyViewed() {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }
}
