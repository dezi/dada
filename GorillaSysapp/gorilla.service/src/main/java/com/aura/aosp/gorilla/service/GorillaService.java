package com.aura.aosp.gorilla.service;

import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;

import com.aura.aosp.gorilla.gomess.GomessThread;

import com.aura.aosp.aura.simple.Log;

public class GorillaService extends Service
{
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

    //endregion Static stuff.

    //region Instance stuff.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("...");

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d("...");

        //
        // Dummy fetch instance to
        // make sure it is started.
        //

        GomessThread.getInstance();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    //endregion Instance stuff.
}
