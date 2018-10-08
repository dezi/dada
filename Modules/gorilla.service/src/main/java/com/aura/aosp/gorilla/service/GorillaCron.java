package com.aura.aosp.gorilla.service;

import android.support.annotation.RequiresApi;

import android.app.job.JobParameters;
import android.app.job.JobService;

import android.os.Build;

import com.aura.aosp.aura.common.simple.Log;

@RequiresApi(api = Build.VERSION_CODES.M)
public class GorillaCron extends JobService
{
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
