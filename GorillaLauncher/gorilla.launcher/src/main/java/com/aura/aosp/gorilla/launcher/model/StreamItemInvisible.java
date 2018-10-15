package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Invisible item (hacky!)
 */
public class StreamItemInvisible extends StreamItem {

    public StreamItemInvisible() {
        super(ItemType.TYPE_STREAMITEM_INVISIBLE, "", R.drawable.stream_oval_transparent_24dp);
    }
}
