package com.aura.aosp.gorilla.gomess;

import android.content.Intent;

import com.aura.aosp.aura.sockets.Connect;
import com.aura.aosp.gorilla.service.GorillaBase;

import com.aura.aosp.aura.univid.Identity;
import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Log;

import org.json.JSONObject;

@SuppressWarnings("FieldCanBeLocal")
public class GomessProtocol
{
    public static GomessProtocol getInstance()
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GomessProtocol();
                }
            }
        }

        return instance;
    }

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

    private static final Object mutex = new Object();

    private static GomessProtocol instance;

    private final Thread workerThread;

    private GomessProtocol()
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
                GorillaNodes.ClientNode clientNode = GorillaNodes.getBestNode("DE", 53.551086, 9.993682);
                if (clientNode == null) continue;

                enterGorillaSession(clientNode);
            }
        }
    };

    private static void enterGorillaSession(GorillaNodes.ClientNode cnode)
    {
        Log.d("...");

        Connect conn = new Connect(cnode.Addr, cnode.Port);
        if (conn.connect() != null) return;

        GorillaSession session = new GorillaSession(conn);

        session.UserUUID = Identity.getUserUUID();
        session.DeviceUUID = Identity.getDeviceUUID();
        session.ClientPrivKey = Identity.getRSAPrivateKey();

        GomessClient client = new GomessClient(session, false);

        client.clientHandler();
    }
}
