package com.aura.aosp.gorilla.sysapp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;

public class TestClass
{
    private String instanceNull = null;

    @Nullable
    private String dochNull(@NonNull String nixnull)
    {
        Log.d("TestClass","nixnull=" + nixnull.length());
        return nixnull;
    }

    public void dodat()
    {
        String null1 = dochNull(null);
        String null2 = dochNull(instanceNull);
        String null3 = dochNull(null2);
    }
}
