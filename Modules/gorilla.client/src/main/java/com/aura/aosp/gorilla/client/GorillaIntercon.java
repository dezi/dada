package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class GorillaIntercon
{
    private final static Map<String, AppData> apkDatas = new HashMap<>();

    private static class AppData
    {
        private IGorillaClientService clientService;
        private IGorillaSystemService systemService;

        private byte[] serverSecret = newSecret();
        private byte[] clientSecret = newSecret();

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

    public static void setServerSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSecret = secret;
    }

    public static void setServerSecret(String apkname, String secretBase64)
    {
        setServerSecret(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @NonNull
    public static byte[] getServerSecret(String apkname)
    {
        return getAppData(apkname).serverSecret;
    }

    @NonNull
    public static String getServerSecretBase64(String apkname)
    {
        byte[] serverSecret = getServerSecret(apkname);
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

    @NonNull
    public static byte[] getClientSecret(String apkname)
    {
        return getAppData(apkname).clientSecret;
    }

    @NonNull
    public static String getClientSecretBase64(String apkname)
    {
        byte[] clientSecret = getClientSecret(apkname);
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

            md.update(getServerSecret(apkname));
            md.update(getClientSecret(apkname));

            for (Object param : params)
            {
                if (param != null) md.update(param.toString().getBytes());
            }

            return md.digest();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    public static String createSHASignatureBase64(byte[] secret, byte[]... buffers)
    {
        return Base64.encodeToString(createSHASignature(secret, buffers), Base64.NO_WRAP);
    }

    @Nullable
    public static byte[] createSHASignature(byte[] secret, byte[]... buffers)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(secret);

            for (byte[] buffer : buffers)
            {
                if (buffer != null) md.update(buffer);
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
