package com.aura.aosp.gorilla.golang;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Environment;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Simple;

import java.io.File;
import java.io.RandomAccessFile;

@SuppressWarnings("unused")
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
     * @param s2 second string.
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

    /**
     * Compute Levenshtein distance between two string in byte array form.
     * <p>
     * Recommended method if data comes from byte arrays because converting
     * byte array to strings is expensive.
     *
     * @param s1    first byte array string.
     * @param s1len number of bytes to use.
     * @param s2    second byte array string.
     * @param s2len number of bytes to use.
     * @return distance or null on error.
     */
    public static Integer levenshteinOld(byte[] s1, int s1len, byte[] s2, int s2len)
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
            if ((rows > 0) && (s1[inx] & 0xc0) == 0x80)
            {
                r1[rows - 1] = (r1[rows - 1] << 8) + s1[inx];

                continue;
            }

            r1[rows++] = s1[inx];
        }

        for (int inx = 0; inx < s2len; inx++)
        {
            if ((cols > 0) && (s2[inx] & 0xc0) == 0x80)
            {
                r2[cols - 1] = (r2[cols - 1] << 8) + s2[inx];

                continue;
            }

            r2[cols++] = s2[inx];
        }

        //
        // Start computing distance.
        //

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
     * Compute Levenshtein distance between two string in byte array form.
     * <p>
     * Recommended method if data comes from byte arrays because converting
     * byte array to strings is expensive.
     *
     * @param s1    first byte array string.
     * @param s1len number of bytes to use.
     * @param s2    second byte array string.
     * @param s2len number of bytes to use.
     * @return distance or null on error.
     */
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
            if ((rows > 0) && (s1[inx] & 0xc0) == 0x80)
            {
                r1[rows - 1] = (r1[rows - 1] << 8) + s1[inx];

                continue;
            }

            r1[rows++] = s1[inx];
        }

        for (int inx = 0; inx < s2len; inx++)
        {
            if ((cols > 0) && (s2[inx] & 0xc0) == 0x80)
            {
                r2[cols - 1] = (r2[cols - 1] << 8) + s2[inx];

                continue;
            }

            r2[cols++] = s2[inx];
        }

        int[] v0 = new int[cols + 1];
        int[] v1 = new int[cols + 1];
        int[] vt;

        for (int i = 0; i <= cols; i++)
        {
            v0[i] = i;
        }

        int m1;
        int dc;
        int ic;
        int sc;

        for (int i = 0; i < rows; i++)
        {
            v1[0] = i + 1;

            for (int j = 0; j < cols; j++)
            {
                dc = v0[j + 1] + 1;
                ic = v1[j] + 1;

                if (r1[i] == r2[j])
                {
                    sc = v0[j];
                }
                else
                {
                    sc = v0[j] + 1;
                }

                m1 = (dc < ic) ? dc : ic;

                v1[j + 1] = m1 < sc ? m1 : sc;
            }

            vt = v0;
            v0 = v1;
            v1 = vt;
        }

        return v0[cols];
    }

    /**
     * Get real UTF-8 rune length from byte array.
     * <p>
     * This method is faster than a call to
     * <p>
     * new String(byteString, 0, byteLength).length()
     *
     * @param byteString byte array with UTF-8 content.
     * @param byteLength usable length of byte array.
     * @return number of runes in string.
     */
    static int getRuneLength(byte[] byteString, int byteLength)
    {
        if (byteString == null)
        {
            Err.errp();
            return 0;
        }

        if (byteString.length < byteLength)
        {
            Err.errp("wrong length!");
            return 0;
        }

        int runeLenght = 0;

        for (int inx = 0; inx < byteLength; inx++)
        {
            if ((byteString[inx] & 0xc0) == 0x80)
            {
                //
                // Trailing UTF-8 character does not count.
                //

                continue;
            }

            runeLenght++;
        }

        return runeLenght;
    }

    /**
     * Helper class for maintaining a score.
     */
    static class Score
    {
        /**
         * The phrase.
         */
        final String phrase;

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
