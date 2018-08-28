package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class GorillaReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction() == null)
        {
            Err.errp("no action.");

            return;
        }

        if (intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_PAYLOAD"))
        {
            recvPayload(context, intent);
            return;
        }

        if (intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD"))
        {
            sendPayload(context, intent);
            return;
        }

        if (intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            sendPayloadResult(context, intent);
            return;
        }

        Err.errp("wrong action.");
    }

    private void sendPayload(Context context, Intent intent)
    {
        String apkname = intent.getStringExtra("apkname");

        if (apkname == null)
        {
            Err.errp("no apkname.");

            return;
        }

        long time = intent.getLongExtra("time", -1);
        String uuid = intent.getStringExtra("uuid");
        String receiver = intent.getStringExtra("receiver");
        String device = intent.getStringExtra("device");
        String payload = intent.getStringExtra("payload");

        JSONObject result = GomessHandler.getInstance().sendPayload(uuid, time, apkname, receiver, device, payload);

        //
        // Prepare a response broadcast.
        //

        Intent responseIntent = new Intent("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        responseIntent.setPackage(apkname);
        responseIntent.putExtra("result", result.toString());

        context.sendBroadcast(responseIntent);
    }

    private void sendPayloadResult(Context context, Intent intent)
    {
        String result = intent.getStringExtra("result");

        Log.d("result=%s", result);
    }

    private void recvPayload(Context context, Intent intent)
    {
        long time = intent.getLongExtra("time", -1);
        String uuid = intent.getStringExtra("uuid");
        String sender = intent.getStringExtra("sender");
        String device = intent.getStringExtra("device");
        String payload = intent.getStringExtra("payload");

        Log.d("uuid=" + uuid + " time=" + time);
        Log.d("sender=" + sender + " device=" + device);
        Log.d("payload=" + payload);
    }
}

