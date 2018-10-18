package com.aura.aosp.gorilla.golang;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Environment;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Perf;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.simple.Err;

import java.io.File;
import java.io.RandomAccessFile;

public class GolangSuggest
{
    private final static Object mutex = new Object();

    @Nullable
    private static File getStorageDir(@NonNull String language, @NonNull String area, boolean create)
    {
        //noinspection ConstantConditions
        if ((language == null) || (area == null))
        {
            Err.errp();
            return null;
        }

        File appfilesdir = Environment.getExternalStorageDirectory();
        File golangdir = new File(appfilesdir, "golang");
        File langdir = new File(golangdir, language);
        File areadir = new File(langdir, area);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(appfilesdir, golangdir, langdir, areadir);
                if (err != null) return null;
            }
        }

        return areadir;
    }

    private final static int BUFFSIZE = 2048;
    private static RandomAccessFile rafFile;
    private static long rafSize;

    public static void testDat()
    {
        File suggestDir = getStorageDir("de", "suggest", false);
        File suggestFile = new File(suggestDir, "suggest.json");

        try
        {
            Perf startTime = new Perf();

            rafFile = new RandomAccessFile(suggestFile, "r");
            rafSize = rafFile.length();

            byte[] buffer = new byte[BUFFSIZE];

            while (true)
            {
                int xfer = rafFile.read(buffer);
                if (xfer <= 0) break;
            }

            Log.d("elapsed=%d", startTime.elapsedTimeMillis());

            startTime = new Perf();
            String result = searchPhrase("Abschnitt");
            Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result);

            startTime = new Perf();
            result = searchPhrase("vernahm");
            Log.d("perf=%d result=%s", startTime.elapsedTimeMillis(), result);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }
    }

    @Nullable
    private static String searchPhrase(String phrase)
    {
        long top = 0;
        long bot = rafSize - BUFFSIZE;

        byte[] buffer = new byte[BUFFSIZE];

        try
        {
            for (int test = 0; top < bot && test < 20; test++)
            {
                long middle = top + ((bot - top) >> 1);

                //Log.d("seek top=%d bot=%d middle=%d", top, bot, middle);

                rafFile.seek(middle);
                int xfer = rafFile.read(buffer);
                if (xfer != BUFFSIZE) break;

                String first = getFirstPhrase(buffer);
                if (first == null)
                {
                    Log.d("fucked up 1.");
                    break;
                }

                if (first.compareTo(phrase) > 0)
                {
                    bot = middle + firstStart;
                    continue;
                }

                String last = getLastPhrase(buffer);
                if (last == null)
                {
                    Log.d("fucked up 2.");
                    break;
                }

                if (last.compareTo(phrase) < 0)
                {
                    top = middle + lastEnd;
                    continue;
                }

                //
                // Must be inside buffer or is missing.
                //

                String bufstr = new String(buffer, firstStart, lastEnd - firstStart);

                //Log.d("fund bufstr=%s", bufstr);

                String[] targetPhrases = bufstr.split("\n");
                String matchPhrase = phrase + ":";

                for (String targetPhrase : targetPhrases)
                {
                    if (targetPhrase.startsWith(matchPhrase))
                    {
                        return targetPhrase;
                    }
                }

                break;
            }
        }
        catch (Exception ex)
        {
            Err.errp(ex);
        }

        return null;
    }

    private static int firstStart;
    private static int lastEnd;

    @Nullable
    private static String getFirstPhrase(byte[] buffer)
    {
        int start = 0;

        while (start < BUFFSIZE)
        {
            if (buffer[start] == '\n') break;
            start++;
        }

        firstStart = start + 1;

        if (++start > BUFFSIZE) return null;

        int end = start;

        while (end < BUFFSIZE)
        {
            if (buffer[end] == ':') break;
            end++;
        }

        if (++end > BUFFSIZE) return null;

        return new String(buffer, start, end - start);
    }

    @Nullable
    private static String getLastPhrase(byte[] buffer)
    {
        int start = BUFFSIZE - 1;

        while (start > 0)
        {
            if (buffer[start] == '\n') break;
            start--;
        }

        lastEnd = start;

        start--;

        if (start <= 0) return null;

        while (start > 0)
        {
            if (buffer[start] == '\n') break;
            start--;
        }

        if (start <= 0) return null;

        start++;

        int end = start;

        while (end < BUFFSIZE)
        {
            if (buffer[end] == ':') break;
            end++;
        }

        if (++end > BUFFSIZE) return null;

        return new String(buffer, start, end - start);
    }
}