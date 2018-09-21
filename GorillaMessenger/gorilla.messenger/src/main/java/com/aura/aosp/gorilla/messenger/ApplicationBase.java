package com.aura.aosp.gorilla.messenger;

import android.app.Application;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.client.GorillaClient;

public class ApplicationBase extends Application
{
    private final static String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(LOGTAG, "onCreate: ...");

        EventManager eventManager = new EventManager();

        GorillaClient.getInstance().bindService(this);
        GorillaClient.getInstance().subscribeGorillaListener(eventManager);
    }
}
