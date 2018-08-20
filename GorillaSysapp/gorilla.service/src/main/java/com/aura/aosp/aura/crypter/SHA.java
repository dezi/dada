package com.aura.aosp.aura.crypter;

import android.support.annotation.Nullable;

import java.security.MessageDigest;
import java.util.Arrays;

public class SHA
{
    @Nullable
    public static byte[] SHACreateSignature(byte[] secret, byte[] buffer)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(secret);
            md.update(buffer);
            return md.digest();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean SHAVerifySignature(byte[] secret, byte[] signature, byte[] buffer)
    {
        byte[] remotesign = SHACreateSignature(secret, buffer);

        return Arrays.equals(signature, remotesign);
    }
}
