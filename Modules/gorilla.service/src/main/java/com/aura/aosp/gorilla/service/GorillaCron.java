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

@RequiresApi(api = Build.VERSION_CODES.M)
public class GorillaCron extends JobService
{
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

        JobInfo.Builder builder = new JobInfo.Builder(47110815, new ComponentName(context, GorillaCron.class));

        builder.setPersisted(true);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);

        builder.setPeriodic(15 * 60 * 1000);

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

    @Override
    public boolean onStartJob(JobParameters jobParameters)
    {
        Log.d("...");

        jobFinished(jobParameters, true);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        Log.d("...");

        jobFinished(jobParameters, true);

        return true;
    }
}
