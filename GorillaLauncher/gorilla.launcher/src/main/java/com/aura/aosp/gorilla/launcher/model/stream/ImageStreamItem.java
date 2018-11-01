package com.aura.aosp.gorilla.launcher.model.stream;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Image stream item
 */
public class ImageStreamItem extends AbstractStreamItem implements StreamItemInterface {

    Integer imageId;

    public ImageStreamItem(@NonNull Integer imageId) {
        super(ItemType.TYPE_STREAMITEM_IMAGE);
        setImageId(imageId);
        setImagePlaceholderId(R.drawable.shape_stream_item_circle);
    }

    public ImageStreamItem(@NonNull User myUser, @NonNull Integer imageId) {
        super(myUser, myUser, ItemType.TYPE_STREAMITEM_IMAGE);
        setImageId(imageId);
        setImagePlaceholderId(R.drawable.shape_stream_item_circle);
    }

    @Nullable
    @Override
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    @Override
    public boolean isFullyViewed() {
        return false;
    }

    @Override
    public boolean isPreviewViewed() {
        return false;
    }

    @Override
    public void onPreviewViewed() {
        return;
    }

    @Override
    public void onFullyViewed() {
        return;
    }
}
