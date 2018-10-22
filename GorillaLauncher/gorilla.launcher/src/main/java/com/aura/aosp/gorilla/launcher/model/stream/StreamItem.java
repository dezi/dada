package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Stream item base class.
 */
public abstract class StreamItem implements StreamItemInterface {

    protected User ownerUser;
    protected String title;
    protected String text;
    protected Integer imageId;
    protected ItemType type;
    protected Float absoluteScore = 1.0f;
    protected Long createTime;
    protected Long modifyTime;

    /**
     * Construct stream item of invocationType "unknown".
     */
    public StreamItem(ItemType itemType) {
        setType(itemType);
    }

    /**
     * Construct stream item with or without owner user.
     *
     * @param ownerUser
     * @param itemType
     * @param title
     * @param text
     * @param imageId
     */
    public StreamItem(User ownerUser, @NonNull ItemType itemType, @NonNull String title, @NonNull String text, @NonNull Integer imageId) {
        setOwnerUser(ownerUser);
        setType(itemType);
        setTitle(title);
        setText(text);
        setImageId(imageId);

        Long currentDateTime = System.currentTimeMillis();

        setCreateTime(currentDateTime);
        setModifyTime(currentDateTime);
    }

    @Override
    public void setType(ItemType type) {
        this.type = type;
    }

    @Override
    public ItemType getType() {
        return type;
    }

    @Override
    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    @Override
    public User getOwnerUser() {
        return ownerUser;
    }

    @Override
    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Integer getImageId() {
        return imageId;
    }

    @Override
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public Long getModifyTime() {
        return modifyTime;
    }

    @Override
    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }
}
