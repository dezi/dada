package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.aura.aosp.aura.common.simple.Err;

public class UID
{
    public static byte[] randomUUID()
    {
        byte[] uuid = RND.randomBytes(16);

        // variant bits; see section 4.1.1

        uuid[8] = (byte) ((uuid[8] & ~0xc0) | 0x80);

        // version 4 (pseudo-random); see section 4.1.3

        uuid[6] = (byte) ((uuid[6] & ~0xf0) | 0x40);

        return uuid;
    }

    public static String randomUUIDBase64()
    {
        return Base64.encodeToString(randomUUID(), android.util.Base64.NO_WRAP);
    }

    public static String randomUUIDString()
    {
        ByteBuffer bb = ByteBuffer.wrap(randomUUID());
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();

        return new UUID(firstLong, secondLong).toString();
    }

    @Nullable
    public static String getUUIDString(byte[] uuidbytes)
    {
        if (uuidbytes == null)
        {
            Err.errp();
            return null;
        }

        if (uuidbytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidbytes.length);
            return null;
        }

        ByteBuffer bb = ByteBuffer.wrap(uuidbytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();

        return new UUID(firstLong, secondLong).toString();
    }

    @Nullable
    public static String getUUIDString(@NonNull String uuidbase64)
    {
        byte[] uuidbytes = getUUIDBytes(uuidbase64);
        if (uuidbytes == null)
        {
            Err.errp();
            return null;
        }

        if (uuidbytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidbytes.length);
            return null;
        }

        return getUUIDString(uuidbytes);
    }

    @Nullable
    public static String getUUIDBase64(byte[] uuidbytes)
    {
        if (uuidbytes == null)
        {
            Err.errp();
            return null;
        }

        if (uuidbytes.length != 16)
        {
            Err.errp("uuid wrong size=%d!", uuidbytes.length);
            return null;
        }

        return Base64.encodeToString(uuidbytes, android.util.Base64.NO_WRAP);
    }

    @Nullable
    public static byte[] getUUIDBytes(String uuidstr)
    {
        if (uuidstr == null)
        {
            Err.errp();
            return null;
        }

        if (uuidstr.contains("-"))
        {
            //
            // 2aeb514f-0bf1-4ade-ac03-c9c3a2a56b3a
            // 123456789012345678901234567890123456
            //

            if (uuidstr.length() != 36)
            {
                Err.errp("uuid wrong format=%s!", uuidstr);
                return null;
            }

            try
            {
                UUID uuid = UUID.fromString(uuidstr);

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

        byte[] uuidbytes = Base64.decode(uuidstr, Base64.DEFAULT);

        if (uuidbytes.length != 16)
        {
            Err.errp("uuid wrong format=%s!", uuidstr);
            return null;
        }

        return uuidbytes;
    }
}
