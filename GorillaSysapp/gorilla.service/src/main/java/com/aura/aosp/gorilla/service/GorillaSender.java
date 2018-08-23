package com.aura.aosp.gorilla.service;

import android.content.Intent;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

public class GorillaSender
{
    public static Err sendBroadCast(GoprotoTicket ticket)
    {
        Intent echoIntent = new Intent("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        Log.d("apkname=%s", apkname);

        echoIntent.setPackage(apkname);

        echoIntent.putExtra("time", System.currentTimeMillis());

        echoIntent.putExtra("uuid", Simple.encodeBase64(ticket.getMessageUUID()));
        echoIntent.putExtra("sender", Simple.encodeBase64(ticket.getSenderUserUUID()));
        echoIntent.putExtra("device", Simple.encodeBase64(ticket.getSenderDeviceUUID()));
        echoIntent.putExtra("payload", new String(ticket.getPayload()));

        GorillaBase.getAppContext().sendBroadcast(echoIntent);

        return null;
    }
}
