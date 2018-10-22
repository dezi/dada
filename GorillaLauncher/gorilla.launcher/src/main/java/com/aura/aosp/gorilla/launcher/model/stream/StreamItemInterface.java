package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.gorilla.launcher.model.user.User;

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

    Integer getImageId();

    void setImageId(Integer imageId);

    Long getCreateTime();

    void setCreateTime(Long createTime);

    Long getModifyTime();

    void setModifyTime(Long modifyTime);

    Float getAbsoluteScore();

    void setAbsoluteScore(Float absoluteScore);
}
