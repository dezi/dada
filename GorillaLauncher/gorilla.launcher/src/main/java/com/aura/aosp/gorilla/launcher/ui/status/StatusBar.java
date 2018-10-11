package com.aura.aosp.gorilla.launcher.ui.status;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Main Launcher Status Bar visualizing basic connectivity information
 */
public class StatusBar extends LinearLayout {

    protected String profileInfo = "...";

    protected boolean svLink = false;
    protected boolean uplink = false;

    protected TextView profileInfoView;
    protected ImageView statusImageSystem;
    protected ImageView statusImageOnline;

    public StatusBar(Context context) {
        super(context);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatusBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Update status information according to current state
     */
    public void update() {

        int useColorSystem;
        int useColorOnline;

        statusImageSystem = findViewById(R.id.statusImageSystem);

        if (isSvLink()) {
            useColorSystem = R.color.color_statusbar_statusimage_active;
        } else {
            useColorSystem = R.color.color_statusbar_statusimage_inactive;
        }

        Drawable imageSystemDrawable = statusImageSystem.getDrawable();
        DrawableCompat.setTint(imageSystemDrawable, ContextCompat.getColor(getContext(), useColorSystem));

        statusImageOnline = findViewById(R.id.statusImageOnline);

        if (isUplink()) {
            useColorOnline = R.color.color_statusbar_statusimage_active;
        } else {
            useColorOnline = R.color.color_statusbar_statusimage_inactive;
        }

        Drawable imageOnlineDrawable = statusImageOnline.getDrawable();
        DrawableCompat.setTint(imageOnlineDrawable, ContextCompat.getColor(getContext(), useColorOnline));

        profileInfoView = findViewById(R.id.statusProfileInfo);
        profileInfoView.setText(getProfileInfo());
    }

    public String getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(String profileInfo) {
        this.profileInfo = profileInfo;
        this.update();
    }

    public boolean isSvLink() {
        return svLink;
    }

    public void setSvLink(boolean svLink) {
        this.svLink = svLink;
        this.update();
    }

    public boolean isUplink() {
        return uplink;
    }

    public void setUplink(boolean uplink) {
        this.uplink = uplink;
        this.update();
    }
}
