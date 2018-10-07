package com.aura.aosp.gorilla.launcher.model;

/**
 * The TimelineItem model is the topmost container for every kind of data items that might
 * be displayed within the main user content/timeline streams.
 */
public class TimelineItem extends AbstractStreamDataItem {

    public String title;
    public String description;
    public int imageId;

    public TimelineItem(String type, String title, String description, int imageId) {
        setType(type);
        setTitle(title);
        setDescription(description);
        setImageId(imageId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
