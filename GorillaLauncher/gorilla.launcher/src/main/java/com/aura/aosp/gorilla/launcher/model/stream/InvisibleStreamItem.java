package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Invisible item (hacky!)
 */
public class InvisibleStreamItem extends StreamItem implements StreamItemInterface {

    public InvisibleStreamItem() {
        super(ItemType.TYPE_STREAMITEM_INVISIBLE);
        setImageId(R.drawable.stream_oval_transparent_24dp);
    }
}
