package com.aura.aosp.gorilla.gomess;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.sockets.Connect;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.gorilla.goproto.GoprotoDefs;
import com.aura.aosp.gorilla.goproto.GoprotoMetadata;
import com.aura.aosp.gorilla.goproto.GoprotoSession;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.service.GorillaMapper;
import com.aura.aosp.gorilla.service.GorillaSender;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class GomessHandler
{
    private static GomessHandler instance;
    private static final Object mutex = new Object();

    public static GomessHandler getInstance()
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GomessHandler();
                }
            }
        }

        return instance;
    }

    private GomessClient client;

    private final Thread readerThread;
    private final Thread writerThread;

    private final List<GoprotoTicket> tickets = new ArrayList<>();

    private GomessHandler()
    {
        readerThread = new Thread(readerThreadRunner);
        readerThread.start();

        writerThread = new Thread(writerThreadRunner);
        writerThread.start();
    }

    public JSONObject sendPayload(String apkname, String userUUID, String deviceUUID, String payload)
    {
        Log.d("user=" + userUUID + " dev=" + deviceUUID);
        Log.d("apkname=" + apkname + " payload=" + payload);

        JSONObject result = new JSONObject();

        if ((apkname == null) || (userUUID == null) || (deviceUUID == null) || (payload == null))
        {
            Json.put(result, "error", "Request parameters missing");
            Json.put(result, "status", "error");

            return result;
        }

        Identity owner = Owner.getOwnerIdentity();

        if (owner == null)
        {
            Json.put(result, "error", "Device owner not set");
            Json.put(result, "status", "error");

            return result;
        }

        String uuid = UID.randomUUIDBase64();
        long time = System.currentTimeMillis();
        Log.d("uuid=" + uuid + " time=" + time);

        Json.put(result, "uuid", uuid);
        Json.put(result, "time", time);

        GoprotoMetadata metadata = new GoprotoMetadata();
        metadata.setTimeStamp(time);

        GoprotoTicket ticket = new GoprotoTicket();

        ticket.setMessageUUID(Simple.decodeBase64(uuid));
        ticket.setMetadata(metadata);

        ticket.setReceiverUserUUID(Simple.decodeBase64(userUUID));
        ticket.setReceiverDeviceUUID(Simple.decodeBase64(deviceUUID));

        ticket.setAppUUID(Simple.decodeBase64(GorillaMapper.mapAPK2UUID(apkname)));

        ticket.setPayload(payload.getBytes());

        addTicketToQueue(ticket);

        Json.put(result, "status", "queued");

        return result;
    }

    public boolean sendPayloadRead(String apkname, String userUUID, String deviceUUID, String messageUUID)
    {
        Log.d("user=" + userUUID + " dev=" + deviceUUID);
        Log.d("apkname=" + apkname + " messageUUID=" + messageUUID);

        JSONObject result = new JSONObject();

        if ((apkname == null) || (userUUID == null) || (deviceUUID == null) || (messageUUID == null))
        {
            return false;
        }

        Identity owner = Owner.getOwnerIdentity();

        if (owner == null)
        {
            Json.put(result, "error", "Device owner not set");
            Json.put(result, "status", "error");

            return false;
        }

        GoprotoMetadata metadata = new GoprotoMetadata();
        metadata.setTimeStamp(System.currentTimeMillis());
        metadata.setStatus(GoprotoDefs.MsgStatusRead);

        GoprotoTicket ticket = new GoprotoTicket();

        ticket.setMetadata(metadata);

        ticket.setMessageUUID(Simple.decodeBase64(messageUUID));
        ticket.setReceiverUserUUID(Simple.decodeBase64(userUUID));
        ticket.setReceiverDeviceUUID(Simple.decodeBase64(deviceUUID));

        ticket.setAppUUID(Simple.decodeBase64(GorillaMapper.mapAPK2UUID(apkname)));

        ticket.setPayload(new byte[0]);

        addTicketToQueue(ticket);

        return true;
    }

    public void killSession()
    {
        if (client != null)
        {
            Log.d("kill starting...");

            client.disconnect();

            Log.d("kill done.");
        }
    }

    public void changeOwner()
    {
        GorillaSender.sendBroadCastOwnerUUID(Owner.getOwnerUUIDBase64());
    }

    public boolean isSessionConnected()
    {
        GomessClient myclient = client;

        return (myclient != null) && myclient.isConnected();
    }

    private void addTicketToQueue(GoprotoTicket ticket)
    {
        synchronized (tickets)
        {
            tickets.add(ticket);
        }
    }

    private final Runnable readerThreadRunner = new Runnable()
    {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run()
        {
            while (true)
            {
                Simple.sleep(1000);

                Identity owner = Owner.getOwnerIdentity();

                if (owner == null)
                {
                    Log.e("owner not defined!");
                    continue;
                }

                GomessNode clientNode = GomessNodes.getBestNode("DE", 53.551086, 9.993682);

                if (clientNode == null)
                {
                    Log.e("gorilla nodes not defined!");
                    continue;
                }

                Err err = handleSession(clientNode);
                if (err != null) Log.e("err=%s", err);
            }
        }
    };

    private final Runnable writerThreadRunner = new Runnable()
    {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run()
        {
            while (true)
            {
                //
                // Draw a detached copy of client instance.
                //

                GomessClient myclient = client;

                //
                // Check if detached client instance copy is connected.
                //

                if ((myclient == null) || ! myclient.isConnected())
                {
                    Simple.sleep(10);

                    continue;
                }

                //
                // Try to obtain a ticket.
                //

                GoprotoTicket ticket = null;

                synchronized (tickets)
                {
                    if (tickets.size() > 0)
                    {
                        ticket = tickets.remove(0);
                    }
                }

                //
                // Nix to do.
                //

                if (ticket == null)
                {
                    Simple.sleep(10);

                    continue;
                }

                //
                // Dispatch ticket to remote gorilla server.
                //

                Err err = myclient.sendMessageUpload(ticket);

                if (err != null)
                {
                    Log.e("err=%s", err);

                    //
                    // Push back ticket to queue for next change.
                    //

                    synchronized (tickets)
                    {
                        tickets.add(0, ticket);
                    }

                    Simple.sleep(100);

                    continue;
                }

                Integer status = ticket.getStatus();

                if ((status != null) && (status != 0))
                {
                    //
                    // Message is a status message.
                    //
                    // No status is generated for status messages.
                    //

                    Log.d("message status=%04x", status);

                    continue;
                }

                //
                // Prepare result json.
                //

                ticket.setStatus(GoprotoDefs.MsgStatusSend);
                ticket.setTimeStamp(System.currentTimeMillis());

                JSONObject result = ticket.getTicketResult();

                if (result == null)
                {
                    Log.e("ticket result err=%s", Err.getLastErr());

                    continue;
                }

                err = GorillaSender.sendPayloadResult(ticket, result);

                if (err != null)
                {
                    Log.e("ticket result send err=%s", err);
                }
            }
        }
    };

    private Err handleSession(GomessNode cnode)
    {
        Log.d("...");

        Connect conn = new Connect(cnode.addr, cnode.port);

        if (conn.connect() != null)
        {
            //
            // Connection refused means server is dead.
            //

            GomessNodes.removeDeadNode("DE", cnode);

            return Err.getLastErr();
        }

        GoprotoSession session = new GoprotoSession(conn);
        Err err = session.aquireIdentity();
        if (err != null) return err;

        client = new GomessClient(session);

        err = client.clientHandler();

        client = null;

        return err;
    }
}
