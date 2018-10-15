package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.univid.Identity;

interface StreamItemInterface {

    // TODO: Create rule for action domains, context, subactions: com.aura.notes...
    public static enum ItemType {
        TYPE_STREAMITEM_UNKNOWN,
        TYPE_STREAMITEM_GENERIC,
        TYPE_STREAMITEM_CONTACT,
        TYPE_STREAMITEM_NOTE,
        TYPE_STREAMUTEN_MESSAGE,
        TYPE_STREAMUTEN_HIGHLIGHT,
        TYPE_STREAMITEM_INVISIBLE
    }

    Identity getOwnerIdentity();

    void setOwnerIdentity(Identity ownerIdentity);

    String getTitle();

    void setTitle(String title);

    String getText();

    void setText(String text);

    Integer getImageId();

    void setImageId(Integer imageId);

    ItemType getType();

    void setType(ItemType type);
}
