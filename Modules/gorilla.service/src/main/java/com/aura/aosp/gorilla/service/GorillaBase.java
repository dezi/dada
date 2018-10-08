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

    public static void startCronJob()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
        {
            //
            // Not available below Marshmallow.
            //

            return;
        }

        Context context = GorillaBase.getAppContext();

        JobInfo.Builder builder = new JobInfo.Builder(47110815, new ComponentName(context, GorillaCron.class));
        builder.setPersisted(true);

        builder.setPeriodic((15 * 60 * 1000));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        if (jobScheduler == null)
        {
            Err.errp("JobScheduler system service unavailable");
            return;
        }

        jobScheduler.schedule(builder.build());

        int result = jobScheduler.schedule(builder.build());
        if (result <= 0)
        {
            Err.errp("JobScheduler error=%d", result);
            return;
        }

        Log.d("result=%d", result);
    }
}
