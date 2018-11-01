package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Invisible item (hacky!)
 */
public class InvisibleStreamItem extends AbstractStreamItem implements StreamItemInterface {

    public InvisibleStreamItem(@NonNull User myUser, @NonNull User ownerUser) {
        super(ItemType.TYPE_STREAMITEM_INVISIBLE);
        setImagePlaceholderId(R.drawable.vector_oval_transparent_24dp);
    }

    @Nullable
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

    }

    @Override
    public void onFullyViewed() {

    }
}
