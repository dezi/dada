package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.AES;
import com.aura.aosp.aura.common.crypter.GZP;
import com.aura.aosp.aura.common.crypter.RND;
import com.aura.aosp.aura.common.crypter.RSA;
import com.aura.aosp.aura.common.crypter.SHA;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.gorilla.goproto.GoprotoDefs;
import com.aura.aosp.gorilla.goproto.GoprotoMessage;
import com.aura.aosp.gorilla.goproto.GoprotoSession;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;
import com.aura.aosp.gorilla.service.GorillaSender;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GomessClient
{
    private GoprotoSession session;

    private List<GomessNode> availableNodes;

    public GomessClient(GoprotoSession session)
    {
        this.session = session;
    }

    public void disconnect()
    {
        session.close();
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

    public boolean isConnected()
    {
        return session.isConnected();
    }

    public Err clientHandler()
    {
        Err err = clientHandlerBody();

        session.close();

        GorillaSender.sendBroadCastOnlineStatus(false);

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

        byte[] routingCrypt = ticket.marshalRouting(session.getAESBlock());
        if (routingCrypt == null) return Err.getLastErr();

        //
        // Assemble response packet.
        //

        int llen = routingCrypt.length + ticket.getPayload().length;

        byte[] head = new GoprotoMessage(GoprotoDefs.MsgMessageUpload, ticket.getIdsmask() | GoprotoDefs.HasSHASignature, 0, llen).marshall();

        byte[] sign = SHA.createSHASignature(session.AESKey, head, routingCrypt, ticket.getPayload());
        if (sign == null) return Err.getLastErr();

        byte[] packet = Simple.concatBuffers(head, sign, routingCrypt, ticket.getPayload());
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

        if (! session.isBoot())
        {
            GorillaSender.sendBroadCastOnlineStatus(true);
        }

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

        Err err = SHA.verifySHASignature(session.AESKey, message.Sign, message.Head, message.Base);
        if (err != null) return err;

        Log.d("received message!");

        Log.d("head=%s", Simple.getHexBytesToString(message.Head));

        GoprotoTicket ticket = new GoprotoTicket();
        ticket.setIdsmask(message.Idsmask);

        err = ticket.unMarshalCrypted(session.AESBlock, message.Base);
        if (err != null) return err;

        ticket.dumpTicket();

        Integer status = ticket.getStatus();

        if ((status != null) && (status != 0))
        {
            //
            // Ticket is a status reply.
            //
            // Generate a result an send to client.
            //

            JSONObject result = ticket.getTicketResult();
            if (result == null) return Err.getLastErr();

            return GorillaSender.sendPayloadResult(ticket, result);
        }

        //
        // Ticket is an original message.
        //

        Log.d("payload=%s", new String(ticket.getPayload()));

        err = GorillaSender.sendPayload(ticket);
        if (err != null) return err;

        GoprotoTicket statusTicket = ticket.prepareStatus(GoprotoDefs.MsgStatusReceived);

        statusTicket.dumpTicket();

        err = sendMessageUpload(statusTicket);

        Log.d("##############status received send.");

        return err;
    }

    private Err chGotGotelloAmt(GoprotoMessage message)
    {
        return null;
    }
}
