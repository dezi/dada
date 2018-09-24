package com.aura.aosp.aura.common.simple;

@SuppressWarnings("PointlessBitwiseExpression")
public class Marshal
{
    public static byte[] marshalShort(short val)
    {
        byte[] bytes = new byte[ 2 ];

        // formatter: off

        bytes[ 0 ] = (byte) ((val >>  8) & 0xff);
        bytes[ 1 ] = (byte) ((val >>  0) & 0xff);

        // formatter: on

        return bytes;
    }

    public static short unMarshalShort(byte[] bytes)
    {
        // formatter: off

        return (short) (((bytes[ 0 ] & 0xff) << 8)
                + ((bytes[ 1 ] & 0xff) << 0));

        // formatter: on
    }

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

    public static int unMarshalInt(byte[] bytes)
    {
        // formatter: off

        return ((bytes[ 0 ] & 0xff) << 24)
             + ((bytes[ 1 ] & 0xff) << 16)
             + ((bytes[ 2 ] & 0xff) <<  8)
             + ((bytes[ 3 ] & 0xff) <<  0);

        // formatter: on
    }

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

    public static long unMarshalLong(byte[] bytes)
    {
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
