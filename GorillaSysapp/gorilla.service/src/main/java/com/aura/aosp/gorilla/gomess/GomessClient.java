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
import com.aura.aosp.gorilla.goproto.GoprotoMessage;
import com.aura.aosp.gorilla.gorilla.GorillaNodes;
import com.aura.aosp.gorilla.goproto.GoprotoSession;

import org.json.JSONArray;

import java.util.List;

public class GomessClient
{
    private GoprotoSession session;
    private boolean boot;

    private List<GorillaNodes.ClientNode> availableNodes;

    public GomessClient(GoprotoSession session, boolean boot)
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
            GoprotoMessage message = readMessage();
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
                if (message.Command == GoprotoMessage.MsgAuthAccepted)
                {
                    Log.d("request connect nodes...");

                    err = sendAuthReqNodes();
                    if (err != null) return err;
                }

                if (message.Command == GoprotoMessage.MsgAuthSndNodes)
                {
                    Log.d("received connect nodes...");

                    break;
                }
            }
        }

        return null;
    }

    private Err handleClientMessage(GoprotoMessage message)
    {
        Err err = null;

        switch (message.Command)
        {
            case GoprotoMessage.MsgAuthChallenge:
                err = chAuthChallenge(message);
                break;

            case GoprotoMessage.MsgAuthAccepted:
                err = chAuthAccepted(message);
                break;

            case GoprotoMessage.MsgAuthSndNodes:
                err = chAuthSndNodes(message);
                break;

            case GoprotoMessage.MsgMessageDownload:
                err = chMessageDownload(message);
                break;

            case GoprotoMessage.MsgGotGotelloAmt:
                err = chGotGotelloAmt(message);
                break;
        }

        return err;
    }

    @Nullable
    private GoprotoMessage readMessage()
    {
        byte[] buffer = session.readSession(GoprotoMessage.GorillaHeaderSize);
        if (buffer == null) return null;

        GoprotoMessage message = new GoprotoMessage();

        Err err = message.unmarshall(buffer);
        if (err != null) return null;

        if ((message.Size < 0) ||
                ((message.Command != GoprotoMessage.MsgAuthSndNodes) &&
                (message.Size > GoprotoMessage.GorillaMaxSize)))
        {
            Err.errp("size=%d fail!", message.Size);

            return null;
        }

        byte[] payload = session.readSession(message.Size);
        if (payload == null) return null;

        if ((message.Idsmask & GoprotoMessage.HasRSASignature) != 0)
        {
            message.Sign = Simple.sliceBytes(payload, 0, GoprotoMessage.GorillaRSASignSize);
            message.Base = Simple.sliceBytes(payload, GoprotoMessage.GorillaRSASignSize);
        }
        else
        {
            if ((message.Idsmask & GoprotoMessage.HasSHASignature) != 0)
            {
                message.Sign = Simple.sliceBytes(payload, 0, GoprotoMessage.GorillaSHASignSize);
                message.Base = Simple.sliceBytes(payload, GoprotoMessage.GorillaSHASignSize);
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
        GoprotoMessage packet = new GoprotoMessage(GoprotoMessage.MsgAuthRequest);

        return session.writeSession(packet.marshall());
    }

    private Err sendAuthReqNodes()
    {
        byte[] head = new GoprotoMessage(GoprotoMessage.MsgAuthReqNodes, GoprotoMessage.HasSHASignature, 0, 0).marshall();

        byte[] sign = SHA.createSHASignature(session.AESKey, head);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign);

        return session.writeSession(packet);
    }

    private Err chAuthChallenge(GoprotoMessage message)
    {
        Log.d("...");

        if (message.Base.length < GoprotoMessage.GorillaChallengeSize)
        {
            return Err.errp("junk message!", message.Size);
        }

        //
        // Disassemble message.
        //

        byte[] challenge = Simple.sliceBytes(message.Base, 0, GoprotoMessage.GorillaChallengeSize);
        byte[] publickey = Simple.sliceBytes(message.Base, GoprotoMessage.GorillaChallengeSize);

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

        Log.d("signature ok!");

        //
        // Create random AES key and cipher.
        //

        session.AESKey = RND.randomBytes(GoprotoMessage.GorillaAESKeySize);
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

        byte[] head = new GoprotoMessage(GoprotoMessage.MsgAuthSolved, GoprotoMessage.HasRSASignature, 0, crypt.length).marshall();

        byte[] sign = RSA.createRSASignature(session.ClientPrivKey, head, crypt);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign, crypt);

        return session.writeSession(packet);
    }

    private Err chAuthAccepted(GoprotoMessage message)
    {
        Log.d("....");

        Err err = RSA.verifyRSASignature(session.PeerPublicKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

        Log.d("connected!");

        //
        // We are finally connected now.
        //

        session.SetIsConnected(true);

        return null;
    }

    private Err chAuthSndNodes(GoprotoMessage message)
    {
        Log.d("...");

        Err err = SHA.verifySHASignature(session.AESKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

        Log.d("received nodes!");

        byte[] nodesBytes = GZP.unGzip(message.Base);
        if (nodesBytes == null) return Err.getLastErr();

        JSONArray jClientNodes = Json.fromStringArray(new String(nodesBytes));

        availableNodes = GorillaNodes.ClientNode.unmarshall(jClientNodes);

        Log.d("nodes=%s", new String(nodesBytes));
        Log.d("nodes=%s", Json.toPretty(jClientNodes));

        return null;
    }

    private Err chMessageDownload(GoprotoMessage message)
    {
        Log.d("...");

        return null;
    }

    private Err chGotGotelloAmt(GoprotoMessage message)
    {
        return null;
    }
}
