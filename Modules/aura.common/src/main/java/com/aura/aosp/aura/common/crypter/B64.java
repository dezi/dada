/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import com.aura.aosp.aura.common.simple.Err;

/**
 * Exception safe, annotated and simplified
 * versions of base64 encode and decode.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings("WeakerAccess")
public class B64
{
    /**
     * Encode bytes into default base64 string.
     *
     * @param bytes bytes to be encoded.
     * @return base64 encoded string or null.
     */
    @Nullable
    public static String encode(byte[] bytes)
    {
        if (bytes == null)
        {
            Err.err();
            return null;
        }

        try
        {
            return new String(Base64.encode(bytes, Base64.NO_WRAP));
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Decode base64 encoded string.
     *
     * @param base64 base64 encoded string.
     * @return decoded bytes or null.
     */
    @Nullable
    public static byte[] decode(String base64)
    {
        if (base64 == null)
        {
            Err.err();
            return null;
        }

        try
        {
            return Base64.decode(base64, Base64.DEFAULT);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }
}
