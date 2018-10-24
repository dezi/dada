package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.gorilla.launcher.model.GorillaPersistable;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * TODO: Refactor interface and implementations to fit API goals!
 */
interface StreamItemInterface {

    // TODO: Create rule for action domains, context, subactions: com.aura.notes...
    public static enum ItemType {
        TYPE_STREAMITEM_GENERIC,
        TYPE_STREAMITEM_CONTACT,
        TYPE_STREAMITEM_DRAFT,
        TYPE_STREAMITEM_MESSAGE,
        TYPE_STREAMITEM_INVISIBLE
    }

    ItemType getType();

    void setType(ItemType type);

    User getOwnerUser();

    void setOwnerUser(User ownerUser);

    String getTitle();

    void setTitle(String title);

    String getText();

    void setText(String text);

    String getTextExcerpt();

    void setTextExcerpt(String textExcerpt);

    Integer getImageId();

    void setImageId(Integer imageId);

    Long getTimeCreated();

    void setTimeCreated(Long timeCreated);

    Long getTimeModified();

    void setTimeModified(Long timeModified);

    Float getAbsoluteScore();

    void setAbsoluteScore(Float absoluteScore);

    String getUuid();

    void setUuid(String uuid);

    void onPreviewViewed(User viewedByUser);

    void onFullyViewed(User viewedByUser);
}
