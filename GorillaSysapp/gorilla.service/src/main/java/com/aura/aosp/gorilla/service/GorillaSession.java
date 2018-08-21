package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.AES;
import com.aura.aosp.aura.simple.Log;

import java.net.SocketTimeoutException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

public class GorillaSession
{
    public byte[] UUID;

    public GorillaConnect conn;
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

    public GorillaSession(GorillaConnect conn)
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
        try
        {
            byte[] buffer = new byte[size];

            int offset = 0;

            while (offset < size)
            {
                try
                {
                    conn.socket.setSoTimeout(1000 * 1000);
                    int xfer = conn.input.read(buffer, offset, size - offset);

                    offset += xfer;

                    if (offset == size) break;
                }
                catch (SocketTimeoutException ignore)
                {
                }
            }

            return buffer;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            close();
        }

        return null;
    }

    public boolean writeSession(byte[] buffer)
    {
        synchronized (mutex)
        {
            try
            {
                Log.d("size=%d", buffer.length);

                conn.socket.setSoTimeout(5 * 1000);
                conn.output.write(buffer);

                return true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                close();
            }
        }

        return false;
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
