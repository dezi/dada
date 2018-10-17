package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

/**
 * Generic Stream Item: the topmost container for every kind of data items that might
 * be displayed within the UI stream component.
 */
public class GenericStreamItem extends StreamItem {

    /**
     * Construct generic stream item.
     *
     * @param ownerIdentity
     * @param title
     * @param text
     */
    public GenericStreamItem(Identity ownerIdentity, @NonNull String title, @NonNull String text) {
        super(ownerIdentity, ItemType.TYPE_STREAMITEM_GENERIC, title, text, R.drawable.ic_public_black_24dp);
    }
}
