package com.aura.aosp.gorilla.launcher;

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

        Log.d("onCreate: ...");

        EventManager eventManager = new EventManager(this);

        GorillaClient.getInstance().connectService(this);
        GorillaClient.getInstance().subscribeGorillaListener(eventManager);
    }
}
