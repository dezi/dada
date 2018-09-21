package com.aura.aosp.gorilla.client;

import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

public class GorillaService extends Service
{
    private final static String LOGTAG = GorillaService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG,"onBind: intent=" + intent.toString());

        return new GorillaClientService();
    }
}
