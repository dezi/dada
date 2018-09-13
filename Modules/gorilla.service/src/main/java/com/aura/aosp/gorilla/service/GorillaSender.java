package com.aura.aosp.gorilla.service;

import android.content.Intent;

import com.aura.aosp.aura.common.crypter.SHA;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.client.GorillaHelpers;
import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONObject;

public class GorillaSender
{
    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

    public static Err sendBroadCastStatus(boolean uplink)
    {

        return null;
    }

    public static Err sendBroadCastPayloadResult(GoprotoTicket ticket, JSONObject result)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        IGorillaClientService remote = GorillaIntercon.getClientService(apkname);

        if (remote == null)
        {
            return Err.err("unknown/unconnected apkname=%s", apkname);
        }

        String resultStr = result.toString();

        String checksum = SHA.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                sysApkName.getBytes(),
                resultStr.getBytes()
        );

        try
        {
            boolean valid = remote.receivePayloadResult(sysApkName, resultStr, checksum);

            if (! valid)
            {
                return Err.err("invalid service apkname=%s", apkname);
            }
        }
        catch (Exception ex)
        {
            return Err.err(ex);
        }

        /*
        byte[] clientSecretBytes = GorillaIntercon.getClientSecret(apkname);

        String checksum = SHA.createSHASignatureBase64(clientSecretBytes, apkname.getBytes(), resultStr.getBytes());

        Intent resultIntent = new Intent();

        resultIntent.setPackage(apkname);
        resultIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT");

        resultIntent.putExtra("result", resultStr);
        resultIntent.putExtra("checksum", checksum);

        Log.d("apkname=%s result=%s", apkname, resultStr);

        GorillaBase.getAppContext().sendBroadcast(resultIntent);
        */

        return null;
    }

    public static Err sendBroadCastPayload(GoprotoTicket ticket)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        IGorillaClientService remote = GorillaIntercon.getClientService(apkname);

        if (remote == null)
        {
            return Err.err("unknown/unconnected apkname=%s", apkname);
        }

        long time = System.currentTimeMillis();

        String uuid = Simple.encodeBase64(ticket.getMessageUUID());
        String senderUUID = Simple.encodeBase64(ticket.getSenderUserUUID());
        String deviceUUID = Simple.encodeBase64(ticket.getSenderDeviceUUID());
        String payload = new String(ticket.getPayload());

        String checksum = SHA.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                sysApkName.getBytes(),
                Long.toString(time).getBytes(),
                uuid.getBytes(),
                senderUUID.getBytes(),
                deviceUUID.getBytes(),
                payload.getBytes()
        );

        try
        {
            boolean valid = remote.receivePayload(sysApkName, time, uuid, senderUUID, deviceUUID, payload, checksum);

            if (! valid)
            {
                return Err.err("invalid service apkname=%s", apkname);
            }
        }
        catch (Exception ex)
        {
            return Err.err(ex);
        }

        /*
        Intent payloadIntent = new Intent();

        payloadIntent.setPackage(apkname);
        payloadIntent.setAction("com.aura.aosp.gorilla.service.RECV_PAYLOAD");

        payloadIntent.putExtra("time", System.currentTimeMillis());
        payloadIntent.putExtra("uuid", Simple.encodeBase64(ticket.getMessageUUID()));
        payloadIntent.putExtra("sender", Simple.encodeBase64(ticket.getSenderUserUUID()));
        payloadIntent.putExtra("device", Simple.encodeBase64(ticket.getSenderDeviceUUID()));
        payloadIntent.putExtra("payload", new String(ticket.getPayload()));

        GorillaBase.getAppContext().sendBroadcast(payloadIntent);
        */

        return null;
    }
}
