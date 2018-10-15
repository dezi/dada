package com.aura.aosp.gorilla.launcher.model;

import com.aura.aosp.aura.common.univid.Identity;

/**
 * Generic Stream Item: the topmost container for every kind of data items that might
 * be displayed within the UI stream component.
 */
public class StreamItem {

    protected Identity ownerIdentity;
    protected String title;
    protected String text;
    protected Integer imageId;
    protected ItemType type;

    // TODO: Create rule for action domains, context, subactions: com.aura.notes...
    public static enum ItemType {
        TYPE_STREAMITEM_GENERIC,
        TYPE_STREAMITEM_CONTACT,
        TYPE_STREAMITEM_NOTE,
        TYPE_STREAMUTEN_MESSAGE,
        TYPE_STREAMUTEN_HIGHLIGHT,
        TYPE_STREAMITEM_INVISIBLE
    };

    public StreamItem(ItemType type, String title, String text, int imageId) {
        setType(type);
        setTitle(title);
        setText(text);
        setImageId(imageId);
    }

    public StreamItem(ItemType type, String text, int imageId) {
        setType(type);
        setText(text);
        setImageId(imageId);
    }

    public Identity getOwnerIdentity() {
        return ownerIdentity;
    }

    public void setOwnerIdentity(Identity ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
