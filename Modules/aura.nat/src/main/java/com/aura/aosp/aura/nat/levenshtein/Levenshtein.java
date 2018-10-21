package com.aura.aosp.aura.nat.levenshtein;

public class Levenshtein
{
    static
    {
        System.loadLibrary("auranat");
    }

    public static native int levenshtein(byte[] s1, int s1len, byte[] s2, int s2len);
}
