package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;
import android.util.Base64;

import com.aura.aosp.aura.common.crypter.RND;
import com.aura.aosp.gorilla.client.IGorillaRemote;

import com.aura.aosp.aura.common.simple.Log;

import java.security.MessageDigest;

public class GorillaRemote extends IGorillaRemote.Stub
{
    public void sendClientSecret(String apkname, String clientSecret)
    {
        Log.d("apkname=%s clientSecret=%s",apkname, clientSecret);

        byte[] clientSecretBytes = Base64.decode(clientSecret, Base64.DEFAULT);
        GorillaMapper.setClientSecret(apkname, clientSecretBytes);

        byte[] serverSecretBytes = RND.randomBytes(16);
        GorillaMapper.setServerSecret(apkname, serverSecretBytes);

        String serverSecret = Base64.encodeToString(serverSecretBytes, Base64.NO_WRAP);
        String challenge = createSHASignatureBase64(clientSecretBytes);

        GorillaSender.sendBroadCastSecret(apkname, serverSecret, challenge);
    }

    public boolean validateConnect(String apkname, String challenge)
    {
        byte[] serverSecretBytes = GorillaMapper.getServerSecret(apkname);
        String solution = createSHASignatureBase64(serverSecretBytes);

        if ((challenge == null) || (solution == null) || ! challenge.equals(solution))
        {
            Log.e("failed!");
            return false;
        }

        Log.d("validated apkname=%s",apkname);

        return true;
    }

    public int addNumbers(int int1, int int2)
    {
        Log.d("...");

        return int1 + int2;
    }

    private static String createSHASignatureBase64(byte[] secret, byte[]... buffers)
    {
        return Base64.encodeToString(createSHASignature(secret, buffers), Base64.NO_WRAP);
    }

    @Nullable
    private static byte[] createSHASignature(byte[] secret, byte[]... buffers)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(secret);

            for (byte[] buffer : buffers)
            {
                md.update(buffer);
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
