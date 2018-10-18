package com.aura.aosp.aura.common.sockets;

import android.support.annotation.Nullable;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;
import java.net.SocketTimeoutException;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

public class Connect
{
    private final String addr;
    private final int port;

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

            return null;
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
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

            return null;
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
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
                    //
                    // Real bullshit implementation of InputStream.read:
                    //
                    // DOES NOT throw execption on broken pipe but returns -1.
                    //

                    int xfer = input.read(buffer, offset, size - offset);

                    if (xfer < 0)
                    {
                        Err.errp("broken pipe");
                        return null;
                    }

                    offset += xfer;

                    if (offset == size) break;
                }
                catch (SocketTimeoutException ignore)
                {
                }
                catch (Exception ex)
                {
                    Err.errp(ex);
                    return null;
                }
            }

            return buffer;
        }
        catch (Exception ex)
        {
            disconnect();

            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public Err writeConnect(byte[] buffer)
    {
        if (buffer == null)
        {
            return Err.errp();
        }

        synchronized (mutex)
        {
            try
            {
                output.write(buffer);

                Log.d("size=%d", buffer.length);

                return null;
            }
            catch (Exception ex)
            {
                disconnect();

                return Err.errp(ex);
            }
        }
    }
}
