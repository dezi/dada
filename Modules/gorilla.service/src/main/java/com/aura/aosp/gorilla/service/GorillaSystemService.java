package com.aura.aosp.gorilla.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Base64;

import com.aura.aosp.aura.common.crypter.RND;
import com.aura.aosp.aura.common.crypter.SHA;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaClientService;
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

        startClientService(apkname);
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

    private void startClientService(final String apkname)
    {
        IGorillaClientService service = GorillaIntercon.getClientService(apkname);
        if (service != null) return;

        ServiceConnection serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d("Client apkname=%s className=%s", apkname, className.toString());

                IGorillaClientService gorillaRemote = IGorillaClientService.Stub.asInterface(service);
                GorillaIntercon.setClientService(apkname, gorillaRemote);
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d("Client apkname=%s className=%s", apkname, className.toString());

                GorillaIntercon.setClientService(apkname, null);

                GorillaBase.getAppContext().unbindService(this);
            }
        };

        Log.d("apkname=%s", apkname);

        ComponentName componentName = new ComponentName(apkname, "com.aura.aosp.gorilla.client.GorillaService");

        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(componentName);

        GorillaBase.getAppContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
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
