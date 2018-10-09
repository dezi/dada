package com.aura.aosp.gorilla.launcher.model;

/**
 * The ContentStreamItem model is the topmost container for every kind of data items that might
 * be displayed within the main user content streams.
 */
public class ContentStreamItem extends AbstractStreamDataItem {

    public String title;
    public String text;
    public int imageId;

    public ContentStreamItem(String type, String title, String text, int imageId) {
        setType(type);
        setTitle(title);
        setText(text);
        setImageId(imageId);
    }

    public ContentStreamItem(String type, String text, int imageId) {
        setType(type);
        setTitle(title);
        setText(text);
        setImageId(imageId);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
