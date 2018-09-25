package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

public class GorillaBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("intent=" + intent.toString());

        if (! Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Err.errp("invalid intent.");
        }
    }
}
