package com.aura.aosp.gorilla.goproto;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.simple.Err;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GoprotoMessage
{
    public int Magic;
    public int Version;
    public int Command;
    public int Idsmask;
    public int Keymask;

    public int Size;

    public byte[] Head;
    public byte[] Load;

    public byte[] Sign;
    public byte[] Base;

    public GoprotoMessage()
    {
        Magic = GoprotoDefs.GorillaMagic;
        Version = (GoprotoDefs.VersionV1Major << 8) + (GoprotoDefs.VersionV1Minor << 0);
    }

    public GoprotoMessage(int command)
    {
        this();

        Command = command;
    }

    public GoprotoMessage(int command, int idsmask, int keymask, int size)
    {
        this(command);

        Idsmask = idsmask;
        Keymask = keymask;

        Size = size;

        if ((Idsmask & GoprotoDefs.HasRSASignature) != 0)
        {
            Size += GoprotoDefs.GorillaRSASignSize;
        }

        if ((Idsmask & GoprotoDefs.HasSHASignature) != 0)
        {
            Size += GoprotoDefs.GorillaSHASignSize;
        }
    }

    @NonNull
    @SuppressWarnings("PointlessBitwiseExpression")
    public byte[] marshall()
    {
        byte[] bytes = new byte[ GoprotoDefs.GorillaHeaderSize ];

        // @formatter:off

        bytes[  0 ] = (byte) ((Magic   >> 24) & 0xff);
        bytes[  1 ] = (byte) ((Magic   >> 16) & 0xff);
        bytes[  2 ] = (byte) ((Magic   >>  8) & 0xff);
        bytes[  3 ] = (byte) ((Magic   >>  0) & 0xff);

        bytes[  4 ] = (byte) ((Version >>  8) & 0xff);
        bytes[  5 ] = (byte) ((Version >>  0) & 0xff);

        bytes[  6 ] = (byte) ((Command >>  8) & 0xff);
        bytes[  7 ] = (byte) ((Command >>  0) & 0xff);

        bytes[  8 ] = (byte) ((Idsmask >>  8) & 0xff);
        bytes[  9 ] = (byte) ((Idsmask >>  0) & 0xff);

        bytes[ 10 ] = (byte) ((Keymask >>  8) & 0xff);
        bytes[ 11 ] = (byte) ((Keymask >>  0) & 0xff);

        bytes[ 12 ] = (byte) ((Size    >> 24) & 0xff);
        bytes[ 13 ] = (byte) ((Size    >> 16) & 0xff);
        bytes[ 14 ] = (byte) ((Size    >>  8) & 0xff);
        bytes[ 15 ] = (byte) ((Size    >>  0) & 0xff);

        // @formatter:on

        Head = bytes;

        return bytes;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public Err unmarshall(byte[] bytes)
    {
        if (bytes == null) return Err.errp();

        if (bytes.length != GoprotoDefs.GorillaHeaderSize)
        {
            return Err.errp("size=%d fail!", bytes.length);
        }

        // @formatter:off

        Magic   = ((bytes[ 0 ] & 0xff) << 24)
                + ((bytes[ 1 ] & 0xff) << 16)
                + ((bytes[ 2 ] & 0xff) <<  8)
                + ((bytes[ 3 ] & 0xff) <<  0);

        Version = ((bytes[  4 ] & 0xff) << 8)
                + ((bytes[  5 ] & 0xff) << 0);

        Command = ((bytes[  6 ] & 0xff) << 8)
                + ((bytes[  7 ] & 0xff) << 0);

        Idsmask = ((bytes[  8 ] & 0xff) << 8)
                + ((bytes[  9 ] & 0xff) << 0);

        Keymask = ((bytes[ 10 ] & 0xff) << 8)
                + ((bytes[ 11 ] & 0xff) << 0);

        Size    = ((bytes[ 12 ] & 0xff) << 24)
                + ((bytes[ 13 ] & 0xff) << 16)
                + ((bytes[ 14 ] & 0xff) <<  8)
                + ((bytes[ 15 ] & 0xff) <<  0);

        // @formatter:on

        Head = bytes;

        return null;
    }
}
