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
        //Log.d(LOGTAG,"onReceive: intent=" + intent.toString());

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD"))
        {
            String apkname = intent.getStringExtra("apkname");

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

            JSONObject result = GorillaProtocol.getInstance(context).sendPayload(apkname, receiver, payload);

            Intent responseIntent = new Intent();

            responseIntent.setPackage(apkname);
            responseIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");
            responseIntent.putExtra("result", result.toString());

            context.sendBroadcast(responseIntent);

            return;
        }

        //
        // Silently ignore.
        //

        Log.d(LOGTAG,"onReceive: wrong action.");
    }
}
