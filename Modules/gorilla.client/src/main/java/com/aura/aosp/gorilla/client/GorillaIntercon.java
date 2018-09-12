package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class GorillaIntercon
{
    private static class AppData
    {
        private IGorillaClientService clientService;
        private IGorillaSystemService systemService;

        private byte[] serverSecret = newSecret();
        private byte[] clientSecret = newSecret();

        private boolean svlink;

        private byte[] newSecret()
        {
            byte[] secret = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(secret);

            return secret;
        }
    }

    private final static Map<String, AppData> apkDatas = new HashMap<>();

    @NonNull
    private static AppData getAppData(String apkname)
    {
        synchronized (apkDatas)
        {
            AppData appData = apkDatas.get(apkname);

            if (appData == null)
            {
                appData = new AppData();
                apkDatas.put(apkname, appData);
            }

            return appData;
        }
    }

    public static void delAppData(String apkname)
    {
        synchronized (apkDatas)
        {
            apkDatas.remove(apkname);
        }
    }

    public static void setServerSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSecret = secret;
    }

    public static void setServerSecret(String apkname, String secretBase64)
    {
        setServerSecret(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @Nullable
    public static byte[] getServerSecret(String apkname)
    {
        return getAppData(apkname).serverSecret;
    }

    @Nullable
    public static String getServerSecretBase64(String apkname)
    {
        byte[] serverSecret = getServerSecret(apkname);
        if (serverSecret == null) return null;

        return Base64.encodeToString(serverSecret, Base64.NO_WRAP);
    }

    public static void setClientSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).clientSecret = secret;
    }

    public static void setClientSecret(String apkname, String secretBase64)
    {
        setClientSecret(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @Nullable
    public static byte[] getClientSecret(String apkname)
    {
        return getAppData(apkname).clientSecret;
    }

    @Nullable
    public static String getClientSecretBase64(String apkname)
    {
        byte[] clientSecret = getClientSecret(apkname);
        if (clientSecret == null) return null;

        return Base64.encodeToString(clientSecret, Base64.NO_WRAP);
    }

    public static void setClientService(String apkname, IGorillaClientService service)
    {
        getAppData(apkname).clientService = service;
    }

    @Nullable
    public static IGorillaClientService getClientService(String apkname)
    {
        return getAppData(apkname).clientService;
    }

    public static void setSystemService(String apkname, IGorillaSystemService service)
    {
        getAppData(apkname).systemService = service;
    }

    @Nullable
    public static IGorillaSystemService getSystemService(String apkname)
    {
        return getAppData(apkname).systemService;
    }
}
