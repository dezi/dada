package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;

public class GorillaBase extends Application
{
    //region Static stuff.

    private static Application appContext;

    @NonNull
    public static Application getAppContext()
    {
        return appContext;
    }

    //endregion Static stuff.

    //region Instance stuff.

    @Override
    public void onCreate()
    {
        appContext = this;

        Log.d("...");

        super.onCreate();

        Simple.initialize(this);

        GorillaService.selfStartMainService();
        GorillaLocation.startService(this);

        GorillaNetwork.logNetworkState();

    }

    //endregion Instance stuff.
}
