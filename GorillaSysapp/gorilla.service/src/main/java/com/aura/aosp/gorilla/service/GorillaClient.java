package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.AES;
import com.aura.aosp.aura.crypter.GZP;
import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.crypter.RSA;
import com.aura.aosp.aura.crypter.SHA;
import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.simple.Log;

import org.json.JSONArray;

import java.util.List;

public class GorillaClient
{
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

    public List<GorillaNodes.ClientNode> getAvailableClientNodes()
    {
        return availableNodes;
    }

    private GorillaSession session;
    private boolean boot;

    private List<GorillaNodes.ClientNode> availableNodes;

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

        return ok;
    }

    @Nullable
    private GorillaMessage readMessage()
    {
        byte[] buffer = session.readSession(GorillaMessage.GorillaHeaderSize);
        if (buffer == null) return null;

        GorillaMessage message = new GorillaMessage();

        if (! message.unmarshall(buffer))
        {
            Log.e("Unmarshall fail!");

            return null;
        }

        if (message.Size < 0)
        {
            Log.e("size=%d fail!", message.Size);

            return null;
        }

        if ((message.Command != GorillaMessage.MsgAuthSndNodes) &&
                (message.Size > GorillaMessage.GorillaMaxSize))
        {
            return null;
        }

        byte[] payload = session.readSession(message.Size);
        if (payload == null) return null;

        if ((message.Idsmask & GorillaMessage.HasRSASignature) != 0)
        {

            message.Sign = Simple.sliceBytes(payload, 0, GorillaMessage.GorillaRSASignSize);
            message.Base = Simple.sliceBytes(payload, GorillaMessage.GorillaRSASignSize);
        }
        else
        {
            if ((message.Idsmask & GorillaMessage.HasSHASignature) != 0)
            {
                message.Sign = Simple.sliceBytes(payload, 0, GorillaMessage.GorillaSHASignSize);
                message.Base = Simple.sliceBytes(payload, GorillaMessage.GorillaSHASignSize);
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
        byte[] head = new GorillaMessage(GorillaMessage.MsgAuthReqNodes, GorillaMessage.HasSHASignature, 0, 0).marshall();

        byte[] sign = SHA.createSHASignature(session.AESKey, head);
        if (sign == null) return false;

        byte[] packet = Simple.concatBuffers(head, sign);

        return session.writeSession(packet);
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
        session.AESBlock = AES.newAESCipher(session.AESKey);

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
        Log.d("....");

        boolean ok = RSA.verifyRSASignature(session.PeerPublicKey, message.Sign, message.Head, message.Base);

        if (!ok)
        {
            Log.e("Signature fail!");

            return false;
        }

        Log.d("Connected!");

        //
        // We are finally connected now.
        //

        session.SetIsConnected(true);

        return true;
    }

    private boolean chAuthSndNodes(GorillaMessage message)
    {
        Log.d("...");

        boolean ok = SHA.verifySHASignature(session.AESKey, message.Sign, message.Head, message.Base);

        if (!ok)
        {
            Log.e("Signature fail!");

            return false;
        }

        Log.d("Received nodes!");

        byte[] nodesBytes = GZP.unGzip(message.Base);

        if (nodesBytes == null)
        {
            Log.e("Unzip fail!");

            return false;
        }

        JSONArray jClientNodes = Json.fromStringArray(new String(nodesBytes));

        availableNodes = GorillaNodes.ClientNode.unmarshall(jClientNodes);

        Log.d("nodes=%s", new String(nodesBytes));
        Log.d("nodes=%s", Json.toPretty(jClientNodes));

        return true;
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
