package com.aura.aosp.gorilla.service;

@SuppressWarnings("PointlessBitwiseExpression")
public class GorillaMessage
{
    public final static int GorillaMagic = 0x6ea60451;

    public final static int GorillaMaxSize = 16 * 1024;

    public final static int GorillaUUIDSize = 16;
    public final static int GorillaHeaderSize = 16;
    public final static int GorillaAESKeySize = 32;
    public final static int GorillaSHASignSize = 32;
    public final static int GorillaRSASignSize = 256;
    public final static int GorillaChallengeSize = 32;

    public final static byte VersionV1Major = 0x01;
    public final static byte VersionV1Minor = 0x01;

    //
    // Server messages.
    //

    public final static int MsgAuthRequest = 0x0001;
    public final static int MsgAuthChallenge = 0x0002;
    public final static int MsgAuthSolved = 0x0003;
    public final static int MsgAuthAccepted = 0x0004;
    public final static int MsgAuthReqNodes = 0x0005;
    public final static int MsgAuthSndNodes = 0x0006;

    public final static int MsgMessageUpload = 0x1000;
    public final static int MsgMessageDownload = 0x1001;

    public final static int MsgGetGotelloAmt = 0x3000;
    public final static int MsgGotGotelloAmt = 0x3001;
    public final static int MsgInviteGotello = 0x3002;

    //
    // Id masks.
    //
    public final static int HasSHASignature = 0x0001;
    public final static int HasRSASignature = 0x0002;
    public final static int HasMessageUUID = 0x0004;
    public final static int HasReceiverUserUUID = 0x0008;
    public final static int HasReceiverDeviceUUID = 0x0010;
    public final static int HasSenderUserUUID = 0x0020;
    public final static int HasSenderDeviceUUID = 0x0040;
    public final static int HasAppUUID = 0x0080;

    //
    // Key masks.
    //
    public final static int HasPayloadAESKeyUser = 0x0001;
    public final static int HasPayloadAESKeyVendor = 0x0002;
    public final static int HasPayloadAESKeyRegime = 0x0004;

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

    public GorillaMessage()
    {
        Magic = GorillaMagic;
        Version = (VersionV1Major << 8) + (VersionV1Minor << 0);
    }

    public GorillaMessage(int command)
    {
        this();

        Command = command;
    }

    public GorillaMessage(int command, int idsmask, int keymask, int size)
    {
        this(command);

        Idsmask = idsmask;
        Keymask = keymask;

        Size = size;

        if ((Idsmask & HasRSASignature) != 0)
        {
            Size += GorillaRSASignSize;
        }

        if ((Idsmask & HasSHASignature) != 0)
        {
            Size += GorillaSHASignSize;
        }
    }

    public byte[] marshall()
    {
        Head = new byte[ GorillaHeaderSize ];

        // @formatter:off
        Head[  0 ] = (byte) ((Magic   >> 24) & 0xff);
        Head[  1 ] = (byte) ((Magic   >> 16) & 0xff);
        Head[  2 ] = (byte) ((Magic   >>  8) & 0xff);
        Head[  3 ] = (byte) ((Magic   >>  0) & 0xff);

        Head[  4 ] = (byte) ((Version >>  8) & 0xff);
        Head[  5 ] = (byte) ((Version >>  0) & 0xff);

        Head[  6 ] = (byte) ((Command >>  8) & 0xff);
        Head[  7 ] = (byte) ((Command >>  0) & 0xff);

        Head[  8 ] = (byte) ((Idsmask >>  8) & 0xff);
        Head[  9 ] = (byte) ((Idsmask >>  0) & 0xff);

        Head[ 10 ] = (byte) ((Keymask >>  8) & 0xff);
        Head[ 11 ] = (byte) ((Keymask >>  0) & 0xff);

        Head[ 12 ] = (byte) ((Size    >> 24) & 0xff);
        Head[ 13 ] = (byte) ((Size    >> 16) & 0xff);
        Head[ 14 ] = (byte) ((Size    >>  8) & 0xff);
        Head[ 15 ] = (byte) ((Size    >>  0) & 0xff);
        // @formatter:on

        return Head;
    }

    public GorillaMessage unmarshall(byte[] bytes)
    {
        if (bytes == null) return null;

        Head = bytes;

        Magic = ((Head[ 0 ] & 0xff) << 24) + ((Head[ 1 ] & 0xff) << 16) + ((Head[ 2 ] & 0xff) << 8) + ((Head[ 3 ] & 0xff) << 0);

        // @formatter:off
        Version = ((Head[  4 ] & 0xff) << 8) + ((Head[  5 ] & 0xff) << 0);
        Command = ((Head[  6 ] & 0xff) << 8) + ((Head[  7 ] & 0xff) << 0);
        Idsmask = ((Head[  8 ] & 0xff) << 8) + ((Head[  9 ] & 0xff) << 0);
        Keymask = ((Head[ 10 ] & 0xff) << 8) + ((Head[ 11 ] & 0xff) << 0);
        // @formatter:on

        Size = ((Head[ 12 ] & 0xff) << 24) + ((Head[ 13 ] & 0xff) << 16) + ((Head[ 14 ] & 0xff) << 8) + ((Head[ 15 ] & 0xff) << 0);

        return this;
    }
}
