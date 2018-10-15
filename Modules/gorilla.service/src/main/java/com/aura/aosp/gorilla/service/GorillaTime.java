package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

public class GorillaTime extends BroadcastReceiver
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

    public static long deviceTimeMillis()
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

        long nowServerTime = serverTime + (SystemClock.elapsedRealtime() - syncedTime);
        long nowClientTime = deviceTimeMillis();

        Log.d("nowServerTime=%s", Dates.getLocalDateAndTimeMillis(nowServerTime));
        Log.d("nowClientTime=%s", Dates.getLocalDateAndTimeMillis(nowClientTime));

        return nowServerTime;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if ((action != null) && action.equals(Intent.ACTION_TIME_CHANGED))
        {
            Log.e("time manually set!");
        }
    }

    private long startTime;

    public GorillaTime()
    {
        startTime = SystemClock.elapsedRealtime();
    }

    public long elapsedTime()
    {
        return startTime - SystemClock.elapsedRealtime();
    }
}
