package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Generic Stream Item: the topmost container for every kind of data items that might
 * be displayed within the UI stream component.
 */
public class GenericStreamItem extends StreamItem implements StreamItemInterface {

    /**
     * Construct generic stream item.
     *
     * @param ownerUser
     * @param title
     * @param text
     */
    public GenericStreamItem(User ownerUser, @NonNull String title, @NonNull String text) {
        super(ownerUser, ItemType.TYPE_STREAMITEM_GENERIC, title, text, R.drawable.ic_blur_on_black_24dp);
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
