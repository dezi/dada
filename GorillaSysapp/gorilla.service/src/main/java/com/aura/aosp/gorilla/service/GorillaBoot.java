package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Log;

public class GorillaBoot extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("intent=" + intent.toString());

        if ((intent.getAction() == null) || ! intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Err.errp("invalid intent.");

            return;
        }

        GorillaService.SelfStartMainService();
    }
}
