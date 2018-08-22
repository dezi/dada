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

import com.aura.aosp.gorilla.goproto.GoprotoDefs;
import com.aura.aosp.gorilla.goproto.GoprotoMessage;
import com.aura.aosp.gorilla.goproto.GoprotoSession;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONArray;

import java.util.List;

public class GomessClient
{
    private GoprotoSession session;

    private List<GomessNode> availableNodes;

    public GomessClient(GoprotoSession session)
    {
        this.session = session;
    }

    public List<GomessNode> getAvailableGomessNodes()
    {
        return availableNodes;
    }

    public Err nodesHandler()
    {
        Err err = nodesHandlerBody();

        session.close();

        return err;
    }

    private Err nodesHandlerBody()
    {
        Err err = sendAuthRequest();
        if (err != null) return err;

        while (true)
        {
            GoprotoMessage message = readMessage();
            if (message == null) return Err.getLastErr();

            err = handleClientMessage(message);
            if (err != null) return err;

            if (message.Command == GoprotoDefs.MsgAuthAccepted)
            {
                Log.d("request connect nodes...");

                err = sendAuthReqNodes();
                if (err != null) return err;
            }

            if (message.Command == GoprotoDefs.MsgAuthSndNodes)
            {
                Log.d("received connect nodes...");

                break;
            }
        }

        return null;
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
        }
    }

    private Err handleClientMessage(GoprotoMessage message)
    {
        Err err = null;

        switch (message.Command)
        {
            case GoprotoDefs.MsgAuthChallenge:
                err = chAuthChallenge(message);
                break;

            case GoprotoDefs.MsgAuthAccepted:
                err = chAuthAccepted(message);
                break;

            case GoprotoDefs.MsgAuthSndNodes:
                err = chAuthSndNodes(message);
                break;

            case GoprotoDefs.MsgMessageDownload:
                err = chMessageDownload(message);
                break;

            case GoprotoDefs.MsgGotGotelloAmt:
                err = chGotGotelloAmt(message);
                break;
        }

        return err;
    }

    @Nullable
    private GoprotoMessage readMessage()
    {
        byte[] buffer = session.readSession(GoprotoDefs.GorillaHeaderSize);
        if (buffer == null) return null;

        GoprotoMessage message = new GoprotoMessage();

        Err err = message.unmarshall(buffer);
        if (err != null) return null;

        if ((message.Size < 0) ||
                ((message.Command != GoprotoDefs.MsgAuthSndNodes) &&
                (message.Size > GoprotoDefs.GorillaMaxSize)))
        {
            Err.errp("excessive size=%d fail!", message.Size);

            return null;
        }

        byte[] payload = session.readSession(message.Size);
        if (payload == null) return null;

        if ((message.Idsmask & GoprotoDefs.HasRSASignature) != 0)
        {
            message.Sign = Simple.sliceBytes(payload, 0, GoprotoDefs.GorillaRSASignSize);
            message.Base = Simple.sliceBytes(payload, GoprotoDefs.GorillaRSASignSize);
        }
        else
        {
            if ((message.Idsmask & GoprotoDefs.HasSHASignature) != 0)
            {
                message.Sign = Simple.sliceBytes(payload, 0, GoprotoDefs.GorillaSHASignSize);
                message.Base = Simple.sliceBytes(payload, GoprotoDefs.GorillaSHASignSize);
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
        GoprotoMessage packet = new GoprotoMessage(GoprotoDefs.MsgAuthRequest);

        return session.writeSession(packet.marshall());
    }

    private Err sendAuthReqNodes()
    {
        byte[] head = new GoprotoMessage(GoprotoDefs.MsgAuthReqNodes, GoprotoDefs.HasSHASignature, 0, 0).marshall();

        byte[] sign = SHA.createSHASignature(session.AESKey, head);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign);

        return session.writeSession(packet);
    }

    public Err sendMessageUpload(GoprotoTicket ticket)
    {
        ticket.dumpTicket();

        //
        // Build AES encoded message routing information.
        //

        int idsMask = GoprotoDefs.HasSHASignature
                | GoprotoDefs.HasMessageUUID
                | GoprotoDefs.HasReceiverUserUUID
                | GoprotoDefs.HasReceiverDeviceUUID
                | GoprotoDefs.HasAppUUID;

        byte[] routingPlain = Simple.concatBuffers(
                ticket.getMessageUUID(),
                ticket.getReceiverUserUUID(),
                ticket.getReceiverDeviceUUID(),
                ticket.getAppUUID());

        byte[] routingCrypt = AES.encryptAESBlock(session.getAESBlock(), routingPlain);
        if (routingCrypt == null) return Err.getLastErr();

        //
        // Assemble response packet.
        //

        int llen = routingCrypt.length + ticket.Payload.length;

        byte[] head = new GoprotoMessage(GoprotoDefs.MsgMessageUpload, idsMask, 0, llen).marshall();

        Log.d("head=%s", Simple.getHexBytesToString(head));

        byte[] sign = SHA.createSHASignature(session.AESKey, head, routingCrypt, ticket.Payload);
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign, routingCrypt, ticket.Payload);

        return session.writeSession(packet);
    }

    private Err chAuthChallenge(GoprotoMessage message)
    {
        Log.d("...");

        if (message.Base.length < GoprotoDefs.GorillaChallengeSize)
        {
            return Err.errp("junk message!", message.Size);
        }

        //
        // Disassemble message.
        //

        byte[] challenge = Simple.sliceBytes(message.Base, 0, GoprotoDefs.GorillaChallengeSize);
        byte[] publickey = Simple.sliceBytes(message.Base, GoprotoDefs.GorillaChallengeSize);

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

        session.AESKey = RND.randomBytes(GoprotoDefs.GorillaAESKeySize);
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

        byte[] head = new GoprotoMessage(GoprotoDefs.MsgAuthSolved, GoprotoDefs.HasRSASignature, 0, crypt.length).marshall();

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

        session.setIsConnected(true);

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

        availableNodes = GomessNodes.unMarshall(jClientNodes);

        Log.d("nodes=%s", new String(nodesBytes));

        return null;
    }

    private Err chMessageDownload(GoprotoMessage message)
    {
        Log.d("...");

        Log.d("head=%s", Simple.getHexBytesToString(message.Head));


        return null;
    }

    private Err chGotGotelloAmt(GoprotoMessage message)
    {
        return null;
    }
}
