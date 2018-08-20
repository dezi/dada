package com.aura.aosp.gorilla.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aura.aosp.aura.simple.Json;
import com.aura.aosp.aura.simple.Simple;

import org.json.JSONObject;

@SuppressWarnings("FieldCanBeLocal")
public class GorillaProtocol
{
    private static final String LOGTAG = GorillaProtocol.class.getSimpleName();

    public static GorillaProtocol getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GorillaProtocol(context);
                }
            }
        }

        return instance;
    }

    public JSONObject sendPayload(String uuid, long time, String apkname, String receiver, String payload)
    {
        Log.d(LOGTAG, "sendPayload: uuid=" + uuid + " time=" + time + " apkname=" + apkname + " receiver=" + receiver + " payload=" + payload);

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

            context.sendBroadcast(echoIntent);

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

    @SuppressLint("StaticFieldLeak")
    private static GorillaProtocol instance;

    private final Context context;
    private final Thread workerThread;

    private GorillaProtocol(Context context)
    {
        this.context = context;

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
                GorillaNodes.getBestNode("DE", 53.551086, 9.993682);

                Simple.sleep(1000);

                Log.d(LOGTAG, "workerRunner: ...");

                break;
            }
        }
    };
}
