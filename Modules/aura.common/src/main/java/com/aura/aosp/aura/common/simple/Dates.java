/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

/**
 * Exception safe, annotated, simplified
 * and locale neutral date string methods
 * for file paths etc.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Dates
{
    /**
     * Get local date and time string (yyyyMMddHHmmss).
     *
     * @param timeStampMillis milliseconds since the epoch.
     * @return local date and time string.
     */
    @NonNull
    public static String getLocalDateAndTime(long timeStampMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date netDate = new Date(timeStampMillis);
        return sdf.format(netDate);
    }

    /**
     * Get local date and time string with milliseconds (yyyyMMddHHmmssSSS).
     *
     * @param timeStampMillis milliseconds since the epoch.
     * @return local date and time string with milliseconds.
     */
    @NonNull
    public static String getLocalDateAndTimeMillis(long timeStampMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date netDate = new Date(timeStampMillis);
        return sdf.format(netDate);
    }

    /**
     * Get UTC date string (yyyyMMdd).
     *
     * @param timeStampMillis milliseconds since the epoch.
     * @return UTC date string.
     */
    @NonNull
    public static String getUniversalDate(long timeStampMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStampMillis);
        return sdf.format(netDate);
    }

    /**
     * Get UTC date and time string (yyyyMMddHHmmss).
     *
     * @param timeStampMillis milliseconds since the epoch.
     * @return UTC date and time string.
     */
    @NonNull
    public static String getUniversalDateAndTime(long timeStampMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStampMillis);
        return sdf.format(netDate);
    }

    /**
     * Get UTC date and time string with milliseconds (yyyyMMddHHmmssSSS).
     *
     * @param timeStampMillis milliseconds since the epoch.
     * @return UTC date and time string with milliseconds.
     */
    @NonNull
    public static String getUniversalDateAndTimeMillis(long timeStampMillis)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStampMillis);
        return sdf.format(netDate);
    }
}
