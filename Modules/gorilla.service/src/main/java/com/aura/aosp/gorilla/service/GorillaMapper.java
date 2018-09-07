package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import java.util.HashMap;
import java.util.Map;

public class GorillaMapper
{
    private static class AppData
    {
        private String apkName;
        private byte[] serverSecret;
        private byte[] clientSecret;
    }

    private final static Map<String, AppData> apkDatas = new HashMap<>();

    public static String mapAPK2UUID(String apkname)
    {
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

    public static String mapUUID2APK(String apkuuid)
    {
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

    @NonNull
    private static AppData getAppData(String apkname)
    {
        AppData appData = apkDatas.get(apkname);

        if (appData == null)
        {
            appData = new AppData();
            apkDatas.put(apkname, appData);
        }

        return appData;
    }

    public static void setServerSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSecret = secret;
    }

    @Nullable
    public static byte[] getServerSecret(String apkname)
    {
        return getAppData(apkname).serverSecret;
    }

    public static void setClientSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).clientSecret = secret;
    }

    @Nullable
    public static byte[] getClientSecret(String apkname)
    {
        return getAppData(apkname).clientSecret;
    }
}
