package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * The stream item interface.
 */
public interface StreamItemInterface {

    // TODO: Create rule for action domains, context, subactions: com.aura.notes...
    public static enum ItemType {
        TYPE_STREAMITEM_IMAGE,
        TYPE_STREAMITEM_DRAFT,
        TYPE_STREAMITEM_MESSAGE,
        TYPE_STREAMITEM_CONTACT,
        TYPE_STREAMITEM_INVISIBLE
    }

    ItemType getType();

    User getMyUser();

    User getOwnerUser();

    String getTitle();

    String getText();

    String getTextExcerpt();

    /**
     * Get the image drawablewhich is added the item stream.
     */
    @Nullable
    Integer getImageId();

    /**
     * Get the placeholder drawable image which is added to
     * the item stream if no specific image id is provided.
     */
    Integer getImagePlaceholderId();

    Long getTimeCreated();

    Long getTimeModified();

    Float getAbsoluteScore();

    String getUuid();

    boolean isMyItem();

    boolean shareIsQueued();

    boolean shareIsSent();

    boolean shareIsPersisted();

    boolean shareIsReceived();

    boolean shareIsRead();

    boolean isFullyViewed();

    boolean isPreviewViewed();

    /**
     * This method gets called if the preview (e.g. text excerpt) of
     * this item has been visually exposed to the device user.
     */
    void onPreviewViewed();

    /**
     * This method gets called if the full content of this
     * item has been visually exposed to the device user.
     */
    void onFullyViewed();
}
