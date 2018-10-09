/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.service;

import android.support.annotation.RequiresApi;

import android.content.ComponentName;
import android.content.Context;

import android.app.job.JobScheduler;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.app.job.JobInfo;

import android.os.Build;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

/**
 * The class {@code GorillaCron} provides a dummy service
 * which is called by the AOSP in regular intervals to
 * make sure, the Gorilla system app stays active.
 * <p>
 * {@code GorillaCron} needs to be registered in manifest:
 * <p>
 * <pre>
 * {@code
 * <service>
 *     android:name=".GorillaCron"
 *     android:permission="android.permission.BIND_JOB_SERVICE">
 * </service>
 * }
 * </pre>
 *
 * @author Dennis Zierahn
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class GorillaCron extends JobService
{
    /**
     * Job id number.
     */
    private final static int CRONJOB_ID = 47110815;

    /**
     * Package private static method to initialize the cron service.
     */
    static void startCronJob()
    {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M)
        {
            //
            // Not available below Marshmallow.
            //

            return;
        }

        Context context = GorillaBase.getAppContext();

        JobInfo.Builder builder = new JobInfo.Builder(CRONJOB_ID, new ComponentName(context, GorillaCron.class));

        //
        // Job info
        //  - will be persisting after reboot
        //  - is started every 15 minutes
        //  - does not require any network
        //  - does not require the device beeing charged
        //  - does not require the device too be in idle mode
        //

        builder.setPersisted(true);
        builder.setPeriodic(15 * 60 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        if (jobScheduler == null)
        {
            Err.errp("JobScheduler system service unavailable!");
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

    /**
     * Dummy worker method, called by job scheduler.
     * This method has no further functionality. The
     * essential Gorilla connect threads are started
     * by the base application entry point.
     *
     * @param jobParameters job parameters.
     * @return false, mark job as done.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters)
    {
        Log.d("...");

        //
        // false:
        // Mark job as done, avoiding a call to onStopJob
        //
        // true:
        // Mark job as unfinished, provoking a call to onStopJob
        //

        return false;
    }

    /**
     * Dummy method needs to be implemented, never called.
     *
     * @param jobParameters job parameters.
     * @return true, mark job as done and but keep the job description.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        Log.d("...");

        jobFinished(jobParameters, false);

        //
        // true:
        // Mark job as done, but keep the job description.
        //
        // false:
        // Mark job as done, destroy job description.
        //

        return true;
    }
}
