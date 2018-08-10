package com.aura.aosp.gorilla.service;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

public class GorillaBase extends Application
{
    private static final String LOGTAG = GorillaBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        Handler handler = new Handler();

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                GorillaService.SelfStartMainService(GorillaBase.this);
            }
        });
    }
}
