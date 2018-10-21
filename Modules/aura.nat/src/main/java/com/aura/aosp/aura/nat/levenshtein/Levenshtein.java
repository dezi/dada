package com.aura.aosp.aura.nat.levenshtein;

public class Levenshtein
{
    static
    {
        System.loadLibrary("auranat");
    }

    public static native int levenshtein(byte[] s1, byte[] s2);
}
