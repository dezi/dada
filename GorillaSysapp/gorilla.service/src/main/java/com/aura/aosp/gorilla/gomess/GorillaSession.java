package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.AES;
import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;
import com.aura.aosp.aura.sockets.Connect;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.net.SocketTimeoutException;

public class GorillaSession
{
    public byte[] UUID;

    public Connect conn;
    public int Version;
    public final Object mutex = new Object();

    public byte[] UserUUID;
    public byte[] DeviceUUID;

    private boolean isConnected;
    private boolean isClosed;

    public byte[] AESKey;
    public AES.Block AESBlock;

    public byte[] Challenge;

    public RSAPublicKey PeerPublicKey;
    public RSAPrivateKey ClientPrivKey;

    public GorillaSession(Connect conn)
    {
        this.conn = conn;
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

    public boolean IsClosed()
    {
        return isClosed;
    }

    public void SetIsClosed(boolean closed)
    {
        isClosed = closed;
    }

    public boolean IsConnected()
    {
        return isConnected;
    }

    public void SetIsConnected(boolean connected)
    {
        isConnected = connected;
    }
}
