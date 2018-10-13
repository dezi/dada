package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

public class Dates
{
    @NonNull
    public static String getLocalDateAndTime(long timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date netDate = new Date(timeStamp);
        return sdf.format(netDate);
    }

    @NonNull
    public static String getLocalDateAndTimeMillis(long timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date netDate = new Date(timeStamp);
        return sdf.format(netDate);
    }

    @NonNull
    public static String getUniversalDate(long timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStamp);
        return sdf.format(netDate);
    }

    @NonNull
    public static String getUniversalDateAndTime(long timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStamp);
        return sdf.format(netDate);
    }

    @NonNull
    public static String getUniversalDateAndTimeMillis(long timeStamp)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date netDate = new Date(timeStamp);
        return sdf.format(netDate);
    }
}
