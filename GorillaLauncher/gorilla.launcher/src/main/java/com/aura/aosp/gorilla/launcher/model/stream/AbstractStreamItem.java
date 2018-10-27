package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Stream item base class.
 */
public abstract class AbstractStreamItem implements StreamItemInterface {

    private static final int DEFAULT_MAX_EXCERPT_LENGTH = 38;

    protected User myUser;
    protected User ownerUser;
    protected String title;
    protected String text;
    protected String textExcerpt;
    protected Integer imagePlaceholderId;
    protected ItemType type;
    protected Float absoluteScore = 1.0f;
    protected Long timeCreated;
    protected Long timeModified;
    protected String uuid;

    /**
     * Construct stream item of invocationType "unknown".
     */
    AbstractStreamItem(@NonNull User myUser, @NonNull User ownerUser, ItemType itemType) {
        setMyUser(myUser);
        setOwnerUser(ownerUser);
        setType(itemType);
        setUuid(UID.randomUUIDBase64());
        setCurrentTime();
    }

    /**
     * Construct stream item with or without owner user.
     *
     * @param myUser
     * @param ownerUser
     * @param itemType
     * @param title
     * @param text
     * @param imagePlaceholderId
     */
    AbstractStreamItem(@NonNull User myUser, @NonNull User ownerUser, @NonNull ItemType itemType, @Nullable String title, @NonNull String text, @NonNull Integer imagePlaceholderId) {
        setMyUser(myUser);
        setOwnerUser(ownerUser);
        setType(itemType);
        setTitle(title);
        setText(text);
        setTextExcerpt(extractExcerpt(text));
        setImagePlaceholderId(imagePlaceholderId);
        setUuid(UID.randomUUIDBase64());
        setCurrentTime();
    }

    /**
     * Set created and modified timestamps to current system time
     */
    private void setCurrentTime() {
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
    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }

    @Override
    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    @Override
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    @Override
    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        this.title = title != null ? title : "";
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getTextExcerpt() {
        return textExcerpt;
    }

    public void setTextExcerpt(String textExcerpt) {
        this.textExcerpt = textExcerpt;
    }

    @Override
    public Integer getImagePlaceholderId() {
        return imagePlaceholderId;
    }

    public void setImagePlaceholderId(Integer imagePlaceholderId) {
        this.imagePlaceholderId = imagePlaceholderId;
    }

    @Override
    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public Long getTimeModified() {
        return timeModified;
    }

    public void setTimeModified(Long timeModified) {
        this.timeModified = timeModified;
    }

    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }

    @Override
    public String getUuid() {
        return uuid;
    }


    /**
     * Check if item is owned by current user identity.
     *
     * @return
     */
    @Override
    public boolean isMyItem() {
        return getMyUser().getIdentity().getUserUUIDBase64().equals(getOwnerUser().getIdentity().getUserUUIDBase64());
    }

    @Override
    public boolean shareIsQueued() {
        return false;
    }

    @Override
    public boolean shareIsSent() {
        return false;
    }

    @Override
    public boolean shareIsPersisted() {
        return false;
    }

    @Override
    public boolean shareIsReceived() {
        return false;
    }

    @Override
    public boolean shareIsRead() {
        return false;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
