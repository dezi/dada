package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.model.GorillaPersistable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * TODO: Refactor interface and implementations to fit API goals!
 */
public interface StreamItemInterface {

    // TODO: Create rule for action domains, context, subactions: com.aura.notes...
    public static enum ItemType {
        TYPE_STREAMITEM_GENERIC,
        TYPE_STREAMITEM_CONTACT,
        TYPE_STREAMITEM_DRAFT,
        TYPE_STREAMITEM_MESSAGE,
        TYPE_STREAMITEM_INVISIBLE
    }

    ItemType getType();

    User getOwnerUser();

    String getTitle();

    String getText();

    String getTextExcerpt();

    @Nullable
    Integer getImageId();

    Integer getImagePlaceholderId();

    Long getTimeCreated();

    Long getTimeModified();

    Float getAbsoluteScore();

    String getUuid();

    void onPreviewViewed(User viewedByUser);

    void onFullyViewed(User viewedByUser);
}
