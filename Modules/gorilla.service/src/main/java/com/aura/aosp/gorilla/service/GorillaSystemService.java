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

        startClientService(apkname);

        String solution = SHA.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                apkname.getBytes(),
                clientSecret.getBytes()
        );

        boolean svlink = ((checksum != null) && checksum.equals(solution));
        GorillaIntercon.setServiceStatus(apkname, svlink);

        Log.d("impl apkname=%s clientSecret=%s svlink=%b", apkname, clientSecret, svlink);

        return svlink;
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
                    GorillaIntercon.getServerSecret(apkname),
                    GorillaIntercon.getClientSecret(apkname),
                    sysApkName.getBytes(),
                    serverSecret.getBytes()
            );

            boolean svlink = gr.initServerSecret(sysApkName, serverSecret, checksum);
            GorillaIntercon.setServiceStatus(apkname, svlink);

            Log.d("call apkname=" + sysApkName + " serverSecret=" + serverSecret + " svlink=" + svlink);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
























    @Override
    public boolean getOnlineStatus(String apkname, String checksum)
    {
        String solution = SHA.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                apkname.getBytes());

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
