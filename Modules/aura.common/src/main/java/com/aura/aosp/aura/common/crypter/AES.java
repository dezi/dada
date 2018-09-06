package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES
{
    public final static int AESBlockSize = 16;

    private static boolean dryrunAES = false;

    public static void setAESDryRun(boolean dryrun)
    {
        dryrunAES = dryrun;
    }

    @Nullable
    public static AESBlock newAESCipher(byte[] aeskey)
    {
        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(aeskey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");

            return new AESBlock(skeySpec, cipher);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static class AESBlock
    {
        private SecretKeySpec skeySpec;
        private Cipher cipher;

        private AESBlock(SecretKeySpec skeySpec, Cipher cipher)
        {
            this.skeySpec = skeySpec;
            this.cipher = cipher;
        }
    }

    @Nullable
    public static byte[] encryptAESBlock(AESBlock block, byte[]... buffers)
    {
        int total = 0;

        for (byte[] buffer : buffers)
        {
            total += buffer.length;
        }

        if (total % AESBlockSize != 0)
        {
            Err.errp("wrong block size");
            return null;
        }

        byte[] iv = RND.randomBytes(AESBlockSize);
        byte[] ms = new byte[total];

        int offset = 0;

        for (byte[] buffer : buffers)
        {
            System.arraycopy(buffer, 0, ms, offset, buffer.length);
            offset += buffer.length;
        }

        if (! dryrunAES)
        {
            try
            {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                block.cipher.init(Cipher.ENCRYPT_MODE, block.skeySpec, ivParameterSpec);

                ms = block.cipher.doFinal(ms);
            }
            catch (Exception ex)
            {
                Err.errp(ex);
                return null;
            }
        }

        byte[] ciphertext = new byte[ AESBlockSize + ms.length ];

        System.arraycopy(iv, 0, ciphertext, 0, iv.length);
        System.arraycopy(ms, 0, ciphertext, iv.length, ms.length);

        return ciphertext;
    }

    @Nullable
    public static byte[] encryptAES(byte[] aeskey, byte[]... buffers)
    {
        AESBlock block = newAESCipher(aeskey);

        return encryptAESBlock(block, buffers);
    }

    @Nullable
    public static byte[] decryptAESBlock(AESBlock block, byte[] ciphertext)
    {
        if (ciphertext.length < AESBlockSize * 2)
        {
            Err.errp("wrong block size");
            return null;
        }

        byte[] iv = new byte[ AESBlockSize ];
        byte[] ms = new byte[ ciphertext.length - AESBlockSize ];

        System.arraycopy(ciphertext, 0, iv, 0, iv.length);
        System.arraycopy(ciphertext, iv.length, ms, 0, ms.length);

        if (! dryrunAES)
        {
            try
            {
                IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                block.cipher.init(Cipher.DECRYPT_MODE, block.skeySpec, ivParameterSpec);

                ms = block.cipher.doFinal(ms);
            }
            catch (Exception ex)
            {
                Err.errp(ex);
                return null;
            }
        }

        return ms;
    }

    @Nullable
    public static byte[] decryptAES(byte[] aeskey, byte[] ciphertext)
    {
        AESBlock block = newAESCipher(aeskey);

        return decryptAESBlock(block, ciphertext);
    }
}
