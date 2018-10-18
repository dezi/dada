package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAvatarImage extends RelativeLayout {

    protected CircleImageView circleImageView;
    protected TextView textView;

    protected Identity identity;

    public UserAvatarImage(Context context, Identity identity) {
        super(context);
        this.setIdentity(identity);
        init();
    }

    public UserAvatarImage(Context context) {
        super(context);
    }

    public UserAvatarImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UserAvatarImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UserAvatarImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void init() {
        inflate(getContext(), R.layout.fragment_avatar_image, this);

        circleImageView = findViewById(R.id.circle_image);
        textView = findViewById(R.id.text_overlay);

        circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.stream_oval_transparent_24dp, getContext().getTheme()));
        circleImageView.setCircleBackgroundColor(getContext().getColor(R.color.color_circleimage_background));

        textView.setText(identity.getNick());
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
