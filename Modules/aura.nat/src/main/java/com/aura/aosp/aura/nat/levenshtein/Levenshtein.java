package com.aura.aosp.aura.nat.levenshtein;

import android.support.annotation.Nullable;

public class Levenshtein
{
    static
    {
        System.loadLibrary("auranat");
    }

    private static native int levenshteinCPP(byte[] s1, int s1len, byte[] s2, int s2len);

    @Nullable
    public static Integer levenshtein(byte[] s1, int s1len, byte[] s2, int s2len)
    {
        int dist = levenshteinCPP(s1, s1len, s2, s2len);
        return (dist < 0) ? null : dist;
    }
}
