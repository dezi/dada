package com.aura.aosp.gorilla.service;

import android.app.Application;
import android.util.Log;

public class GorillaBase extends Application
{
    private static final String LOGTAG = GorillaBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        GorillaService.SelfStartMainService(this);
    }
}
