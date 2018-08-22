package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.simple.Err;

import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class GorillaReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ((intent.getAction() == null) || !intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD"))
        {
            Err.errp("wrong action.");

            return;
        }

        String apkname = intent.getStringExtra("apkname");

        if (apkname == null)
        {
            Err.errp("no apkname.");

            return;
        }

        //
        // Prepare a response broadcast.
        //

        String uuid = intent.getStringExtra("uuid");
        long time = intent.getLongExtra("time", -1);
        String receiver = intent.getStringExtra("receiver");
        String payload = intent.getStringExtra("payload");

        JSONObject result = GomessHandler.getInstance().sendPayload(uuid, time, apkname, receiver, payload);

        Intent responseIntent = new Intent("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        responseIntent.setPackage(apkname);
        responseIntent.putExtra("result", result.toString());

        context.sendBroadcast(responseIntent);
    }
}

