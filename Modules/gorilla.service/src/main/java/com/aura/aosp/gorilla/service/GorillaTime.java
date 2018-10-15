package com.aura.aosp.gorilla.service;

import android.os.SystemClock;

import com.aura.aosp.aura.common.simple.Err;

public class GorillaTime
{
    private static boolean timeSynced;

    private static long serverTime;
    private static long syncedTime;

    public static void setServerTime(long serverTime)
    {
        GorillaTime.serverTime = serverTime;
        GorillaTime.syncedTime = SystemClock.elapsedRealtime();
        GorillaTime.timeSynced = true;
    }

    public static long currentTimeMillis()
    {
        return System.currentTimeMillis();
    }

    public static long serverTimeMillis()
    {
        if (! timeSynced)
        {
            Err.errp("no server time!");

            return 0;
        }

        return serverTime + (SystemClock.elapsedRealtime() - syncedTime);
    }
}
