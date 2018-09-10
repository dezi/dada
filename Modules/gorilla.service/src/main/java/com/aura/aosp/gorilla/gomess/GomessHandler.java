package com.aura.aosp.gorilla.gomess;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.sockets.Connect;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
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

    private final Thread readerThread;
    private final Thread writerThread;

    private final List<GoprotoTicket> tickets = new ArrayList<>();

    public JSONObject sendPayload(String uuid, long time, String apkname, String userUUID, String deviceUUID, String payload)
    {
        Log.d("uuid=" + uuid + " time=" + time);
        Log.d("user=" + userUUID + " dev=" + deviceUUID);
        Log.d("apkname=" + apkname + " payload=" + payload);

        JSONObject result = new JSONObject();

        if ((uuid == null) || (apkname == null) || (userUUID == null) || (deviceUUID == null) || (payload == null) || (time <= 0))
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

        Json.put(result, "uuid", uuid);
        Json.put(result, "time", time);

        GoprotoTicket ticket = new GoprotoTicket();

        ticket.setMessageUUID(Simple.decodeBase64(uuid));

        ticket.setReceiverUserUUID(Simple.decodeBase64(userUUID));
        ticket.setReceiverDeviceUUID(Simple.decodeBase64(deviceUUID));

        ticket.setAppUUID(Simple.decodeBase64(GorillaMapper.mapAPK2UUID(apkname)));

        ticket.setPayload(payload.getBytes());

        addTicketToQueue(ticket);

        Json.put(result, "status", "queued");

        return result;
    }

    public void resetSession()
    {
        if (client != null)
        {
            Log.d("reset starting...");

            client.disconnect();

            Log.d("reset done.");
        }
    }

    private GomessHandler()
    {
        readerThread = new Thread(readerRunner);
        readerThread.start();

        writerThread = new Thread(writerRunner);
        writerThread.start();
    }

    private void addTicketToQueue(GoprotoTicket ticket)
    {
        synchronized (tickets)
        {
            tickets.add(ticket);
        }
    }

    private final Runnable readerRunner = new Runnable()
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

    private final Runnable writerRunner = new Runnable()
    {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run()
        {
            while (true)
            {
                if (client == null)
                {
                    Simple.sleep(100);

                    continue;
                }

                GoprotoTicket ticket = null;

                synchronized (tickets)
                {
                    if (tickets.size() > 0)
                    {
                        ticket = tickets.remove(0);
                    }
                }

                if (ticket == null)
                {
                    Simple.sleep(100);

                    continue;
                }

                Err err = client.sendMessageUpload(ticket);

                JSONObject result = new JSONObject();
                Json.put(result, "uuid", ticket.getMessageUUIDBase64());
                Json.put(result, "status", "send");

                if (err != null)
                {
                    Json.put(result, "error", err.err);
                    Json.put(result, "status", "error");
                }

                GorillaSender.sendBroadCastPayloadResult(ticket, result);

                if (err == null) continue;

                tickets.add(0, ticket);
                Log.e("err=%s", err);

                Simple.sleep(100);
            }
        }
    };

    private GomessClient client;

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
        return client.clientHandler();
    }
}
