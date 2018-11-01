package com.aura.aosp.gorilla.launcher.model.stream;

import android.support.annotation.NonNull;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

/**
 * Boundary image stream item (stream start and end items)
 */
public class BoundaryStreamItem extends ImageStreamItem implements StreamItemInterface {

    public BoundaryStreamItem() {
        super(R.drawable.ic_star_white_24dp);
    }
}
