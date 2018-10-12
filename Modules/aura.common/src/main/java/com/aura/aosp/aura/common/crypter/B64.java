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
    @NonNull
    public static String encode(@NonNull byte[] bytes)
    {
        return new String(Base64.encode(bytes, Base64.NO_WRAP));
    }

    /**
     * Decode base64 encoded string.
     *
     * @param base64 base64 encoded string.
     * @return decoded bytes or null.
     */
    @Nullable
    public static byte[] decode(@NonNull String base64)
    {
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
