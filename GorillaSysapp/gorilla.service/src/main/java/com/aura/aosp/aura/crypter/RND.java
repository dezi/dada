package com.aura.aosp.aura.crypter;

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
}
