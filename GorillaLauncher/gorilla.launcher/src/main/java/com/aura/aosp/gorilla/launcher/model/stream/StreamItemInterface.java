package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * The stream item interface.
 */
public interface StreamItemInterface extends StreamItemLifecycleListener {

    public static enum ItemType {
        ITEMTYPE_IMAGE,
        ITEMTYPE_DRAFT,
        ITEMTYPE_MESSAGE,
        ITEMTYPE_CONTACT
    }

    public static enum ItemDisplayState {
        DSTATE_DOT,
        DSTATE_CIRCLE,
        DSTATE_PREVIEW,
        DSTATE_EXPANDED,
        DSTATE_FULL
    }

    ItemType getType();

    void setType(ItemType itemType);

    ItemDisplayState getDisplayState();

    void setDisplayState(ItemDisplayState displayState);

    User getMyUser();

    User getOwnerUser();

    String getTitle();

    String getText();

    String getTextExcerpt();

    @Nullable
    String getImageCaption();

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
}
