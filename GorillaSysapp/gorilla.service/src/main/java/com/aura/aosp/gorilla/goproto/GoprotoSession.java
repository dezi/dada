package com.aura.aosp.gorilla.goproto;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.AES;
import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.sockets.Connect;
import com.aura.aosp.aura.univid.Owner;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class GoprotoSession
{
    public byte[] UUID;

    public Connect conn;

    public byte[] UserUUID;
    public byte[] DeviceUUID;

    private boolean isConnected;
    private boolean isClosed;

    public byte[] AESKey;
    public AES.Block AESBlock;

    public RSAPublicKey PeerPublicKey;
    public RSAPrivateKey ClientPrivKey;

    public GoprotoSession(Connect conn)
    {
        this.conn = conn;
    }

    public Err aquireIdentity()
    {
        UserUUID = Owner.getUserUUID();
        if (UserUUID == null) return Err.getLastErr();

        DeviceUUID = Owner.getDeviceUUID();
        if (DeviceUUID == null) return Err.getLastErr();

        ClientPrivKey = Owner.getRSAPrivateKey();
        if (ClientPrivKey == null) return Err.getLastErr();

        return null;
    }

    public void close()
    {
        if (conn != null)
        {
            conn.disconnect();
            conn = null;
        }

        isClosed = true;
        isConnected = false;
    }

    @Nullable
    public byte[] readSession(int size)
    {
        return conn.readConnect(size);
    }

    public Err writeSession(byte[] buffer)
    {
        return conn.writeConnect(buffer);
    }

    public boolean isClosed()
    {
        return isClosed;
    }

    public void setIsClosed(boolean closed)
    {
        isClosed = closed;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void setIsConnected(boolean connected)
    {
        isConnected = connected;
    }

    public AES.Block getAESBlock()
    {
        return AESBlock;
    }
}
