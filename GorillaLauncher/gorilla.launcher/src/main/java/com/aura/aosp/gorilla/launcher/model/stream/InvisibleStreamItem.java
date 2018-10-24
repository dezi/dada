package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Invisible item (hacky!)
 */
public class InvisibleStreamItem extends StreamItem implements StreamItemInterface {

    public InvisibleStreamItem() {
        super(ItemType.TYPE_STREAMITEM_INVISIBLE);
        setImageId(R.drawable.stream_oval_transparent_24dp);
    }

    @Override
    public void onPreviewViewed(User viewedByUser) {
        return;
    }

    @Override
    public void onFullyViewed(User viewedByUser) {
        return;
    }
}
