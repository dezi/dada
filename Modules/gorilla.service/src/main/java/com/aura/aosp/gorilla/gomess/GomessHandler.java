package com.aura.aosp.gorilla.gomess;

import android.os.Handler;
import android.support.annotation.NonNull;

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
import com.aura.aosp.gorilla.service.GorillaTime;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class GomessHandler
{
    private static GomessHandler instance;
    private static final Object mutex = new Object();

    public static void startService()
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
    }

    public static GomessHandler getInstance()
    {
        if (instance == null) startService();

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

    @NonNull
    public JSONObject sendPayload(String apkname, String userUUID, String deviceUUID, String payload)
    {
        Log.d("user=" + userUUID + " dev=" + deviceUUID);
        Log.d("apkname=" + apkname + " payload=" + payload);

        JSONObject result = new JSONObject();
        JSONObject load = new JSONObject();

        String uuid = UID.randomUUIDBase64();
        long time = GorillaTime.serverTimeMillis();
        Log.d("uuid=" + uuid + " time=" + time);

        Json.put(result, "uuid", uuid);
        Json.put(result, "time", time);
        Json.put(result, "load", load);

        if ((apkname == null) || (userUUID == null) || (deviceUUID == null) || (payload == null))
        {
            Json.put(load, "error", "Request parameters missing");
            Json.put(load, "status", "error");

            return result;
        }

        Identity owner = Owner.getOwnerIdentity();

        if (owner == null)
        {
            Json.put(result, "error", "Device owner not set");
            Json.put(result, "status", "error");

            return result;
        }

        GoprotoMetadata metadata = new GoprotoMetadata();
        metadata.setTimeStamp(time);

        final GoprotoTicket ticket = new GoprotoTicket();

        ticket.setMessageUUID(Simple.decodeBase64(uuid));
        ticket.setMetadata(metadata);

        ticket.setReceiverUserUUID(Simple.decodeBase64(userUUID));
        ticket.setReceiverDeviceUUID(Simple.decodeBase64(deviceUUID));

        ticket.setAppUUID(Simple.decodeBase64(GorillaMapper.mapAPK2UUID(apkname)));

        ticket.setPayload(payload.getBytes());

        addTicketToQueue(ticket);

        Json.put(load, "status", "queued");

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
        metadata.setTimeStamp(GorillaTime.serverTimeMillis());
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
