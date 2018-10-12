package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;

import java.security.SecureRandom;

public class RND
{
    @NonNull
    public static byte[] randomBytes(int size)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static int randomIntn(int max)
    {
        if (max == 0) return 0;

        byte[] bytes = randomBytes(4);

        // @formatter:off

        long val = ((bytes[ 0 ] & 0x7f) << 24)
                 + ((bytes[ 1 ] & 0xff) << 16)
                 + ((bytes[ 2 ] & 0xff) <<  8)
                 + ((bytes[ 3 ] & 0xff) <<  0);

        // @formatter:on

        return (int) (val % max);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static double RandomFloat64()
    {
        byte[] bytes = randomBytes(4);

        // @formatter:off

        long val = ((bytes[ 0 ] & 0x7f) << 24)
                 + ((bytes[ 1 ] & 0xff) << 16)
                 + ((bytes[ 2 ] & 0xff) <<  8)
                 + ((bytes[ 3 ] & 0xff) <<  0);

        // @formatter:on

        return ((double) val) / ((double) 0x7fffffff);
    }
}
