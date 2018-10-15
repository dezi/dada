/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.service;

import android.preference.PreferenceManager;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

/**
 * The class {@code GorillaTime} provides a date and time
 * value from the Gorilla server environment.
 * <p>
 * {@code GorillaTime} needs to be registered in manifest:
 * <p>
 * <pre>
 * {@code
 * <receiver android:name=".GorillaTime">
 *      <intent-filter>
 *          <action android:name="android.intent.action.TIME_SET"/>
 *      </intent-filter>
 * </receiver>
 * }
 * </pre>
 *
 * @author Dennis Zierahn
 */
public class GorillaTime extends BroadcastReceiver
{
    /**
     * Flag if time has beend synced with servers.
     */
    private static boolean timeSynced;

    /**
     * Server time at time of syncronization.
     */
    private static long serverTime;

    /**
     * Device time at time of syncronization.
     */
    private static long deviceTime;

    /**
     * Elapsed device startup time at time of syncronization.
     */
    private static long syncedTime;

    /**
     * Set server time to system and persist.
     *
     * @param serverTime server time in UTC milliseconds since the epoch.
     */
    public static void setServerTime(long serverTime)
    {
        GorillaTime.serverTime = serverTime;
        GorillaTime.deviceTime = deviceTimeMillis();
        GorillaTime.syncedTime = SystemClock.elapsedRealtime();
        GorillaTime.timeSynced = true;

        Log.d("setServerTime=%s", Dates.getLocalDateAndTimeMillis(serverTime));
        Log.d("setClientTime=%s", Dates.getLocalDateAndTimeMillis(deviceTime));

        saveServerTime();
    }

    /**
     * Persists all time data to repoduce setting after restart.
     */
    private static void saveServerTime()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GorillaBase.getAppContext());

        String prefkey = GorillaTime.class.getCanonicalName();

        prefs.edit().putLong(prefkey + ".serverTime", serverTime).apply();
        prefs.edit().putLong(prefkey + ".deviceTime", deviceTime).apply();

        Log.d("save server time apkname=%s", prefkey);

        dumpTime();
    }

    /**
     * Reloads all time data to repoduce setting after restart.
     */
    public static void loadServerTime()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GorillaBase.getAppContext());

        String prefkey = GorillaTime.class.getCanonicalName();

        long serverTime = prefs.getLong(prefkey + ".serverTime", 0);
        long deviceTime = prefs.getLong(prefkey + ".deviceTime", 0);

        if ((serverTime > 0) && (deviceTime > 0))
        {
            long timeSinceSave = deviceTimeMillis() - deviceTime;

            GorillaTime.serverTime = serverTime + timeSinceSave;
            GorillaTime.deviceTime = deviceTime + timeSinceSave;
            GorillaTime.syncedTime = SystemClock.elapsedRealtime();
            GorillaTime.timeSynced = true;

            Log.d("load server time apkname=%s", prefkey);

            dumpTime();
        }
        else
        {
            Log.d("no server time found apkname=%s", prefkey);
        }
    }

    /**
     * Get true device time.
     *
     * @return true device time in UTC milliseconds since the epoch.
     */
    public static long deviceTimeMillis()
    {
        return System.currentTimeMillis();
    }

    /**
     * Get true Gorilla server time.
     *
     * @return legacy true Gorilla server time in UTC milliseconds since the epoch.
     */
    public static long serverTimeMillis()
    {
        if (!timeSynced)
        {
            Err.errp("no server time!");

            return deviceTimeMillis();
        }

        return serverTime + (SystemClock.elapsedRealtime() - syncedTime);
    }

    /**
     * Convert device time into server time.
     *
     * @param deviceTime device time in millis.
     * @return server time in millis.
     */
    public static long serverizeTime(long deviceTime)
    {
        long timeSkew = deviceTimeMillis() - deviceTime;
        return serverTimeMillis() - timeSkew;
    }

    /**
     * Dump time for debug.
     */
    private static void dumpTime()
    {
        long nowServerTime = serverTimeMillis();
        long nowClientTime = deviceTimeMillis();

        Log.d("nowServerTime=%s", Dates.getLocalDateAndTimeMillis(nowServerTime));
        Log.d("nowClientTime=%s", Dates.getLocalDateAndTimeMillis(nowClientTime));
    }

    /**
     * Broadcast receiver someone set time and date manually.
     *
     * @param context broadcast context.
     * @param intent  broadcast intent.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if ((action != null) && action.equals(Intent.ACTION_TIME_CHANGED))
        {
            Log.e("time manually set!");
        }
    }
}
