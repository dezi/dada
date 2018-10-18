package com.aura.aosp.gorilla.launcher.model.stream;

/**
 * Invisible item (hacky!)
 */
public class InvisibleStreamItem extends StreamItem {

    public InvisibleStreamItem() {
        super();
        setType(ItemType.TYPE_STREAMITEM_INVISIBLE);
    }
}
