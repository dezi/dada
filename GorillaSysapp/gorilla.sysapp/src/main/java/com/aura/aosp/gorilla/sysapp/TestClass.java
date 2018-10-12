package com.aura.aosp.gorilla.sysapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Log;

public class TestClass
{
    public String funzNull = null;

    @Nullable
    public String dochNull(@NonNull String nixnull)
    {
        Log.d("TestClass","nixnull=" + nixnull.length());
        return nixnull;
    }

    public void dodat()
    {
        String wasnull = dochNull(funzNull);

        Log.d("TestClass", "dodat: 1");

        String nextnull = dochNull(wasnull);

        Log.d("TestClass", "dodat: 2");

    }
}
