package com.aura.aosp.aura.nlp.suggest;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Levenshtein
{
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
        return levenshtein(s1,s2,-1);
    }

    /**
     * Compute Levenshtein distance between two strings.
     *
     * @param s1 first string.
     * @param s2 second string.
     * @return distance or null on error.
     */
    @Nullable
    public static Integer levenshtein(String s1, String s2, int maxdist)
    {
        if ((s1.length() > 48) || (s2.length() > 48))
        {
            Err.errp("string length too big s1=%s s2=%d", s1.length(), s2.length());

            return null;
        }

        int rows = s1.length();
        int cols = s2.length();

        int[] v0 = new int[cols + 1];
        int[] v1 = new int[cols + 1];
        int[] vt;

        for (int i = 0; i <= cols; i++)
        {
            v0[i] = i;
        }

        int m1;
        int m2;
        int m3;
        int dc;
        int ic;
        int sc;

        for (int i = 0; i < rows; i++)
        {
            m3 = maxdist + 1;

            v1[0] = i + 1;

            for (int j = 0; j < cols; j++)
            {
                dc = v0[j + 1] + 1;
                ic = v1[j] + 1;

                if (s1.charAt(i) == s2.charAt(j))
                {
                    sc = v0[j];
                }
                else
                {
                    sc = v0[j] + 1;
                }

                m1 = (dc < ic) ? dc : ic;
                m2 = (m1 < sc) ? m1 : sc;
                m3 = (m3 < m2) ? m3 : m2;

                v1[j + 1] = m2;
            }

            if ((maxdist >= 0) && (m3 > maxdist))
            {
                //
                // Things can only become worse.
                //

                return m3;
            }

            vt = v0;
            v0 = v1;
            v1 = vt;
        }

        return v0[cols];
    }

    /**
     * Compute Levenshtein distance between two strings in byte array form.
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
    @Nullable
    public static Integer levenshtein(byte[] s1, int s1len, byte[] s2, int s2len)
    {
        return levenshtein(s1, s1len, s2, s2len, -1);
    }

    /**
     * Compute Levenshtein distance between two strings in byte array form.
     * <p>
     * Recommended method if data comes from byte arrays because converting
     * byte array to strings is expensive.
     *
     * @param s1      first byte array string.
     * @param s1len   number of bytes to use.
     * @param s2      second byte array string.
     * @param s2len   number of bytes to use.
     * @param maxdist maximum distance desired or -1.
     * @return distance or null on error.
     */
    @Nullable
    public static Integer levenshtein(byte[] s1, int s1len, byte[] s2, int s2len, int maxdist)
    {
        int[] r1 = new int[s1len];
        int[] r2 = new int[s2len];

        int r1len = Utils.getRunesFromBytes(r1, s1, s1len);
        int r2len = Utils.getRunesFromBytes(r2, s2, s2len);

        return levenshtein(r1, r1len, r2, r2len, maxdist);
    }

    /**
     * Compute Levenshtein distance between two string in rune array form.
     * <p>
     * Recommended method if data comes from byte arrays because converting
     * byte array to strings is expensive.
     *
     * @param r1      first rune array string.
     * @param r1len   number of runes to use.
     * @param r2      second rune array string.
     * @param r2len   number of runes to use.
     * @param maxdist maximum distance desired or -1.
     * @return distance or null on error.
     */
    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static Integer levenshtein(int[] r1, int r1len, int[] r2, int r2len, int maxdist)
    {
        if ((r1len > 48) || (r2len > 48))
        {
            Err.errp("rune length too big s1=%s s2=%d", r1len, r2len);

            return null;
        }

        int rows = r1len;
        int cols = r2len;

        int[] v0 = new int[cols + 1];
        int[] v1 = new int[cols + 1];
        int[] vt;

        for (int i = 0; i <= cols; i++)
        {
            v0[i] = i;
        }

        int m1;
        int m2;
        int m3;
        int dc;
        int ic;
        int sc;

        for (int i = 0; i < rows; i++)
        {
            m3 = maxdist + 1;

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
                m2 = (m1 < sc) ? m1 : sc;
                m3 = (m3 < m2) ? m3 : m2;

                v1[j + 1] = m2;
            }

            if ((maxdist >= 0) && (m3 > maxdist))
            {
                //
                // Things can only become worse.
                //

                return m3;
            }

            vt = v0;
            v0 = v1;
            v1 = vt;
        }

        return v0[cols];
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
    @Nullable
    private static Integer levenshteinSlow(byte[] s1, int s1len, byte[] s2, int s2len)
    {
        if ((s1len > 48) || (s2len > 48))
        {
            Err.errp("string length too big s1=%s s2=%d", s1len, s2len);

            return null;
        }

        int[] r1 = new int[s1len];
        int[] r2 = new int[s2len];

        int rows = Utils.getRunesFromBytes(r1, s1, s1len);
        int cols = Utils.getRunesFromBytes(r2, s2, s2len);

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

                    m1 = (d1 < d2) ? d1 : d2;

                    dist[((i + 1) * coln) + (j + 1)] = (m1 < d3) ? m1 : d3;
                }
            }
        }

        return dist[(coln * rown) - 1];
    }

}
