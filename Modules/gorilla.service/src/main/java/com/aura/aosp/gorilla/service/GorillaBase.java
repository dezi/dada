package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;

import com.aura.aosp.gorilla.gomess.GomessHandler;

public class GorillaBase extends Application
{
    /**
     * Process lifetime application context. This
     * does not leak, because it is valid while
     * the process exists.
     */
    private static Application appContext;

    /**
     * Get application context from elsewhere.
     *
     * @return application context.
     */
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

        GorillaBase.startCronJob();

        GomessHandler.startService();

        GorillaLocation.startService();

        GorillaNetwork.logNetworkState();
    }

    private static void startCronJob()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            GorillaCron.startCronJob();
        }
    }
}
