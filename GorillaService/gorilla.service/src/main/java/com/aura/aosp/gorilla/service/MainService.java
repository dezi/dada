package com.aura.aosp.gorilla.service;

import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service
{
    private static final String LOGTAG = MainService.class.getSimpleName();

    public static void SelfStartMainService(Context context)
    {
        Intent serviceIntent = new Intent(context, MainService.class);
        context.startService(serviceIntent);

        Log.d(LOGTAG,"SelfStartMainService: service started.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOGTAG, "onStartCommand: started.");

        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate: started.");

        GorillaClient.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
