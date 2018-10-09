/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

/**
 * The class {@code GorillaBoot} receives an
 * event when the device is rebootet.
 *
 * Basically the Gorilla system app is started
 * and with the help of the {@code GorillaCron}
 * class, stays alive.
 * <p>
 * {@code GorillaBoot} needs the following permission in manifest:
 * <p>
 * <pre>
 * {@code
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * }
 * </pre>
 * <p>
 * {@code GorillaBoot} needs to be registered in manifest:
 * <p>
 * <pre>
 * {@code
 * <receiver android:name=".GorillaBoot">
 *     <intent-filter>
 *         <action android:name="android.intent.action.BOOT_COMPLETED" />
 *         <category android:name="android.intent.category.HOME" />
 *     </intent-filter>
 * </receiver>
 * }
 * </pre>
 *
 * @author Dennis Zierahn
 */
public class GorillaBoot extends BroadcastReceiver
{
    /**
     * Dummy methode w/o function. The main work of
     * connecting to the Gorilla cloud service is done
     * in the base application class.
     *
     * @param context calling context.
     * @param intent calling intent.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("intent=" + intent.toString());

        if (! Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Err.errp("invalid intent.");
        }
    }
}
