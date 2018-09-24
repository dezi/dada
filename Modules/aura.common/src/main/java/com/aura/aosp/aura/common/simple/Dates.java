package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;

public class Dates
{
    @NonNull
    public static String getLocalDateAndTime(Long timeStamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            Date netDate = new Date(timeStamp);
            return sdf.format(netDate);
        }
        catch(Exception ex)
        {
            return "19700101000000";
        }
    }

    @NonNull
    public static String getUniversalDate(Long timeStamp)
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
            return "19700101000000";
        }
    }

    @NonNull
    public static String getUniversalDateAndTime(Long timeStamp)
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
            return "19700101000000";
        }
    }

    @NonNull
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
            return "19700101000000000";
        }
    }
}
