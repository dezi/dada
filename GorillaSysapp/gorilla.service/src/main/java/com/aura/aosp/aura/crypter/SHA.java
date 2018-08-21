package com.aura.aosp.aura.crypter;

import android.support.annotation.Nullable;

import java.util.Arrays;
import java.security.MessageDigest;

public class SHA
{
    @Nullable
    public static byte[] createSHASignature(byte[] secret, byte[]... buffers)
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
        }

        return null;
    }

    public static boolean verifySHASignature(byte[] secret, byte[] signature, byte[]... buffers)
    {
        byte[] remotesign = createSHASignature(secret, buffers);

        return Arrays.equals(signature, remotesign);
    }
}
