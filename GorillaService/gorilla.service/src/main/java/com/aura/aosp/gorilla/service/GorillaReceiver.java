package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

public class GorillaReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = GorillaReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ((intent.getAction() == null) || ! intent.getAction().equals("com.aura.android.gorillaservice.SEND_PAYLOAD"))
        {
            //
            // Silently ignore.
            //

            Log.d(LOGTAG,"onReceive: wrong action.");

            return;
        }

        Log.d(LOGTAG,"onReceive: intent=" + intent.toString());

        String apkname = intent.getPackage();

        if (apkname == null)
        {
            //
            // Silently ignore.
            //

            Log.d(LOGTAG,"onReceive: no apkname.");

            return;
        }

        //
        // Prepare a response broadcast.
        //

        String receiver = intent.getStringExtra("receiver");
        String payload = intent.getStringExtra("payload");

        JSONObject result = GorillaClient.getInstance(context).sendPayload(apkname, receiver, payload);

        Intent responseIntent = new Intent();

        responseIntent.setPackage(apkname);
        responseIntent.setAction("com.aura.android.gorillaservice.SEND_PAYLOAD_RESULT");
        responseIntent.putExtra("result", result.toString());

        context.sendBroadcast(responseIntent);
    }
}
