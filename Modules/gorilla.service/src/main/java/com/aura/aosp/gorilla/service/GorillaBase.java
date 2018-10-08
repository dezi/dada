package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.aura.aosp.aura.common.simple.Err;
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

        GorillaBase.startCronJob();

        GomessHandler.startService();

        GorillaLocation.startService(this);

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
