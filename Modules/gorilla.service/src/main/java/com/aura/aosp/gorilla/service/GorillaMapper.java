package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

public class GorillaMapper
{
    @Nullable
    public static String mapAPK2UUID(String apkname)
    {
        if (apkname == null)
        {
            Err.errp();
            return null;
        }

        if (apkname.equals("com.aura.aosp.gorilla.sysapp"))
        {
            return "A0Et8SbAQ4e7O3tVhYdJOQ==";
        }

        if (apkname.equals("com.aura.aosp.gorilla.messenger"))
        {
            return "B0Et8SbAQ4e7O3tVhYdJOQ==";
        }

        Err.err("unknown apk=%s", apkname);

        return null;
    }

    @Nullable
    public static String mapUUID2APK(String apkuuid)
    {
        if (apkuuid == null)
        {
            Err.errp();
            return null;
        }

        if (apkuuid.equals("A0Et8SbAQ4e7O3tVhYdJOQ=="))
        {
            return "com.aura.aosp.gorilla.sysapp";
        }

        if (apkuuid.equals("B0Et8SbAQ4e7O3tVhYdJOQ=="))
        {
            return "com.aura.aosp.gorilla.messenger";
        }

        Err.err("unknown apkuuid=%s", apkuuid);

        return null;
    }
}
