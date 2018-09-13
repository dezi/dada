package com.aura.aosp.gorilla.service;

import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    @Override
    public boolean initClientSecret(String apkname, String clientSecret, String checksum)
    {
        GorillaIntercon.setClientSecret(apkname, clientSecret);

        GorillaService.startClientService(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, clientSecret);

        boolean svlink = ((checksum != null) && checksum.equals(solution));
        GorillaIntercon.setServiceStatus(apkname, svlink);

        Log.d("impl apkname=%s clientSecret=%s svlink=%b", apkname, clientSecret, svlink);

        return svlink;
    }

    @Override
    public boolean getOnlineStatus(String apkname, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        boolean status = GomessHandler.getInstance().isSessionConnected();

        Log.d("status=%b apkname=%s", status, apkname);

        return status;
    }

    @Override
    public String getOwnerUUID(String apkname, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        return Owner.getOwnerUUIDBase64();
    }

    @Override
    public String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, userUUID, deviceUUID, payload);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        JSONObject result = GomessHandler.getInstance().sendPayload(apkname, userUUID, deviceUUID, payload);

        return result.toString();
    }
}
