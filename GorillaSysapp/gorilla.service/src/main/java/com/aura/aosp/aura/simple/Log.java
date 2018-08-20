package com.aura.aosp.aura.simple;

public class Log
{
    public static boolean debug = true;

    public static String[] allow = new String[]
            {
            };

    public static void d(String logtag, String message)
    {
        if (debug || checkLog(allow, logtag))
        {
            android.util.Log.d(logtag, message);
        }
    }

    public static void d(String format, Object... args)
    {
        //
        // Because of da fucked log format in Android Studio 3.1
        //

        Simple.sleep(1);

        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        String logtag = "????????";

        if (elements.length > 3)
        {
            StackTraceElement ste = elements[3];

            String[] classpath = ste.getClassName().split("\\.");

            logtag = classpath[classpath.length - 1];
            format = ste.getMethodName() + ": " + format;
        }

        if (debug || checkLog(allow, logtag))
        {
            android.util.Log.d(logtag, String.format(format, args));
        }
    }

    public static void e(String format, Object... args)
    {
        //
        // Because of da fucked log format in Android Studio 3.1
        //

        Simple.sleep(1);

        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        String logtag = "????????";

        if (elements.length > 3)
        {
            StackTraceElement ste = elements[3];

            String[] classpath = ste.getClassName().split("\\.");

            logtag = classpath[classpath.length - 1];
            format = ste.getMethodName() + ": " + format;
        }

        android.util.Log.e(logtag, String.format(format, args));
    }

    private static boolean checkLog(String[] checks, String logtag)
    {
        logtag = logtag.toLowerCase();

        for (String check : checks)
        {
            check = check.toLowerCase();

            if (check.endsWith("*") && logtag.startsWith(check.substring(0, check.length() - 2)))
            {
                return true;
            }

            if (check.equalsIgnoreCase(logtag))
            {
                return true;
            }
        }

        return false;
    }
}
