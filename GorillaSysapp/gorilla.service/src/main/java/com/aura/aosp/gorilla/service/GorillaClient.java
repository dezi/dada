package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.simple.Log;

public class GorillaClient
{
    private GorillaSession session;
    private boolean boot;

    public GorillaClient(GorillaSession session, boolean boot)
    {
        this.session = session;
        this.boot = boot;
    }

    public void clientHandler()
    {
        clientHandlerBody();

        session.close();
    }

    private void clientHandlerBody()
    {
        boolean ok = sendAuthRequest();
        if (!ok) return;

        while (true)
        {
            GorillaMessage message = readMessage();
            if (message == null) break;

            ok = handleClientMessage(message);
            if (!ok) break;

            //
            // Boot part.
            //
            // Request all nodes and exit.
            //

            if (boot)
            {
                if (message.Command == GorillaMessage.MsgAuthAccepted)
                {
                    Log.d("MsgAuthReqNodes...");

                    ok = sendAuthReqNodes();
                    if (!ok) break;
                }

                if (message.Command == GorillaMessage.MsgAuthSndNodes)
                {
                    Log.d("MsgAuthSndNodes...");

                    break;
                }
            }
        }
    }

    private boolean handleClientMessage(GorillaMessage message)
    {
        boolean ok = false;

        Log.d("command=0x%04x size=%d", message.Command, message.Size);

        switch (message.Command)
        {
            case GorillaMessage.MsgAuthChallenge:
                ok = chAuthChallenge(message);
                break;

            case GorillaMessage.MsgAuthAccepted:
                ok = chAuthAccepted(message);
                break;

            case GorillaMessage.MsgAuthSndNodes:
                ok = chAuthSndNodes(message);
                break;

            case GorillaMessage.MsgMessageDownload:
                ok = chMessageDownload(message);
                break;

            case GorillaMessage.MsgGotGotelloAmt:
                ok = chGotGotelloAmt(message);
                break;
        }

        return false;
    }

    @Nullable
    private GorillaMessage readMessage()
    {
        byte[] buffer = session.readSession(GorillaMessage.GorillaHeaderSize);
        if (buffer == null) return null;

        return new GorillaMessage().UnMarshall(buffer);
    }

    private boolean sendAuthRequest()
    {
        GorillaMessage packet = new GorillaMessage(GorillaMessage.MsgAuthRequest);

        return session.writeSession(packet.Marshall());
    }

    private boolean sendAuthReqNodes()
    {
        GorillaMessage packet = new GorillaMessage(GorillaMessage.MsgAuthRequest);

        return session.writeSession(packet.Marshall());
    }

    private boolean chAuthChallenge(GorillaMessage message)
    {
        return false;
    }

    private boolean chAuthAccepted(GorillaMessage message)
    {
        return false;
    }

    private boolean chAuthSndNodes(GorillaMessage message)
    {
        return false;
    }

    private boolean chMessageDownload(GorillaMessage message)
    {
        return false;
    }

    private boolean chGotGotelloAmt(GorillaMessage message)
    {
        return false;
    }
}
