package com.aura.aosp.gorilla.service;

import android.content.Intent;

import com.aura.aosp.aura.common.crypter.SHA;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONObject;

import java.util.List;

public class GorillaSender
{
    public static Err sendBroadCastSecret(String apkname, String serverSecret, String challenge)
    {
        Intent secretIntent = new Intent();

        secretIntent.setPackage(apkname);
        secretIntent.setAction("com.aura.aosp.gorilla.service.RECV_SECRET");

        secretIntent.putExtra("serverSecret", serverSecret);
        secretIntent.putExtra("challenge", challenge);

        GorillaBase.getAppContext().sendBroadcast(secretIntent);

        return null;
    }

    public static Err sendBroadCastOwner(String apkname, String ownerUUID)
    {
        byte[] clientSecretBytes = GorillaMapper.getClientSecret(apkname);

        String checksum;

        if (ownerUUID == null)
        {
            checksum = SHA.createSHASignatureBase64(clientSecretBytes, apkname.getBytes());
        }
        else
        {
            checksum = SHA.createSHASignatureBase64(clientSecretBytes, apkname.getBytes(), ownerUUID.getBytes());
        }

        Intent ownerIntent = new Intent();

        ownerIntent.setPackage(apkname);
        ownerIntent.setAction("com.aura.aosp.gorilla.service.RECV_OWNER");

        ownerIntent.putExtra("ownerUUID", ownerUUID);
        ownerIntent.putExtra("checksum", checksum);

        Log.d("apkname=%s ownerUUID=%s", apkname, ownerUUID);

        GorillaBase.getAppContext().sendBroadcast(ownerIntent);

        return null;
    }

    public static Err sendBroadCastStatus(boolean uplink)
    {

        return null;
    }

    public static Err sendBroadCastPayloadResult(GoprotoTicket ticket, JSONObject result)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        byte[] clientSecretBytes = GorillaMapper.getClientSecret(apkname);

        String resultStr = result.toString();
        String checksum = SHA.createSHASignatureBase64(clientSecretBytes, apkname.getBytes(), resultStr.getBytes());

        Intent resultIntent = new Intent();

        resultIntent.setPackage(apkname);
        resultIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        resultIntent.putExtra("result", resultStr);
        resultIntent.putExtra("checksum", checksum);

        Log.d("apkname=%s result=%s", apkname, resultStr);

        GorillaBase.getAppContext().sendBroadcast(resultIntent);

        return null;
    }

    public static Err sendBroadCastPayload(GoprotoTicket ticket)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        Intent payloadIntent = new Intent();

        payloadIntent.setPackage(apkname);
        payloadIntent.setAction("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

        payloadIntent.putExtra("time", System.currentTimeMillis());
        payloadIntent.putExtra("uuid", Simple.encodeBase64(ticket.getMessageUUID()));
        payloadIntent.putExtra("sender", Simple.encodeBase64(ticket.getSenderUserUUID()));
        payloadIntent.putExtra("device", Simple.encodeBase64(ticket.getSenderDeviceUUID()));
        payloadIntent.putExtra("payload", new String(ticket.getPayload()));

        GorillaBase.getAppContext().sendBroadcast(payloadIntent);

        return null;
    }
}
