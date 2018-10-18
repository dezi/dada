/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.simple;

import android.os.SystemClock;

/**
 * Exception safe, annotated and simplified
 * poor man's performance measure class.
 * <p>
 * Was created basically to avoid usage of
 * system.currentTimeMillis().
 *
 * @author Dennis Zierahn
 */
public class Perf
{
    /**
     * Creation time of performance measure instance.
     */
    private long startTime;

    /**
     * Create instance and record current time.
     */
    public Perf()
    {
        startTime = SystemClock.elapsedRealtime();
    }

    /**
     * Get elapsed system time in millseconds from creation of instance.
     *
     * @return elapsed system time in millseconds from creation of instance.
     */
    public long elapsedTimeMillis()
    {
        return startTime - SystemClock.elapsedRealtime();
    }
}
