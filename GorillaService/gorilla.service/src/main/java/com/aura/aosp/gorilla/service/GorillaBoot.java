package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GorillaBoot extends BroadcastReceiver
{
    private static final String LOGTAG = GorillaBoot.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive: intent=" + intent.toString());

        if ((intent.getAction() == null) || ! intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.d(LOGTAG, "onReceive: invalid intent.");

            return;
        }

        GorillaService.SelfStartMainService(context);
    }
}
