package com.aura.aosp.gorilla.golang;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;

import java.io.File;
import java.io.RandomAccessFile;

public class GolangUtils
{
    private final static Object mutex = new Object();

    @Nullable
    static File getStorageDir(@NonNull String language, @NonNull String area, boolean create)
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

    /**
     * UTF-8 safe read line from random access file.
     * <p>
     * Max line size is READSIZE.
     *
     * @return UTF-8 string or null on error.
     */
    @Nullable
    static String readLineUTFSafe(RandomAccessFile raFile)
    {
        byte[] bytes = new byte[2048];
        int xfer = 0;

        try
        {
            while (true)
            {
                byte byt = raFile.readByte();
                if (byt == '\n') break;

                bytes[xfer++] = byt;
                if (xfer >= bytes.length) break;
            }

            return new String(bytes, 0, xfer);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    static int levenshtein(byte[] s1, int s1len, byte[] s2, int s2len)
    {
        int rows = s1len;
        int cols = s2len;
        int rown = rows + 1;
        int coln = cols + 1;

        int d1;
        int d2;
        int d3;
        int m1;
        int i;
        int j;

        int[] dist = new int[rown * coln];

        for (i = 0; i < rown; i++)
        {
            dist[i * coln] = i;
        }

        for (j = 0; j < coln; j++)
        {
            dist[j] = j;
        }

        for (j = 0; j < cols; j++)
        {
            for (i = 0; i < rows; i++)
            {
                if (s1[i] == s2[j])
                {
                    dist[((i + 1) * coln) + (j + 1)] = dist[(i * coln) + j];
                }
                else
                {
                    d1 = dist[(i * coln) + (j + 1)] + 1;
                    d2 = dist[((i + 1) * coln) + j] + 1;
                    d3 = dist[(i * coln) + j] + 1;

                    m1 = (d2 < d3) ? d2 : d3;

                    dist[((i + 1) * coln) + (j + 1)] = (d1 < m1) ? d1 : m1;
                }
            }
        }

        return dist[(coln * rown) - 1];
    }

    static int levenshtein(String s1, String s2)
    {
        int rows = s1.length();
        int cols = s2.length();
        int rown = rows + 1;
        int coln = cols + 1;

        int d1;
        int d2;
        int d3;
        int m1;
        int i;
        int j;

        int[] dist = new int[rown * coln];

        for (i = 0; i < rown; i++)
        {
            dist[i * coln] = i;
        }

        for (j = 0; j < coln; j++)
        {
            dist[j] = j;
        }

        for (j = 0; j < cols; j++)
        {
            for (i = 0; i < rows; i++)
            {
                if (s1.charAt(i) == s2.charAt(j))
                {
                    dist[((i + 1) * coln) + (j + 1)] = dist[(i * coln) + j];
                }
                else
                {
                    d1 = dist[(i * coln) + (j + 1)] + 1;
                    d2 = dist[((i + 1) * coln) + j] + 1;
                    d3 = dist[(i * coln) + j] + 1;

                    m1 = (d2 < d3) ? d2 : d3;

                    dist[((i + 1) * coln) + (j + 1)] = (d1 < m1) ? d1 : m1;
                }
            }
        }

        return dist[(coln * rown) - 1];
    }

    /**
     * Helper class for maintaining a score.
     */
    static class Score
    {
        /**
         * The phrase.
         */
        String phrase;

        /**
         * The score.
         */
        int score;

        /**
         * Create Score object.
         *
         * @param phrase the phrase.
         * @param score  the score.
         */
        Score(String phrase, int score)
        {
            this.phrase = phrase;
            this.score = score;
        }
    }
}
