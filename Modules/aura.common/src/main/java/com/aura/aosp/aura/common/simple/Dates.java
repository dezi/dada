package com.aura.aosp.aura.common.simple;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class Dates
{
    @Nullable
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
            return null;
        }
    }

}
