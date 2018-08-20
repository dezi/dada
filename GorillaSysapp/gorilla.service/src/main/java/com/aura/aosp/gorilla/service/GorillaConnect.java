package com.aura.aosp.gorilla.service;

import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GorillaConnect
{
    private static final String LOGTAG = GorillaConnect.class.getSimpleName();

    public String addr;
    public int port;
    public Socket socket;
    public InputStream input;
    public OutputStream output;

    public GorillaConnect(String addr, int port)
    {
        this.addr = addr;
        this.port = port;
    }

    public boolean connect()
    {
        try
        {
            Log.d(LOGTAG, "connect: try addr=" + addr + " port=" + port);

            socket = new Socket(addr, port);
            socket.setTcpNoDelay(true);

            Log.d(LOGTAG, "connect: open socket done.");

            input = socket.getInputStream();
            output = socket.getOutputStream();

            return true;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, "readClientNodesFromGorilla: ex=" + ex.toString());
        }

        return false;
    }

    public boolean disconnect()
    {
        if (socket == null) return true;

        try
        {
            input.close();
            input = null;

            output.close();
            output = null;

            socket.close();
            socket = null;

            return true;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, "disconnect: ex=" + ex.toString());
        }

        return false;
    }
}
