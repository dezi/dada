package com.aura.aosp.aura.common.simple;

import android.os.SystemClock;

public class Perf
{
    private long startTime;

    public Perf()
    {
        startTime = SystemClock.elapsedRealtime();
    }

    public long elapsedTime()
    {
        return startTime - SystemClock.elapsedRealtime();
    }
}
