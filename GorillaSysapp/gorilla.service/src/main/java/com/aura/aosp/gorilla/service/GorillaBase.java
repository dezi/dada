package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.aura.aosp.aura.simple.Log;

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

        //
        // Required for testing with Android Studio
        // because onBootCompleted is never issued
        // in this case.
        //

        Handler handler = new Handler();

        handler.post(new Runnable()
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
