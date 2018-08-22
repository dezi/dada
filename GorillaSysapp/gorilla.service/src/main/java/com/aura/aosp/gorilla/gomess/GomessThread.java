package com.aura.aosp.gorilla.gomess;

import android.content.Intent;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.sockets.Connect;
import com.aura.aosp.gorilla.goproto.GoprotoSession;
import com.aura.aosp.gorilla.service.GorillaBase;

import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Log;

import org.json.JSONObject;

@SuppressWarnings("FieldCanBeLocal")
public class GomessThread
{
    private static GomessThread instance;
    private static final Object mutex = new Object();

    public static GomessThread getInstance()
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GomessThread();
                }
            }
        }

        return instance;
    }

    private final Thread workerThread;

    public JSONObject sendPayload(String uuid, long time, String apkname, String receiver, String payload)
    {
        Log.d("uuid=" + uuid + " time=" + time + " apkname=" + apkname + " receiver=" + receiver + " payload=" + payload);

        JSONObject result = new JSONObject();

        if ((uuid == null) || (apkname == null) || (receiver == null) || (payload == null) || (time <= 0))
        {
            Json.put(result, "error", "Request parameters missing");
            Json.put(result, "status", "error");
        }
        else
        {
            //
            // Simply echo the payload for now...
            //

            Intent echoIntent = new Intent("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

            echoIntent.setPackage(apkname);
            echoIntent.putExtra("uuid", uuid);
            echoIntent.putExtra("time", time);
            echoIntent.putExtra("sender", receiver);
            echoIntent.putExtra("payload", payload);

            GorillaBase.getAppContext().sendBroadcast(echoIntent);

            //
            // Return success for now.
            //

            Json.put(result, "uuid", uuid);
            Json.put(result, "time", time);
            Json.put(result, "status", "send");
        }

        return result;
    }

    private GomessThread()
    {
        workerThread = new Thread(workerRunner);
        workerThread.start();
    }

    private static final Runnable workerRunner = new Runnable()
    {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run()
        {
            while (true)
            {
                GomessNode clientNode = GomessNodes.getBestNode("DE", 53.551086, 9.993682);
                if (clientNode == null) continue;

                Err err = enterGorillaSession(clientNode);
                if (err == null) continue;

                Simple.sleep(1000);
            }
        }
    };

    private static Err enterGorillaSession(GomessNode cnode)
    {
        Log.d("...");

        Connect conn = new Connect(cnode.Addr, cnode.Port);
        if (conn.connect() != null) return Err.getLastErr();

        GoprotoSession session = new GoprotoSession(conn);
        Err err = session.aquireIdentity();
        if (err != null) return err;

        GomessClient client = new GomessClient(session, false);
        return client.clientHandler();
    }
}
