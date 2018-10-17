/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;

/**
 * Exception safe, annotated and simplified
 * numeric marshal methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class Marshal
{
    /**
     * Marshal short value into 2 bytes byte array.
     *
     * @param val value.
     * @return byte array.
     */
    @NonNull
    public static byte[] marshalShort(short val)
    {
        byte[] bytes = new byte[ 2 ];

        // formatter: off

        bytes[ 0 ] = (byte) ((val >>  8) & 0xff);
        bytes[ 1 ] = (byte) ((val >>  0) & 0xff);

        // formatter: on

        return bytes;
    }

    /**
     * Unmarshal short value from 2 bytes byte array.
     *
     * @param bytes byte array.
     * @return value or 0 if array null or to short.
     */
    public static short unMarshalShort(byte[] bytes)
    {
        if (bytes == null)
        {
            Err.errp();
            return 0;
        }

        if (bytes.length < 2)
        {
            Err.errp("wrong size=%d", bytes.length);
            return 0;
        }

        // formatter: off

        return (short) (((bytes[ 0 ] & 0xff) << 8)
                + ((bytes[ 1 ] & 0xff) << 0));

        // formatter: on
    }

    /**
     * Marshal int value into 4 bytes byte array.
     *
     * @param val value.
     * @return byte array.
     */
    @NonNull
    public static byte[] marshalInt(int val)
    {
        byte[] bytes = new byte[ 4 ];

        // formatter: off

        bytes[ 0 ] = (byte) ((val >> 24) & 0xff);
        bytes[ 1 ] = (byte) ((val >> 16) & 0xff);
        bytes[ 2 ] = (byte) ((val >>  8) & 0xff);
        bytes[ 3 ] = (byte) ((val >>  0) & 0xff);

        // formatter: on

        return bytes;
    }

    /**
     * Unmarshal int value from 4 bytes byte array.
     *
     * @param bytes byte array.
     * @return value or 0 if array null or to short.
     */
    public static int unMarshalInt(byte[] bytes)
    {
        if (bytes == null)
        {
            Err.errp();
            return 0;
        }

        if (bytes.length < 4)
        {
            Err.errp("wrong size=%d", bytes.length);
            return 0;
        }

        // formatter: off

        return ((bytes[ 0 ] & 0xff) << 24)
             + ((bytes[ 1 ] & 0xff) << 16)
             + ((bytes[ 2 ] & 0xff) <<  8)
             + ((bytes[ 3 ] & 0xff) <<  0);

        // formatter: on
    }

    /**
     * Marshal long value into 8 bytes byte array.
     *
     * @param val value.
     * @return byte array.
     */
    @NonNull
    public static byte[] marshalLong(long val)
    {
        byte[] bytes = new byte[ 8 ];

        // formatter: off

        bytes[ 0 ] = (byte) ((val >> 56) & 0xff);
        bytes[ 1 ] = (byte) ((val >> 48) & 0xff);
        bytes[ 2 ] = (byte) ((val >> 40) & 0xff);
        bytes[ 3 ] = (byte) ((val >> 32) & 0xff);
        bytes[ 4 ] = (byte) ((val >> 24) & 0xff);
        bytes[ 5 ] = (byte) ((val >> 16) & 0xff);
        bytes[ 6 ] = (byte) ((val >>  8) & 0xff);
        bytes[ 7 ] = (byte) ((val >>  0) & 0xff);

        // formatter: on

        return bytes;
    }

    /**
     * Unmarshal long value from 8 bytes byte array.
     *
     * @param bytes byte array.
     * @return value or 0 if array null or to short.
     */
    public static long unMarshalLong(byte[] bytes)
    {
        if (bytes == null)
        {
            Err.errp();
            return 0;
        }

        if (bytes.length < 8)
        {
            Err.errp("wrong size=%d", bytes.length);
            return 0;
        }

        // formatter: off

        return ((long) (bytes[ 0 ] & 0xff) << 56)
             + ((long) (bytes[ 1 ] & 0xff) << 48)
             + ((long) (bytes[ 2 ] & 0xff) << 40)
             + ((long) (bytes[ 3 ] & 0xff) << 32)
             + ((long) (bytes[ 4 ] & 0xff) << 24)
             + ((long) (bytes[ 5 ] & 0xff) << 16)
             + ((long) (bytes[ 6 ] & 0xff) <<  8)
             + ((long) (bytes[ 7 ] & 0xff) <<  0);

        // formatter: on
    }
}
