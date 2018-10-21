/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import android.app.Application;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.nat.hello.Hello;

import com.aura.aosp.aura.nat.levenshtein.Levenshtein;
import com.aura.aosp.gorilla.golang.GolangCorrect;
import com.aura.aosp.gorilla.golang.GolangSuggest;
import com.aura.aosp.gorilla.gomess.GomessHandler;

/**
 * The class {@code GorillaBase} is executed first
 * once any activity, broadcast, service or job
 * is started.
 * <p>
 * {@code GorillaBase} needs to be registered in manifest:
 * <p>
 * <pre>
 * {@code
 * <application android:name=".GorillaBase">
 * }
 * </pre>
 *
 * @author Dennis Zierahn
 */
public class GorillaBase extends Application
{
    /**
     * Process lifetime application context. This
     * does not leak, because it is valid while
     * the process exists.
     */
    private static Application appContext;

    /**
     * Get application context from elsewhere.
     *
     * @return application context.
     */
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

        Log.d("########################## %s", Hello.helloFromJNI());

        String str1 = "vitalitaet";
        String str2 = "vutalitaet";

        byte[] s1 = str1.getBytes();
        byte[] s2 = str2.getBytes();

        Levenshtein.levenshtein(s1, s1.length, s2, s2.length);

        //GolangSuggest.testDat();
        GolangCorrect.testDat();

        GorillaTime.loadServerTime();

        GorillaBase.startCronJob();

        GomessHandler.startService();

        GorillaLocation.startService();

        GorillaNetwork.logNetworkState();
    }

    private static void startCronJob()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
            GorillaCron.startCronJob();
        }
    }
}
