/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.Nullable;

import java.security.MessageDigest;
import java.util.Arrays;

import com.aura.aosp.aura.common.simple.Err;

/**
 * Exception safe, annotated and simplified
 * SHA-256 methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SHA
{
    /**
     * Create a SHA-256 signature over secret and buffers.
     *
     * @param secret  start secret.
     * @param buffers binary byte buffers.
     * @return SHA-256 signature over secret and buffers.
     */
    @Nullable
    public static byte[] createSHASignature(byte[] secret, byte[]... buffers)
    {
        if ((secret == null) || (buffers == null))
        {
            Err.errp();
            return null;
        }

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

    /**
     * Veryfy a SHA-256 signature over secret and buffers.
     *
     * @param secret    start secret.
     * @param signature signature to verify.
     * @param buffers   binary byte buffers.
     * @return null on signatures match, error otherwise.
     */
    @Nullable
    public static Err verifySHASignature(byte[] secret, byte[] signature, byte[]... buffers)
    {
        if ((secret == null) || (signature == null) || (buffers == null))
        {
            return Err.err();
        }

        byte[] mysignature = createSHASignature(secret, buffers);
        if (mysignature == null) return Err.getLastErr();

        return Arrays.equals(mysignature, signature) ? null : Err.errp("signature fail!");
    }
}
