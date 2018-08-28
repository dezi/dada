package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

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
            Err.errp(ex);
            return null;
        }
    }

    public static Err verifySHASignature(byte[] secret, byte[] signature, byte[]... buffers)
    {
        byte[] remotesign = createSHASignature(secret, buffers);
        if (remotesign == null) return Err.getLastErr();

        return Arrays.equals(signature, remotesign) ? null : Err.errp("signature fail!");
    }
}
