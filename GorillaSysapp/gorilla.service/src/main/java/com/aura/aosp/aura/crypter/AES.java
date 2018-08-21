package com.aura.aosp.aura.crypter;

import android.support.annotation.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES
{
    private final static int AESBlockSize = 16;

    private static boolean dryrunAES;

    public static void setAESDryRun(boolean dryrun)
    {
        dryrunAES = dryrun;
    }

    @Nullable
    public static Block newAESCipher(byte[] aeskey)
    {
        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(aeskey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            return new Block(skeySpec, cipher);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static class Block
    {
        SecretKeySpec skeySpec;
        Cipher cipher;

        public Block(SecretKeySpec skeySpec, Cipher cipher)
        {
            this.skeySpec = skeySpec;
            this.cipher = cipher;
        }
    }

    @Nullable
    public byte[] EncryptAESBlock(Block block, byte[]... buffers)
    {
        int total = 0;

        for (byte[] buffer : buffers)
        {
            total += buffer.length;
        }

        if (total % AESBlockSize != 0)
        {
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
                ex.printStackTrace();

                return null;
            }
        }

        byte[] ciphertext = new byte[ AESBlockSize + ms.length ];

        System.arraycopy(iv, 0, ciphertext, 0, iv.length);
        System.arraycopy(ms, 0, ciphertext, iv.length, ms.length);

        return ciphertext;
    }
}
