package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.univid.Identity;

/**
 * Stream item base class.
 */
public abstract class StreamItem implements StreamItemInterface {

    protected Identity ownerIdentity;
    protected String title;
    protected String text;
    protected Integer imageId;
    protected ItemType type;
    protected Float absoluteScore = 1.0f;

    /**
     * Construct stream item of invocationType "unknown".
     */
    public StreamItem(ItemType itemType) {
        setType(itemType);
    }

    /**
     * Construct stream item with or without owner identity.
     *
     * @param ownerIdentity
     * @param itemType
     * @param title
     * @param text
     * @param imageId
     */
    public StreamItem(Identity ownerIdentity, @NonNull ItemType itemType, @NonNull String title, @NonNull String text, @NonNull Integer imageId) {
        setOwnerIdentity(ownerIdentity);
        setType(itemType);
        setTitle(title);
        setText(text);
        setImageId(imageId);
    }

    @Override
    public Identity getOwnerIdentity() {
        return ownerIdentity;
    }

    @Override
    public void setOwnerIdentity(Identity ownerIdentity) {
        this.ownerIdentity = ownerIdentity;
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
    public ItemType getType() {
        return type;
    }

    @Override
    public void setType(ItemType type) {
        this.type = type;
    }

    @Override
    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    @Override
    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }
}
