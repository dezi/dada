package com.aura.aosp.gorilla.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GorillaReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        GorillaClient.getInstance(context).onReceive(intent);
    }
}
