package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Generic Stream Item: the topmost container for every kind of data items that might
 * be displayed within the UI stream component.
 */
public class GenericStreamItem extends AbstractStreamItem implements StreamItemInterface {

    /**
     * Construct generic stream item.
     *
     * @param ownerUser
     * @param title
     * @param text
     */
    public GenericStreamItem(@NonNull User ownerUser, @NonNull String title, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_GENERIC, title, text, R.drawable.ic_blur_on_black_24dp);
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
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }

    @Override
    public void onFullyViewed() {
        // TODO: Implement!
        // e.g.: Write as event to PMAI!
    }
}
