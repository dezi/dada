package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Invisible item (hacky!)
 */
public class InvisibleStreamItem extends AbstractStreamItem implements StreamItemInterface {

    public InvisibleStreamItem(@NonNull User ownerUser) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_INVISIBLE);
        setImagePlaceholderId(R.drawable.stream_oval_transparent_24dp);
    }

    @Override
    public Integer getImageId() {
        return null;
    }

    @Override
    public boolean isFullyViewed() {
        return false;
    }

    @Override
    public boolean isPreviewViewed() {
        return false;
    }

    @Override
    public void onPreviewViewed() {
        return;
    }

    @Override
    public void onFullyViewed() {
        return;
    }
}
