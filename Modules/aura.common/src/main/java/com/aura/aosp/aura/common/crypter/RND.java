package com.aura.aosp.aura.common.crypter;

import android.util.Base64;

import java.security.SecureRandom;

public class RND
{
    public static byte[] randomBytes(int size)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static int randomInt(int max)
    {
        byte[] bytes = randomBytes(4);

        // @formatter:off

        long val = ((bytes[ 0 ] & 0xff) << 24)
                 + ((bytes[ 1 ] & 0xff) << 16)
                 + ((bytes[ 2 ] & 0xff) <<  8)
                 + ((bytes[ 3 ] & 0xff) <<  0);

        // @formatter:on

        return (int) (val % max);
    }

    public static byte[] randomUUID()
    {
        byte[] uuid = randomBytes(16);

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
}
