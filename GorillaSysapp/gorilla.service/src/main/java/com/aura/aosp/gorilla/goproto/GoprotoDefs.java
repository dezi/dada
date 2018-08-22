package com.aura.aosp.gorilla.goproto;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.simple.Err;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GoprotoDefs
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
}
