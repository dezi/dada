package com.aura.aosp.aura.nat.hello;

public class Hello
{
    static
    {
        System.loadLibrary("auranat");
    }

    public static native String helloFromJNI();
}
