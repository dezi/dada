package com.aura.aosp.gorilla.launcher.model.stream;

import android.view.View;

public interface StreamItemLifecycleListener {

    /**
     * This method gets called if the preview (e.g. text excerpt) of
     * this item has been visually exposed to the device user.
     */
    void onPreviewViewed();

    /**
     * This method gets called if the full content of this
     * item has been visually exposed to the device user.
     */
    void onFullyViewed();
}
