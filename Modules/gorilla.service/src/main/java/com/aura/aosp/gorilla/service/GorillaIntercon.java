package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GorillaIntercon
{
    private final static Map<String, AppData> apkDatas = new HashMap<>();

    private static class AppData
    {
        private IGorillaClientService clientService;
        private IGorillaSystemService systemService;

        private byte[] serverSignature = newSecret();
        private byte[] clientSignature = newSecret();

        private boolean svlink;
        private boolean uplink;

        private byte[] newSecret()
        {
            byte[] secret = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(secret);

            return secret;
        }
    }

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

    public static List<String> getAllApknames()
    {
        synchronized (apkDatas)
        {
            return new ArrayList<>(apkDatas.keySet());
        }
    }

    public static void setServerSignature(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSignature = secret;
    }

    public static void setServerSignature(String apkname, String secretBase64)
    {
        setServerSignature(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @NonNull
    public static byte[] getServerSignature(String apkname)
    {
        return getAppData(apkname).serverSignature;
    }

    @NonNull
    public static String getServerSignatureBase64(String apkname)
    {
        byte[] serverSignature = getServerSignature(apkname);
        return Base64.encodeToString(serverSignature, Base64.NO_WRAP);
    }

    public static void setClientSignature(String apkname, byte[] secret)
    {
        getAppData(apkname).clientSignature = secret;
    }

    public static void setClientSignature(String apkname, String secretBase64)
    {
        setClientSignature(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @NonNull
    public static byte[] getClientSignature(String apkname)
    {
        return getAppData(apkname).clientSignature;
    }

    @NonNull
    public static String getClientSignatureBase64(String apkname)
    {
        byte[] clientSignature = getClientSignature(apkname);
        return Base64.encodeToString(clientSignature, Base64.NO_WRAP);
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

    public static boolean setServiceStatus(String apkname, boolean svlink)
    {
        boolean change = (getAppData(apkname).svlink != svlink);
        getAppData(apkname).svlink = svlink;
        return change;
    }

    public static boolean getServiceStatus(String apkname)
    {
        return getAppData(apkname).svlink;
    }

    public static boolean setUplinkStatus(String apkname, boolean uplink)
    {
        boolean change = (getAppData(apkname).uplink != uplink);
        getAppData(apkname).uplink = uplink;
        return change;
    }

    public static boolean getUplinkStatus(String apkname)
    {
        return getAppData(apkname).uplink;
    }

    public static String createSHASignatureBase64(String apkname, Object... params)
    {
        return Base64.encodeToString(createSHASignature(apkname, params), Base64.NO_WRAP);
    }

    @Nullable
    public static byte[] createSHASignature(String apkname, Object... params)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(getServerSignature(apkname));
            md.update(getClientSignature(apkname));

            for (Object param : params)
            {
                if (param != null)
                {
                    md.update(param.toString().getBytes());
                }
            }

            return md.digest();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
