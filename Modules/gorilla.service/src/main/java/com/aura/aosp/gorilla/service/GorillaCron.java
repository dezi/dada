package com.aura.aosp.gorilla.service;

import android.support.annotation.RequiresApi;

import android.content.ComponentName;
import android.content.Context;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobInfo;

import android.os.Build;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class GorillaCron extends JobService
{
    public static Err startBlowJob(Context context)
    {
        JobInfo.Builder builder = new JobInfo.Builder(47110815, new ComponentName(context, GorillaCron.class));
        builder.setPersisted(true);

        builder.setPeriodic((15 * 60 * 1000));

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiresCharging(false);

        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);

        if (jobScheduler == null)
        {
            return Err.errp("JobScheduler system service unavailable");
        }

        jobScheduler.schedule(builder.build());

        int result = jobScheduler.schedule(builder.build());
        if (result <= 0)
        {
            return Err.errp("JobScheduler error=%d", result);

        }

        Log.d("result=%d", result);

        return null;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters)
    {
        Log.d("...");

        jobFinished(jobParameters, false);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters)
    {
        Log.d("...");

        jobFinished(jobParameters, false);

        return false;
    }
}
