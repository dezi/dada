package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.crypter.AES;
import com.aura.aosp.aura.crypter.GZP;
import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.crypter.RSA;
import com.aura.aosp.aura.crypter.SHA;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Log;

import org.json.JSONArray;

import java.util.List;

public class GomessClient
{
    private GorillaSession session;
    private boolean boot;

    private List<GorillaNodes.ClientNode> availableNodes;

    public GomessClient(GorillaSession session, boolean boot)
    {
        this.session = session;
        this.boot = boot;
    }

    public List<GorillaNodes.ClientNode> getAvailableClientNodes()
    {
        return availableNodes;
    }

    public Err clientHandler()
    {
        Err err = clientHandlerBody();

        session.close();

        return err;
    }

    private Err clientHandlerBody()
    {
        Err err = sendAuthRequest();
        if (err != null) return err;

        while (true)
        {
            GorillaMessage message = readMessage();
            if (message == null) return Err.getLastErr();

            err = handleClientMessage(message);
            if (err != null) return err;

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

                    err = sendAuthReqNodes();
                    if (err != null) return err;
                }

                if (message.Command == GorillaMessage.MsgAuthSndNodes)
                {
                    Log.d("MsgAuthSndNodes...");

                    break;
                }
            }
        }

        return null;
    }

    private Err handleClientMessage(GorillaMessage message)
    {
        Err err = null;

        switch (message.Command)
        {
            case GorillaMessage.MsgAuthChallenge:
                err = chAuthChallenge(message);
                break;

            case GorillaMessage.MsgAuthAccepted:
                err = chAuthAccepted(message);
                break;

            case GorillaMessage.MsgAuthSndNodes:
                err = chAuthSndNodes(message);
                break;

            case GorillaMessage.MsgMessageDownload:
                err = chMessageDownload(message);
                break;

            case GorillaMessage.MsgGotGotelloAmt:
                err = chGotGotelloAmt(message);
                break;
        }

        return err;
    }

    @Nullable
    private GorillaMessage readMessage()
    {
        byte[] buffer = session.readSession(GorillaMessage.GorillaHeaderSize);
        if (buffer == null) return null;

        GorillaMessage message = new GorillaMessage();

        if (! message.unmarshall(buffer))
        {
            Err.errp("unmarshall fail!");

            return null;
        }

        if ((message.Size < 0) ||
                ((message.Command != GorillaMessage.MsgAuthSndNodes) &&
                (message.Size > GorillaMessage.GorillaMaxSize)))
        {
            Err.errp("size=%d fail!", message.Size);

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

    private Err sendAuthRequest()
    {
        GorillaMessage packet = new GorillaMessage(GorillaMessage.MsgAuthRequest);

        return session.writeSession(packet.marshall());
    }

    private Err sendAuthReqNodes()
    {
        byte[] head = new GorillaMessage(GorillaMessage.MsgAuthReqNodes, GorillaMessage.HasSHASignature, 0, 0).marshall();

        byte[] sign = SHA.createSHASignature(session.AESKey, head);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign);

        return session.writeSession(packet);
    }

    private Err chAuthChallenge(GorillaMessage message)
    {
        Log.d("...");

        if (message.Base.length < GorillaMessage.GorillaChallengeSize)
        {
            return Err.errp("junk message!", message.Size);
        }

        //
        // Disassemble message.
        //

        byte[] challenge = Simple.sliceBytes(message.Base, 0, GorillaMessage.GorillaChallengeSize);
        byte[] publickey = Simple.sliceBytes(message.Base, GorillaMessage.GorillaChallengeSize);

        session.PeerPublicKey = RSA.unmarshalRSAPublicKey(publickey);
        if (session.PeerPublicKey == null) return Err.getLastErr();

        //
        // Todo: Verify certificate of server. If not verified, return error and drop connect.
        //

        //
        // Verify servers signature.
        //

        Err err = RSA.verifyRSASignature(session.PeerPublicKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

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
        if (crypt == null) return Err.getLastErr();

        //
        // Assemble response packet.
        //

        byte[] head = new GorillaMessage(GorillaMessage.MsgAuthSolved, GorillaMessage.HasRSASignature, 0, crypt.length).marshall();

        byte[] sign = RSA.createRSASignature(session.ClientPrivKey, head, crypt);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign, crypt);

        return session.writeSession(packet);
    }

    private Err chAuthAccepted(GorillaMessage message)
    {
        Log.d("....");

        Err err = RSA.verifyRSASignature(session.PeerPublicKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

        Log.d("Connected!");

        //
        // We are finally connected now.
        //

        session.SetIsConnected(true);

        return null;
    }

    private Err chAuthSndNodes(GorillaMessage message)
    {
        Log.d("...");

        Err err = SHA.verifySHASignature(session.AESKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

        Log.d("Received nodes!");

        byte[] nodesBytes = GZP.unGzip(message.Base);
        if (nodesBytes == null) return Err.getLastErr();

        JSONArray jClientNodes = Json.fromStringArray(new String(nodesBytes));

        availableNodes = GorillaNodes.ClientNode.unmarshall(jClientNodes);

        Log.d("nodes=%s", new String(nodesBytes));
        Log.d("nodes=%s", Json.toPretty(jClientNodes));

        return null;
    }

    private Err chMessageDownload(GorillaMessage message)
    {
        Log.d("...");

        return null;
    }

    private Err chGotGotelloAmt(GorillaMessage message)
    {
        return null;
    }
}
