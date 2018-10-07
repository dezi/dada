package com.aura.aosp.gorilla.service;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;

import com.aura.aosp.gorilla.client.IGorillaClientService;

import com.aura.aosp.aura.common.simple.Log;

public class GorillaSystem extends Service
{
    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

    static void startClientService(final String apkname)
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

                validateConnect(apkname);
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

    private static void validateConnect(String apkname)
    {
        IGorillaClientService gr = GorillaIntercon.getClientService(apkname);
        if (gr == null) return;

        try
        {
            String clientSignature = gr.returnYourSignature(sysApkName);
            GorillaIntercon.setClientSignature(apkname, clientSignature);

            Log.d("call apkname=%s clientSignature=%s", apkname, clientSignature);

            String checksum = GorillaIntercon.createSHASignatureBase64(apkname, sysApkName);

            boolean svlink = gr.validateConnect(sysApkName, checksum);

            Log.d("call apkname=%s serverSignature=%s clientSignature=%s svlink=%b",
                    sysApkName,
                    GorillaIntercon.getServerSignatureBase64(apkname),
                    GorillaIntercon.getClientSignatureBase64(apkname),
                    svlink);

            if (!svlink) return;

            GorillaIntercon.setServiceStatus(apkname, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("intent=%s", intent.toString());

        return new GorillaSystemService();
    }
}
