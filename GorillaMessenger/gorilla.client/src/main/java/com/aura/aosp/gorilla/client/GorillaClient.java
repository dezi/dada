package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.util.UUID;

@SuppressLint("StaticFieldLeak")
public class GorillaClient
{
    private static final String LOGTAG = GorillaClient.class.getSimpleName();

    //region Static implemention.

    private static final Object mutex = new Object();
    private static GorillaClient instance;

    public static GorillaClient getInstance(Context context)
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GorillaClient(context);
                }
            }
        }

        return instance;
    }

    public static class OnResultListener
    {
        public void onResult(JSONObject result)
        {
            Log.d(LOGTAG, "onResult: STUB!");
        }
    }

    //endregion Static implemention.

    //region Instance implemention.

    private Context context;
    private OnResultListener onResultListener;

    private GorillaClient(Context context)
    {
        this.context = context;
    }

    public void onReceive(Intent intent)
    {
        //Log.d(LOGTAG, "onReceive: intent=" + intent.toString());

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            String result = intent.getStringExtra("result");

            Log.d(LOGTAG,"onReceive: SEND_PAYLOAD_RESULT result=" + result);

            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_PAYLOAD"))
        {
            String uuid = intent.getStringExtra("uuid");
            long time = intent.getLongExtra("time", -1);
            String sender = intent.getStringExtra("sender");
            String payload = intent.getStringExtra("payload");

            Log.d(LOGTAG,"onReceive: RECV_PAYLOAD uuid=" + uuid + " time=" + time + " sender=" + sender + " payload=" + payload);

            return;
        }

        //
        // Silently ignore.
        //

        Log.d(LOGTAG, "onReceive: wrong action.");
    }

    public void sendPayload(String receiver, String payload)
    {
        JSONObject result = new JSONObject();

        String uuid = UUID.randomUUID().toString();
        long time = System.currentTimeMillis();

        Json.put(result, "uuid", uuid);
        Json.put(result, "time", time);
        Json.put(result, "status", "pending");

        Intent requestIntent = new Intent();

        requestIntent.setPackage("com.aura.android.gorilla.sysapp");
        requestIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD");

        requestIntent.putExtra("uuid", uuid);
        requestIntent.putExtra("time", time);
        requestIntent.putExtra("apkname", context.getPackageName());
        requestIntent.putExtra("receiver", receiver);
        requestIntent.putExtra("payload", payload);

        context.sendBroadcast(requestIntent);

        if (onResultListener != null)
        {
            onResultListener.onResult(result);
        }
    }

    public void setOnResultListener(OnResultListener onResultListener)
    {
        this.onResultListener = onResultListener;
    }

    //endregion Instance implemention.
}
