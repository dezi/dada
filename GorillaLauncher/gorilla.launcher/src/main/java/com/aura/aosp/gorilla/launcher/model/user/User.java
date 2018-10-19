package com.aura.aosp.gorilla.launcher.model.user;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;

import java.lang.reflect.Field;

/**
 * User class built around "Identity"
 */
public class User {

    protected Identity identity;

    /**
     * Construct user by passing identity.
     *
     * @param identity
     */
    public User(Identity identity) {
        this.identity = identity;
    }

    /**
     * Get avatar image resource for contact user identity
     * TODO: Very hacky - remove and improve/extend Contact class!
     */
    @Nullable
    public String getInitials() {

        String fullName = getIdentity().getFull();
        String initials = "";

        String[] parts = fullName.split(" ");

        for (String part : parts) {
            initials = initials.concat(part.substring(0, 1));
        }

        return initials;
    }

    /**
     * Get avatar image resource for contact user identity
     * TODO: Very hacky - remove and improve/extend Contact class!
     */
    @Nullable
    public Integer getContactAvatarImageRes() {

        Integer resId = null;
        String resFileName = "avatar_" + getIdentity().getNick(); // e.g.: "avatar_abi"

        try {
            Field resIdField = R.drawable.class.getField(resFileName);
            resId = resIdField.getInt(null);
        } catch (Exception e) {
            return null;
        }

        return resId;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
