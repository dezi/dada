/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Exception safe, annotated and simplified
 * AES encryption methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AES
{
    /**
     * AES common block size.
     */
    public final static int AESBlockSize = 16;

    /**
     * Test flag for disabling all AES encryption.
     */
    private static boolean dryrunAES = false;

    /**
     * Set dry run flag.
     *
     * @param dryrun dryrun enable flag.
     */
    public static void setAESDryRun(boolean dryrun)
    {
        dryrunAES = dryrun;
    }

    /**
     * Create a new cypher from binary AES key.
     *
     * @param aeskey binary AES key.
     * @return AES cypher block.
     */
    @Nullable
    public static AESBlock newAESCipher(byte[] aeskey)
    {
        if (aeskey == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            SecretKeySpec skeySpec = new SecretKeySpec(aeskey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");

            return new AESBlock(skeySpec, cipher);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * AES block holding sub class.
     */
    public static class AESBlock
    {
        /**
         * Secret key spec.
         */
        private SecretKeySpec skeySpec;

        /**
         * Cipher worker.
         */
        private Cipher cipher;

        /**
         * Construct AES cypher block.
         *
         * @param skeySpec secret key spec.
         * @param cipher cipher worker.
         */
        private AESBlock(SecretKeySpec skeySpec, Cipher cipher)
        {
            this.skeySpec = skeySpec;
            this.cipher = cipher;
        }
    }

    /**
     * Encrypt data buffers and deliver encrypted data in
     * one buffer using a pre-allocated cypher block.
     *
     * @param block AES cypher block.
     * @param buffers Arbitrary number of binary buffers.
     * @return One block of encrypted data or null on failure.
     */
    @Nullable
    public static byte[] encryptAESBlock(AESBlock block, byte[]... buffers)
    {
        if ((block == null) || (buffers == null))
        {
            Err.errp();
            return null;
        }

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

        //
        // We use random IV.
        //

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

        //
        // Construct final cypher message including IV and data.
        //

        byte[] ciphertext = new byte[ AESBlockSize + ms.length ];

        System.arraycopy(iv, 0, ciphertext, 0, iv.length);
        System.arraycopy(ms, 0, ciphertext, iv.length, ms.length);

        return ciphertext;
    }

    /**
     * Encrypt data buffers and deliver encrypted data in
     * one buffer using only a binary AES key.
     *
     * @param aeskey binary AES key.
     * @param buffers Arbitrary number of binary buffers.
     * @return One block of encrypted data or null on failure.
     */
    @Nullable
    public static byte[] encryptAES(byte[] aeskey, byte[]... buffers)
    {
        if ((aeskey == null) || (buffers == null))
        {
            Err.errp();
            return null;
        }

        AESBlock block = newAESCipher(aeskey);

        return encryptAESBlock(block, buffers);
    }

    /**
     * Decrypt an AES encrypted message using a pre-allocated AES cypher block.
     *
     * @param block AES cypher block.
     * @param ciphertext encrypted data.
     * @return decrypted binary data or null on failure.
     */
    @Nullable
    public static byte[] decryptAESBlock(AESBlock block, byte[] ciphertext)
    {
        if ((block == null) || (ciphertext == null))
        {
            Err.errp();
            return null;
        }

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

    /**
     * Decrypt an AES encrypted message using a binary AES key.
     *
     * @param aeskey binary AES key.
     * @param ciphertext encrypted data.
     * @return decrypted binary data or null on failure.
     */
    @Nullable
    public static byte[] decryptAES(byte[] aeskey, byte[] ciphertext)
    {
        if ((aeskey == null) || (ciphertext == null))
        {
            Err.errp();
            return null;
        }

        AESBlock block = newAESCipher(aeskey);

        return decryptAESBlock(block, ciphertext);
    }
}
