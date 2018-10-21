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
import com.aura.aosp.aura.common.simple.Perf;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.nat.hello.Hello;

import com.aura.aosp.aura.nat.levenshtein.Levenshtein;
import com.aura.aosp.aura.nat.prime.Prime;
import com.aura.aosp.gorilla.golang.GolangCorrect;
import com.aura.aosp.gorilla.golang.GolangSuggest;
import com.aura.aosp.gorilla.golang.GolangUtils;
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

        String str1 = "vitalitaetmonsterbauunterführungsassistent";
        String str2 = "vitalitaetmonsterbauunterführungsassistent";

        byte[] s1 = str1.getBytes();
        byte[] s2 = str2.getBytes();

        Perf cpp = new Perf();
        for (int inx = 0; inx < 10000; inx++)
        {
            Levenshtein.levenshtein(s1, s1.length, s2, s2.length);
        }
        Log.d("cpp=%s", cpp.elapsedTimeMillis());

        Perf jav = new Perf();
        for (int inx = 0; inx < 10000; inx++)
        {
            GolangUtils.levenshtein(s1, s1.length, s2, s2.length);
        }
        Log.d("jav=%s", jav.elapsedTimeMillis());

        Perf primeJav = new Perf();
        Prime.isPrimeJava(2760889966651L);
        Log.d("jav=%s", primeJav.elapsedTimeMillis());

        Perf primeCpp = new Perf();
        Prime.isPrimeCpp(2760889966651L);
        Log.d("cpp=%s", primeCpp.elapsedTimeMillis());

        //GolangSuggest.testDat();
        //GolangCorrect.testDat();

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
