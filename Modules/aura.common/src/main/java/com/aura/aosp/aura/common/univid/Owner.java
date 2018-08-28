package com.aura.aosp.aura.common.univid;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Simple;

public class Owner
{
    @SuppressLint("ApplySharedPref")
    public static void setOwnerUUIDBase64(String userUUID)
    {
        String prefkey = "aura.pref.ownerUUID";
        Simple.getPrefs().edit().putString(prefkey, userUUID).commit();
    }

    @Nullable
    public static String getOwnerUUIDBase64()
    {
        String prefkey = "aura.pref.ownerUUID";
        return Simple.getPrefs().getString(prefkey, null);
    }

    @Nullable
    public static Identity getOwnerIdentity()
    {
        return Contacts.getContact(getOwnerUUIDBase64());
    }
}
