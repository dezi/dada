package com.aura.aosp.gorilla.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.gomess.GomessHandler;

import com.aura.aosp.aura.common.simple.Log;

public class GorillaService extends Service
{
    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

    //region Static stuff.

    //
    // Public static self start Gorilla Service via intent.
    //
    public static void SelfStartMainService()
    {
        Context context = GorillaBase.getAppContext();
        Intent serviceIntent = new Intent(context, GorillaService.class);
        context.startService(serviceIntent);

        Log.d("service started...");
    }

    public static void SelfStopMainService()
    {
        Context context = GorillaBase.getAppContext();
        Intent serviceIntent = new Intent(context, GorillaService.class);
        context.stopService(serviceIntent);

        Log.d("service stopped...");
    }

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

    private static void initServerSecret(String apkname)
    {
        IGorillaClientService gr = GorillaIntercon.getClientService(apkname);
        if (gr == null) return;

        try
        {
            String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
            String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, sysApkName, serverSecret);

            boolean svlink = gr.initServerSecret(sysApkName, serverSecret, checksum);

            Log.d("call apkname=%s serverSecret=%s clientSecret=%s svlink=%b",
                    sysApkName,
                    GorillaIntercon.getServerSecretBase64(apkname),
                    GorillaIntercon.getClientSecretBase64(apkname),
                    svlink);

            if (!svlink) return;

            GorillaIntercon.setServiceStatus(apkname, true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //endregion Static stuff.

    //region Instance stuff.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("...");

        return START_STICKY;
        //return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d("...");

        //
        // Dummy fetch instance to
        // make sure it is started.
        //

        GomessHandler.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("intent=%s", intent.toString());

        return new GorillaSystemService();
    }

    //endregion Instance stuff.
}
