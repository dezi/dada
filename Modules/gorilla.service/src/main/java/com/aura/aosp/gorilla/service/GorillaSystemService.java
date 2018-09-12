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

import com.aura.aosp.gorilla.client.GorillaHelpers;
import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.client.IGorillaSystemService;
import com.aura.aosp.gorilla.gomess.GomessHandler;

import org.json.JSONObject;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

    @Override
    public boolean initClientSecret(String apkname, String clientSecret, String checksum)
    {
        GorillaIntercon.setClientSecret(apkname, clientSecret);

        String challenge = SHA.createSHASignatureBase64(GorillaIntercon.getClientSecret(apkname));

        GorillaSender.sendBroadCastSecret(apkname, GorillaIntercon.getServerSecretBase64(apkname), challenge);

        startClientService(apkname);

        String solution = SHA.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                apkname.getBytes(),
                clientSecret.getBytes()
        );

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d("impl apkname=%s clientSecret=%s valid=%b",apkname, clientSecret, valid);

        return false;
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

                initServerSecret(apkname);
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

    private void initServerSecret(String apkname)
    {
        IGorillaClientService gr = GorillaIntercon.getClientService(apkname);
        if (gr == null) return;

        try
        {
            String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);

            String checksum = SHA.createSHASignatureBase64(
                    GorillaIntercon.getClientSecret(apkname),
                    sysApkName.getBytes(),
                    serverSecret.getBytes()
            );

            boolean valid = gr.initServerSecret(sysApkName, serverSecret, checksum);

            Log.d("call apkname=" + sysApkName + " serverSecret=" + serverSecret + " valid=" + valid);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
