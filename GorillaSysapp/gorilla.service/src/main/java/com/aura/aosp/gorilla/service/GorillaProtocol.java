package com.aura.aosp.gorilla.service;

import android.content.Context;
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
        Json.put(result, "success", false);

        if ((apkname == null) || (receiver == null) || (payload == null))
        {
            Json.put(result, "error", "APK-name, receiver or payload missing");
        }
        else
        {
            Json.put(result, "success", true);
        }

        return result;
    }
}
