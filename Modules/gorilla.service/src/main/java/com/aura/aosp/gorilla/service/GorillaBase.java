package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.gomess.GomessHandler;

public class GorillaBase extends Application
{
    private static Application appContext;

    @NonNull
    public static Application getAppContext()
    {
        return appContext;
    }

    @Override
    public void onCreate()
    {
        appContext = this;

        Log.d("...");

        super.onCreate();

        Simple.initialize(this);

        GomessHandler.startService();

        GorillaLocation.startService(this);

        GorillaNetwork.logNetworkState();
    }
}
