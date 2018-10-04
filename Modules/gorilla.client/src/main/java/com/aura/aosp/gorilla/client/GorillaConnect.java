package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.SecureRandom;

class GorillaConnect
{
    private static IGorillaSystemService systemService;

    private static byte[] serverSecret = newSecret();
    private static byte[] clientSecret = newSecret();

    private static String ownerUUID;

    private static boolean svlink;
    private static boolean uplink;

    private static byte[] newSecret()
    {
        byte[] secret = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(secret);

        return secret;
    }

    static void setServerSecretBase64(String secretBase64)
    {
        serverSecret = Base64.decode(secretBase64, Base64.DEFAULT);
    }

    @NonNull
    static String getServerSecretBase64()
    {
        return Base64.encodeToString(serverSecret, Base64.NO_WRAP);
    }

    @NonNull
    static String getClientSecretBase64()
    {
        return Base64.encodeToString(clientSecret, Base64.NO_WRAP);
    }

    static void setSystemService(IGorillaSystemService service)
    {
        systemService = service;
    }

    @Nullable
    static IGorillaSystemService getSystemService()
    {
        return systemService;
    }

    static boolean setServiceStatus(boolean svlinkNew)
    {
        boolean change = (svlink != svlinkNew);
        svlink = svlinkNew;
        return change;
    }

    static boolean getServiceStatus()
    {
        return svlink;
    }

    static boolean setUplinkStatus(boolean uplinkNew)
    {
        boolean change = (uplink != uplinkNew);
        uplink = uplinkNew;
        return change;
    }

    static boolean getUplinkStatus()
    {
        return uplink;
    }

    static boolean setOwnerUUID(@Nullable String ownerUUIDNew)
    {
        boolean change = (ownerUUIDNew != null) && ! ownerUUIDNew.equals(ownerUUID);
        ownerUUID = ownerUUIDNew;
        return change;
    }

    @Nullable
    static String getOwnerUUID()
    {
        return ownerUUID;
    }


    static String createSHASignatureBase64(Object... params)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(serverSecret);
            md.update(clientSecret);

            for (Object param : params)
            {
                if (param != null) md.update(param.toString().getBytes());
            }

            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
