package com.aura.aosp.gorilla.client;

import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

public class GorillaService extends Service
{
    private final static String LOGTAG = GorillaService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOGTAG,"onStartCommand: ...");

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG,"onCreate: ...");
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG,"onBind: intent=" + intent.toString());

        return new GorillaClientService();
    }
}
