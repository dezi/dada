package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.gorilla.launcher.R;

/**
 * The ContactStreamItem model is the topmost container for every kind of data items that might
 * be displayed within the main user content streams.
 */
public class InvisibleStreamItem extends StreamItem {

    public InvisibleStreamItem() {
        super(ItemType.TYPE_STREAMITEM_INVISIBLE, "", R.drawable.contentstream_oval_transparent_24dp);
    }
}
