package com.aura.aosp.gorilla.service;

import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONObject;

public class GorillaSender
{
    public static Err sendBroadCastPayload(GoprotoTicket ticket)
    {
        Intent payloadIntent = new Intent("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        Log.d("apkname=%s", apkname);

        payloadIntent.setPackage(apkname);

        payloadIntent.putExtra("time", System.currentTimeMillis());

        payloadIntent.putExtra("uuid", Simple.encodeBase64(ticket.getMessageUUID()));
        payloadIntent.putExtra("sender", Simple.encodeBase64(ticket.getSenderUserUUID()));
        payloadIntent.putExtra("device", Simple.encodeBase64(ticket.getSenderDeviceUUID()));
        payloadIntent.putExtra("payload", new String(ticket.getPayload()));

        GorillaBase.getAppContext().sendBroadcast(payloadIntent);

        return null;
    }

    public static Err sendBroadCastResult(GoprotoTicket ticket, JSONObject result)
    {
        Intent resultIntent = new Intent("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        Log.d("apkname=%s", apkname);

        resultIntent.setPackage(apkname);
        resultIntent.putExtra("result",result.toString());

        GorillaBase.getAppContext().sendBroadcast(resultIntent);

        return null;
    }

    public static Err sendBroadCastSecret(String apkname, String serverSecret, String challenge)
    {
        Intent secretIntent = new Intent("com.aura.aosp.gorilla.service.RECV_SECRET");

        secretIntent.setPackage(apkname);

        secretIntent.putExtra("serverSecret", serverSecret);
        secretIntent.putExtra("challenge", challenge);

        GorillaBase.getAppContext().sendBroadcast(secretIntent);

        return null;
    }
}
