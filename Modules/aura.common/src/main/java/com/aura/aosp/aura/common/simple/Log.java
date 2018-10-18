package com.aura.aosp.aura.common.simple;

import java.util.Locale;

public class Log
{
    private final static boolean debug = true;

    private final static String[] allow = new String[]{ "*" };

    public static void d(String format, Object... args)
    {
        d(4,format,args);
    }

    @SuppressWarnings("SameParameterValue")
    private static void d(int index, String format, Object... args)
    {
        try
        {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();

            String logtag = "????????";

            if (elements.length > index)
            {
                StackTraceElement ste = elements[index];

                String[] classpath = ste.getClassName().split("\\.");

                logtag = classpath[classpath.length - 1];
                format = ste.getMethodName() + ": " + format;
            }

            if (debug || checkLog(logtag))
            {
                android.util.Log.d(logtag, String.format(Locale.ROOT, format, args));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void e(String format, Object... args)
    {
        e(4,format,args);
    }

    public static void eerr(String format, Object... args)
    {
        e(5,format,args);
    }

    private static void e(int index, String format, Object... args)
    {
        try
        {
            //
            // Sleep because of da fucked log format in Android Studio 3.1
            //

            Simple.sleep(5);

            StackTraceElement[] elements = Thread.currentThread().getStackTrace();

            String logtag = "????????";

            if (elements.length > index)
            {
                StackTraceElement ste = elements[index];

                String[] classpath = ste.getClassName().split("\\.");

                logtag = classpath[classpath.length - 1];
                format = ste.getMethodName() + ": " + format;
            }

            android.util.Log.e(logtag, String.format(Locale.ROOT, format, args));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static boolean checkLog(String logtag)
    {
        logtag = logtag.toLowerCase();

        for (String check : allow)
        {
            check = check.toLowerCase();

            if (check.equals("*"))
            {
                return true;
            }

            if (check.equalsIgnoreCase(logtag))
            {
                return true;
            }

            if (check.endsWith("*") && (check.length() > 2) && logtag.startsWith(check.substring(0, check.length() - 2)))
            {
                return true;
            }
        }

        return false;
    }
}
