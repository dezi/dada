package com.aura.aosp.aura.univid;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.RSA;
import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Simple;

import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

public class Identity
{
    private String nick;
    private String country;

    private byte[] userUUID;
    private byte[] deviceUUID;
    private byte[] pubkey;
    private byte[] privKey;

    public Identity(String nick, String country, byte[] userUUID, byte[] deviceUUID, byte[] pubkey, byte[] privKey)
    {
        this.nick = nick;
        this.country = country;
        this.userUUID = userUUID;
        this.deviceUUID = deviceUUID;
        this.privKey = privKey;
        this.pubkey = pubkey;
    }

    public Identity(String nick, String country, String userUUID, String deviceUUID, String pubkey, String privKey)
    {
        this(nick, country,
                Simple.decodeBase64(userUUID),
                Simple.decodeBase64(deviceUUID),
                Simple.decodeBase64(pubkey),
                Simple.decodeBase64(privKey));
    }

    @Nullable
    public String getNick()
    {
        return nick;
    }

    @Nullable
    public byte[] getUserUUID()
    {
        return userUUID;
    }

    @Nullable
    public String getUserUUIDBase64()
    {
        return Simple.encodeBase64(userUUID);
    }

    @Nullable
    public byte[] getDeviceUUID()
    {
        return deviceUUID;
    }

    public String getDeviceUUIDBase64()
    {
        return Simple.encodeBase64(deviceUUID);
    }

    @Nullable
    public RSAPublicKey getRSAPublicKey()
    {
        try
        {
            return RSA.unmarshalRSAPublicKey(pubkey);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public RSAPrivateKey getRSAPrivateKey()
    {
        try
        {
            return RSA.unmarshalRSAPrivateKey(privKey);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }
}
