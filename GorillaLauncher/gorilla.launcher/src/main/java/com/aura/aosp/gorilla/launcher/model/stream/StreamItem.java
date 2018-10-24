package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Stream item base class.
 */
public abstract class StreamItem implements StreamItemInterface {

    private static final int DEFAULT_MAX_EXCERPT_LENGTH = 42;

    protected User ownerUser;
    protected String title;
    protected String text;
    protected String textExcerpt;
    protected Integer imageId;
    protected ItemType type;
    protected Float absoluteScore = 1.0f;
    protected Long timeCreated;
    protected Long timeModified;
    protected String uuid;

    /**
     * Construct stream item of invocationType "unknown".
     */
    public StreamItem(ItemType itemType) {
        setType(itemType);
        setUuid(UID.randomUUIDBase64());
        setCurrentTime();
    }

    /**
     * Construct stream item of invocationType "unknown".
     */
    public StreamItem(@NonNull User ownerUser, ItemType itemType) {
        setOwnerUser(ownerUser);
        setType(itemType);
        setUuid(UID.randomUUIDBase64());
        setCurrentTime();
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
    public StreamItem(@NonNull User ownerUser, @NonNull ItemType itemType, @Nullable String title, @NonNull String text, @NonNull Integer imageId) {
        setOwnerUser(ownerUser);
        setType(itemType);
        setTitle(title);
        setText(text);
        setTextExcerpt(extractExcerpt(text));
        setImageId(imageId);
        setUuid(UID.randomUUIDBase64());
        setCurrentTime();
    }


    /**
     * Set created and modified timestamps to current system time
     */
    protected void setCurrentTime() {
        Long currentDateTime = System.currentTimeMillis();

        setTimeCreated(currentDateTime);
        setTimeModified(currentDateTime);
    }

    /**
     * TODO: Extract to util class
     *
     * @param text
     * @return
     */
    private static String extractExcerpt(String text) {

        if (text.length() <= DEFAULT_MAX_EXCERPT_LENGTH) {
            return text;
        }

        String tryTitle = "";
        String useTitle = null;

        if (text.contains("\n")) {

            tryTitle = text.substring(0, text.indexOf("\n"));

            if (tryTitle.length() <= DEFAULT_MAX_EXCERPT_LENGTH) {
                useTitle = tryTitle;
            }

        } else if (text.contains(" ")) {

            int startPos = 0;

            while (true) {

                int endPos = text.indexOf(" ", startPos);

                if (endPos >= 0 && startPos < DEFAULT_MAX_EXCERPT_LENGTH - 4) {
                    tryTitle += text.substring(startPos, endPos) + " ";
                    startPos = endPos + 1;
                    continue;
                }

                useTitle = tryTitle.trim() + " ...";

                break;
            }
        }

        if (useTitle == null) {
            useTitle = text.substring(0, DEFAULT_MAX_EXCERPT_LENGTH - 4) + " ...";
        }

        return useTitle;
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
    public void setTitle(@Nullable String title) {
        this.title = title != null ? title : "";
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
    public String getTextExcerpt() {
        return textExcerpt;
    }

    @Override
    public void setTextExcerpt(String textExcerpt) {
        this.textExcerpt = textExcerpt;
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
    public Long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public Long getTimeModified() {
        return timeModified;
    }

    @Override
    public void setTimeModified(Long timeModified) {
        this.timeModified = timeModified;
    }

    @Override
    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
