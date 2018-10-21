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

    /**
     * Compute Levenshtein distance between two strings.
     *
     * @param s1 first string.
     * @param s2 seconde string.
     * @return distance or null on error.
     */
    @Nullable
    public static Integer levenshtein(String s1, String s2)
    {
        if ((s1.length() > 48) || (s2.length() > 48))
        {
            Err.errp("string length too big s1=%s s2=%d", s1.length(), s2.length());

            return null;
        }

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

    public static Integer levenshtein(byte[] s1, int s1len, byte[] s2, int s2len)
    {
        if ((s1len > 48) || (s2len > 48))
        {
            Err.errp("string length too big s1=%s s2=%d", s1len, s2len);

            return null;
        }

        //
        // Convert byte array string into UTF-8 runes array.
        //
        // UTF-8 encoding:
        //
        // 0000 0000 – 0000 007F 	0xxxxxxx
        // 0000 0080 – 0000 07FF 	110xxxxx 10xxxxxx
        // 0000 0800 – 0000 FFFF 	1110xxxx 10xxxxxx 10xxxxxx
        // 0001 0000 – 0010 FFFF 	11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
        //

        int[] r1 = new int[s1len];
        int[] r2 = new int[s2len];

        int rows = 0;
        int cols = 0;

        for (int inx = 0; inx < s1len; inx++)
        {
            if ((rows > 0) && (s1[ inx ] & 0xc0) == 0x80)
            {
                r1[ rows - 1 ] = (r1[ rows - 1 ] << 8) + s1[ inx ];

                continue;
            }

            r1[ rows++ ] = s1[ inx ];
        }

        for (int inx = 0; inx < s2len; inx++)
        {
            if ((cols > 0) && (s2[ inx ] & 0xc0) == 0x80)
            {
                r2[ cols - 1 ] = (r2[ cols - 1 ] << 8) + s2[ inx ];

                continue;
            }

            r2[ cols++ ] = s2[ inx ];
        }

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
                if (r1[i] == r2[j])
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
