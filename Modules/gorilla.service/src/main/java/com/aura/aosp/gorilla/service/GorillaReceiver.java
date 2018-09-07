package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.univid.Owner;

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

        if (intent.getAction().equals("com.aura.aosp.gorilla.service.WANT_OWNER"))
        {
            wantOwner(context, intent);
            return;
        }

        if (intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD"))
        {
            sendPayload(context, intent);
            return;
        }

        Err.errp("wrong action.");
    }

    private void wantOwner(Context context, Intent intent)
    {
        String apkname = intent.getStringExtra("apkname");

        if (apkname == null)
        {
            Err.errp("no apkname.");

            return;
        }

        String ownerUUID = Owner.getOwnerUUIDBase64();

        Intent responseIntent = new Intent("com.aura.aosp.gorilla.service.RECV_OWNER");

        responseIntent.setPackage(apkname);
        responseIntent.putExtra("ownerUUID", ownerUUID);

        Log.d("ownerUUID=%s apk=%s", ownerUUID, apkname);

        context.sendBroadcast(responseIntent);
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
}

