package com.aura.aosp.gorilla.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = StartupReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ((intent.getAction() == null) || ! intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            return;
        }

        Log.d(LOGTAG, "onReceive: intent=" + intent.toString());

        MainService.SelfStartMainService(context);
    }
}
