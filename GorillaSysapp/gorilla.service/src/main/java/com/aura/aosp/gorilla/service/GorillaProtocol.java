package com.aura.aosp.gorilla.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

public class GorillaProtocol
{
    private static final String LOGTAG = GorillaProtocol.class.getSimpleName();

    private static final Object mutex = new Object();
    private static GorillaProtocol instance;

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

    private Context context;

    private GorillaProtocol(Context context)
    {
        this.context = context;
    }

    public JSONObject sendPayload(String apkname, String receiver, String payload)
    {
        Log.d(LOGTAG, "sendPayload: apkname=" + apkname + " receiver=" + receiver + " payload=" + payload);

        JSONObject result = new JSONObject();

        if ((apkname == null) || (receiver == null) || (payload == null))
        {
            Json.put(result, "error", "APK-name, receiver or payload missing");
            Json.put(result, "success", false);
        }
        else
        {
            //
            // Simply echo the payload for now...
            //

            Intent echoIntent = new Intent();

            echoIntent.setPackage(apkname);
            echoIntent.setAction("com.aura.aosp.gorilla.service.RECV_PAYLOAD");
            echoIntent.putExtra("sender", receiver);
            echoIntent.putExtra("payload", payload);

            context.sendBroadcast(echoIntent);

            //
            // Return success for now.
            //

            Json.put(result, "success", true);
        }

        return result;
    }
}
