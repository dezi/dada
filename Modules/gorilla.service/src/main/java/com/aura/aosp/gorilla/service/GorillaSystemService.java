package com.aura.aosp.gorilla.service;

import android.util.Base64;

import com.aura.aosp.aura.common.crypter.RND;
import com.aura.aosp.aura.common.crypter.SHA;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.IGorillaSystemService;
import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    @Override
    public void initClientSecret(String apkname, String clientSecret)
    {
        Log.d("apkname=%s clientSecret=%s",apkname, clientSecret);

        GorillaIntercon.setClientSecret(apkname, clientSecret);

        byte[] serverSecretBytes = RND.randomBytes(16);
        GorillaIntercon.setServerSecret(apkname, serverSecretBytes);

        String serverSecret = Base64.encodeToString(serverSecretBytes, Base64.NO_WRAP);
        String challenge = SHA.createSHASignatureBase64(GorillaIntercon.getClientSecret(apkname));

        GorillaSender.sendBroadCastSecret(apkname, serverSecret, challenge);

        GorillaIntercon.startClientService(apkname);
    }

    @Override
    public void replyClientSecret(String apkname, String clientSecret, String checksum)
    {

    }

    @Override
    public boolean validateConnect(String apkname, String challenge)
    {
        byte[] serverSecretBytes = GorillaIntercon.getServerSecret(apkname);
        String solution = SHA.createSHASignatureBase64(serverSecretBytes);

        if ((challenge == null) || (solution == null) || ! challenge.equals(solution))
        {
            Log.e("challenge failed!");
            return false;
        }

        Log.d("validated apkname=%s",apkname);

        return true;
    }
























    @Override
    public boolean getOnlineStatus(String apkname, String checksum)
    {
        byte[] serverSecretBytes = GorillaIntercon.getServerSecret(apkname);
        String solution = SHA.createSHASignatureBase64(serverSecretBytes, apkname.getBytes());

        if ((checksum == null) || (solution == null) || ! checksum.equals(solution))
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
        byte[] serverSecretBytes = GorillaIntercon.getServerSecret(apkname);
        String solution = SHA.createSHASignatureBase64(serverSecretBytes, apkname.getBytes());

        if ((checksum == null) || (solution == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        return Owner.getOwnerUUIDBase64();
    }

    @Override
    public String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum)
    {
        byte[] serverSecretBytes = GorillaIntercon.getServerSecret(apkname);

        String solution = SHA.createSHASignatureBase64(serverSecretBytes,
                apkname.getBytes(),
                userUUID.getBytes(),
                deviceUUID.getBytes(),
                payload.getBytes()
        );

        if ((checksum == null) || (solution == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        JSONObject result = GomessHandler.getInstance().sendPayload(apkname, userUUID, deviceUUID, payload);

        return result.toString();
    }
}
