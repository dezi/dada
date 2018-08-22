package com.aura.aosp.aura.sockets;

import android.support.annotation.Nullable;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;

public class Connect
{
    private String addr;
    private int port;
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private final Object mutex = new Object();

    public Connect(String addr, int port)
    {
        this.addr = addr;
        this.port = port;
    }

    @Nullable
    public Err connect()
    {
        disconnect();

        try
        {
            Log.d("try addr=" + addr + " port=" + port);

            socket = new Socket(addr, port);
            socket.setSoTimeout(5 * 1000);
            socket.setTcpNoDelay(true);

            Log.d("socket open...");

            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }

        return null;
    }

    @Nullable
    public Err disconnect()
    {
        try
        {
            if (input != null)
            {
                input.close();
                input = null;
            }

            if (output != null)
            {
                output.close();
                output = null;
            }

            if (socket != null)
            {
                socket.close();
                socket = null;
            }
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }

        return null;
    }

    @Nullable
    public byte[] readConnect(int size)
    {
        try
        {
            byte[] buffer = new byte[size];

            int offset = 0;

            while (offset < size)
            {
                try
                {
                    int xfer = input.read(buffer, offset, size - offset);

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
            disconnect();

            Err.errp(ex);
        }

        return null;
    }

    @Nullable
    public Err writeConnect(byte[] buffer)
    {
        synchronized (mutex)
        {
            try
            {
                com.aura.aosp.aura.simple.Log.d("size=%d", buffer.length);

                output.write(buffer);
            }
            catch (Exception ex)
            {
                disconnect();

                return Err.errp(ex);
            }
        }

        return null;
    }
}
