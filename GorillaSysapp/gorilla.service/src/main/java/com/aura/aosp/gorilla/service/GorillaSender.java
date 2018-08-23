package com.aura.aosp.gorilla.service;

import android.content.Intent;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

public class GorillaSender
{
    public static Err sendBroadCast(GoprotoTicket ticket)
    {
        Intent echoIntent = new Intent("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

        /*
        echoIntent.setPackage(apkname);
        echoIntent.putExtra("uuid", uuid);
        echoIntent.putExtra("time", time);
        echoIntent.putExtra("sender", userUUID);
        echoIntent.putExtra("device", deviceUUID);
        echoIntent.putExtra("payload", payload);

        GorillaBase.getAppContext().sendBroadcast(echoIntent);
        */

        return null;
    }
}
