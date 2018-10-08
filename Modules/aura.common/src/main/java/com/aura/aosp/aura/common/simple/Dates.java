package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

public class Dates
{
    @Nullable
    public static String getLocalDateAndTime(@NonNull Long timeStamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date netDate = new Date(timeStamp);
            return sdf.format(netDate);
        }
        catch(Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public static String getUniversalDate(@NonNull Long timeStamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date netDate = new Date(timeStamp);
            return sdf.format(netDate);
        }
        catch(Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public static String getUniversalDateAndTime(@NonNull Long timeStamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date netDate = new Date(timeStamp);
            return sdf.format(netDate);
        }
        catch(Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @Nullable
    public static String getUniversalDateAndTimeMillis(@NonNull Long timeStamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date netDate = new Date(timeStamp);

            return sdf.format(netDate);
        }
        catch(Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    public static long getAgeInSeconds(long timestamp)
    {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }
}
