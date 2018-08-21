package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.crypter.RSA;
import com.aura.aosp.aura.simple.Simple;
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

        GorillaMessage message = new GorillaMessage().unmarshall(buffer);

        if ((message.Command != GorillaMessage.MsgAuthSndNodes) &&
                (message.Size > GorillaMessage.GorillaMaxSize))
        {
            return null;
        }

        byte[] payload = session.readSession(message.Size);
        if (payload == null) return null;

        if ((message.Idsmask & GorillaMessage.HasRSASignature) != 0)
        {

            message.Sign = Simple.sliceBytes(payload, 0, 256);
            message.Base = Simple.sliceBytes(payload, 256);
        }
        else
        {
            if ((message.Idsmask & GorillaMessage.HasSHASignature) != 0)
            {
                message.Sign = Simple.sliceBytes(payload, 0, 32);
                message.Base = Simple.sliceBytes(payload, 32);
            }
            else
            {
                message.Base = payload;
            }
        }

        return message;
    }

    private boolean sendAuthRequest()
    {
        GorillaMessage packet = new GorillaMessage(GorillaMessage.MsgAuthRequest);

        return session.writeSession(packet.marshall());
    }

    private boolean sendAuthReqNodes()
    {
        GorillaMessage packet = new GorillaMessage(GorillaMessage.MsgAuthRequest);

        return session.writeSession(packet.marshall());
    }

    private boolean chAuthChallenge(GorillaMessage message)
    {
        Log.d("...");

        if (message.Base.length < GorillaMessage.GorillaChallengeSize)
        {
            return false;
        }

        //
        // Disassemble message.
        //

        byte[] challenge = Simple.sliceBytes(message.Base, 0, GorillaMessage.GorillaChallengeSize);
        byte[] publickey = Simple.sliceBytes(message.Base, GorillaMessage.GorillaChallengeSize);

        session.PeerPublicKey = RSA.unmarshalRSAPublicKey(publickey);

        if (session.PeerPublicKey == null)
        {
            Log.e("Parse public key fail!");

            return false;
        }

        //
        // Todo: Verify certificate of server. If not verified, return error and drop connect.
        //

        //
        // Verify servers signature.
        //

        boolean ok = RSA.verifyRSASignature(session.PeerPublicKey, message.Sign, message.Head, message.Base);

        if (!ok)
        {
            Log.e("Signature fail!");

            return false;
        }

        Log.d("Signature ok!");

        //
        // Create random AES key and cipher.
        //

        session.AESKey = RND.randomBytes(GorillaMessage.GorillaAESKeySize);
        //session.AESBlock = crypter.AESNewCipher(session.AESKey);

        //
        // Encrypt challenge, AES key and user UUIDs into RSA block.
        //

        byte[] plain = Simple.concatBuffers(challenge, session.AESKey, session.UserUUID, session.DeviceUUID);
        byte[] crypt = RSA.encodeRSABuffer(session.PeerPublicKey, plain);

        if (crypt == null) return false;

        //
        // Assemble response packet.
        //

        byte[] head = new GorillaMessage(GorillaMessage.MsgAuthSolved, GorillaMessage.HasRSASignature, 0, crypt.length).marshall();

        byte[] sign = RSA.createRSASignature(session.ClientPrivKey, head, crypt);
        if (sign == null) return false;

        byte[] packet = Simple.concatBuffers(head, sign, crypt);

        return session.writeSession(packet);
    }

    private boolean chAuthAccepted(GorillaMessage message)
    {
        Log.d("...");

        return false;
    }

    private boolean chAuthSndNodes(GorillaMessage message)
    {
        Log.d("...");

        return false;
    }

    private boolean chMessageDownload(GorillaMessage message)
    {
        Log.d("...");

        return false;
    }

    private boolean chGotGotelloAmt(GorillaMessage message)
    {
        return false;
    }
}
