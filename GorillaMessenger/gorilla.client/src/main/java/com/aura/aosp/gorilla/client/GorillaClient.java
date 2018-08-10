package com.aura.aosp.gorilla.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class GorillaClient extends BroadcastReceiver
{
    private static final String LOGTAG = GorillaClient.class.getSimpleName();

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

    private Context context;

    private GorillaClient(Context context)
    {
        this.context = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        this.context.registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Log.d(LOGTAG, "onReceive: intent=" + intent.toString());

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            String result = intent.getStringExtra("result");

            Log.d(LOGTAG,"onReceive: result=" + result);

            return;
        }

        //
        // Silently ignore.
        //

        Log.d(LOGTAG, "onReceive: wrong action.");
    }

    public void sendPayload(String receiver, String payload)
    {
        Intent requestIntent = new Intent();

        requestIntent.setPackage("com.aura.android.gorilla.sysapp");
        requestIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD");

        requestIntent.putExtra("apkname", context.getPackageName());
        requestIntent.putExtra("receiver", receiver);
        requestIntent.putExtra("payload", payload);

        context.sendBroadcast(requestIntent);
    }
}
