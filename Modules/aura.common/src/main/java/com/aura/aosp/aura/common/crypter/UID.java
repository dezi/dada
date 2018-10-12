/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.aura.aosp.aura.common.simple.Err;

/**
 * Exception safe, annotated and simplified
 * UUID methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings("WeakerAccess")
public class UID
{
    /**
     * Generate a 128 bit UUID from secure random.
     *
     * @return 128 bit UUID from secure random.
     */
    @NonNull
    public static byte[] randomUUID()
    {
        byte[] uuid = RND.randomBytes(16);

        // variant bits; see section 4.1.1

        uuid[8] = (byte) ((uuid[8] & ~0xc0) | 0x80);

        // version 4 (pseudo-random); see section 4.1.3

        uuid[6] = (byte) ((uuid[6] & ~0xf0) | 0x40);

        return uuid;
    }

    /**
     * Generate a 128 bit UUID from secure random base64 encoded.
     *
     * @return 128 bit UUID from secure random base64 encoded.
     */
    @NonNull
    public static String randomUUIDBase64()
    {
        //
        // Under these circumstances B64.encode is always NonNull.
        //
        // noinspection ConstantConditions

        return B64.encode(randomUUID());
    }

    /**
     * Generate a 128 bit UUID from secure random as traditional UUID string.
     *
     * @return 128 bit UUID from secure random as traditional UUID string.
     */
    @NonNull
    public static String randomUUIDString()
    {
        ByteBuffer bb = ByteBuffer.wrap(randomUUID());

        //
        // ByteBuffer.getLong is a potential source of exceptions.
        //
        // Since randomUUID delivers always 16 byte, were a sure
        // that no exeception is virtual possible.
        //

        long firstLong = bb.getLong();
        long secondLong = bb.getLong();

        return new UUID(firstLong, secondLong).toString();
    }

    /**
     * Convert UUID in bytes to UUID as traditional string.
     *
     * @param uuidBytes UUID in 16 bytes.
     * @return UUID as traditional string or null.
     */
    @Nullable
    public static String convertUUIDToString(byte[] uuidBytes)
    {
        if (uuidBytes == null)
        {
            Err.errp();
            return null;
        }

        if (uuidBytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidBytes.length);
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);

        long firstLong = bb.getLong();
        long secondLong = bb.getLong();

        return new UUID(firstLong, secondLong).toString();
    }

    /**
     * Convert UUID as base64 encoded string to UUID as traditional string.
     *
     * @param uuidBase64 UUID as base64 encoded string.
     * @return UUID as traditional string or null.
     */
    @Nullable
    public static String convertUUIDToString(String uuidBase64)
    {
        if (uuidBase64 == null)
        {
            Err.errp();
            return null;
        }

        byte[] uuidbytes = convertUUIDToBytes(uuidBase64);
        if (uuidbytes == null) return null;

        if (uuidbytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidbytes.length);
            return null;
        }

        return convertUUIDToString(uuidbytes);
    }

    /**
     * Convert UUID in bytes to UUID as base64 encoded string.
     *
     * @param uuidBytes UUID in 16 bytes.
     * @return UUID as base64 encoded string or null.
     */
    @Nullable
    public static String convertUUIDToStringBase64(byte[] uuidBytes)
    {
        if (uuidBytes == null)
        {
            Err.errp();
            return null;
        }

        if (uuidBytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidBytes.length);
            return null;
        }

        return B64.encode(uuidBytes);
    }

    /**
     * Convert UUID in either base64 encoded string or as traditional string
     * into UUID in bytes.
     *
     * @param uuidAnyStr base64 or traditional UUID string.
     * @return UUID in bytes or null.
     */
    @Nullable
    public static byte[] convertUUIDToBytes(String uuidAnyStr)
    {
        if (uuidAnyStr == null)
        {
            Err.errp();
            return null;
        }

        if (uuidAnyStr.contains("-"))
        {
            //
            // 2aeb514f-0bf1-4ade-ac03-c9c3a2a56b3a
            // 123456789012345678901234567890123456
            //

            if (uuidAnyStr.length() != 36)
            {
                Err.errp("uuid wrong format=%s!", uuidAnyStr);
                return null;
            }

            try
            {
                UUID uuid = UUID.fromString(uuidAnyStr);

                ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
                bb.putLong(uuid.getMostSignificantBits());
                bb.putLong(uuid.getLeastSignificantBits());

                return bb.array();
            }
            catch (Exception ex)
            {
                Err.err(ex);
                return null;
            }
        }

        byte[] uuidBytes = B64.decode(uuidAnyStr);

        if ((uuidBytes == null) || (uuidBytes.length != 16))
        {
            Err.errp("uuid wrong format=%s!", uuidAnyStr);
            return null;
        }

        return uuidBytes;
    }
}
