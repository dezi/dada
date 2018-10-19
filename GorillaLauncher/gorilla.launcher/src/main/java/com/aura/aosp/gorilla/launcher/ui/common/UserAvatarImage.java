package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.user.User;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * User avatar image view (round shape).
 */
public class UserAvatarImage extends RelativeLayout {

    protected CircleImageView circleImageView;
    protected TextView textView;

    protected User user;

    public UserAvatarImage(Context context, User user) {
        super(context);
        this.setUser(user);
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

        circleImageView.setCircleBackgroundColor(getContext().getColor(R.color.color_circleimage_background));

        final Integer avatarImageRes = getUser().getContactAvatarImageRes();

        if (avatarImageRes != null) {
            circleImageView.setImageResource(avatarImageRes);
        } else {
            circleImageView.setImageDrawable(getResources().getDrawable(R.drawable.stream_oval_transparent_24dp, getContext().getTheme()));
            textView.setText(user.getInitials());
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
