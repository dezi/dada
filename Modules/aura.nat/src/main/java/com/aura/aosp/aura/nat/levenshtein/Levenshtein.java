package com.aura.aosp.aura.nat.levenshtein;

import android.util.Log;

public class Levenshtein
{
    private static final String LOGTAG = Levenshtein.class.getSimpleName();

    public static native int levenshtein(byte[] s1, byte[] s2);

    public static native String helloFromJNI();

    static
    {
        System.loadLibrary("auranat");
    }
}
