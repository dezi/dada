package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;
import android.content.Context;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;

public class GorillaBase extends Application
{
    //region Static stuff.

    private static Context appContext;

    @NonNull
    public static Context getAppContext()
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

        //
        // Required for testing with Android Studio
        // because onBootCompleted is never issued
        // in this case.
        //

        Simple.getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                GorillaService.SelfStartMainService();
            }
        });
    }

    //endregion Instance stuff.
}
