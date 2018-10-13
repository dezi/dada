/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.crypter;

import android.support.annotation.NonNull;

import java.security.SecureRandom;

/**
 * Exception safe, annotated and simplified
 * secure random methods.
 * <p>
 * Methods do not need to be seeded.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class RND
{
    /**
     * Generate byte array random.
     *
     * @param size size to allocate.
     * @return allocated byte array with random values.
     */
    @NonNull
    public static byte[] randomBytes(int size)
    {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }

    /**
     * Generate random integer value.
     *
     * @param max max value - 1.
     * @return random value between 0 and max - 1.
     */
    @SuppressWarnings("PointlessBitwiseExpression")
    public static int randomIntn(int max)
    {
        if (max == 0) return 0;

        byte[] bytes = randomBytes(4);

        // @formatter:off

        long val = ((bytes[0] & 0x7f) << 24)
                + ((bytes[1] & 0xff) << 16)
                + ((bytes[2] & 0xff) << 8)
                + ((bytes[3] & 0xff) << 0);

        // @formatter:on

        return (int) (val % max);
    }

    /**
     * Generate a random float value between 0 and 1.
     * @return random float value.
     */
    @SuppressWarnings("PointlessBitwiseExpression")
    public static double RandomFloat64()
    {
        byte[] bytes = randomBytes(4);

        // @formatter:off

        long val = ((bytes[0] & 0x7f) << 24)
                + ((bytes[1] & 0xff) << 16)
                + ((bytes[2] & 0xff) << 8)
                + ((bytes[3] & 0xff) << 0);

        // @formatter:on

        return ((double) val) / ((double) 0x7fffffff);
    }
}
