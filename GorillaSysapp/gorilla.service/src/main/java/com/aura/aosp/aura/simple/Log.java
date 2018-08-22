package com.aura.aosp.aura.simple;

public class Log
{
    public static boolean debug = true;

    public static String[] allow = new String[]
            {
            };

    public static void d(String format, Object... args)
    {
        d(4,format,args);
    }

    public static void derr(String format, Object... args)
    {
        d(5,format,args);
    }

    private static void d(int index, String format, Object... args)
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

        if (debug || checkLog(allow, logtag))
        {
            android.util.Log.d(logtag, String.format(format, args));
        }
    }

    public static void eerr(String format, Object... args)
    {
        e(4,format,args);
    }

    public static void e(String format, Object... args)
    {
        e(3,format,args);
    }

    private static void e(int index, String format, Object... args)
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
