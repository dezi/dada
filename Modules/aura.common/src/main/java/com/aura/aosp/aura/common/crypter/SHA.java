package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.security.MessageDigest;
import java.util.Arrays;

import com.aura.aosp.aura.common.simple.Err;

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
                if (buffer != null) md.update(buffer);
            }

            return md.digest();
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public static String createSHASignatureBase64(byte[] secret, byte[]... buffers)
    {
        return Base64.encodeToString(createSHASignature(secret, buffers), Base64.NO_WRAP);
    }

    public static Err verifySHASignature(byte[] secret, byte[] signature, byte[]... buffers)
    {
        byte[] remotesign = createSHASignature(secret, buffers);
        if (remotesign == null) return Err.getLastErr();

        return Arrays.equals(signature, remotesign) ? null : Err.errp("signature fail!");
    }
}
