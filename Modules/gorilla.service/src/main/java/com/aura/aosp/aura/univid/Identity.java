package com.aura.aosp.aura.univid;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.crypter.RSA;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;

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

    @NonNull
    public String getNick()
    {
        return nick;
    }

    @NonNull
    public byte[] getUserUUID()
    {
        return userUUID;
    }

    @NonNull
    public String getUserUUIDBase64()
    {
        return Simple.encodeBase64(userUUID);
    }

    @NonNull
    public byte[] getDeviceUUID()
    {
        return deviceUUID;
    }

    @NonNull
    public String getDeviceUUIDBase64()
    {
        return Simple.encodeBase64(deviceUUID);
    }

    @NonNull
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

    @NonNull
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
