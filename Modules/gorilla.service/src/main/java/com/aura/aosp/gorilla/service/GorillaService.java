package com.aura.aosp.gorilla.service;

import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.aura.aosp.gorilla.client.GorillaRemote;
import com.aura.aosp.gorilla.gomess.GomessHandler;

import com.aura.aosp.aura.common.simple.Log;

public class GorillaService extends Service
{
    //region Static stuff.

    //
    // Public static self start Gorilla Service via intent.
    //
    public static void SelfStartMainService()
    {
        Context context = GorillaBase.getAppContext();
        Intent serviceIntent = new Intent(context, GorillaService.class);
        context.startService(serviceIntent);

        Log.d("service started...");
    }

    //endregion Static stuff.

    //region Instance stuff.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("...");

        //return START_STICKY;
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d("...");

        //
        // Dummy fetch instance to
        // make sure it is started.
        //

        GomessHandler.getInstance();
    }

    private final Messenger myMessenger = new Messenger(new IncomingHandler());

    private static class IncomingHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle data = msg.getData();

            Log.d("data=%s", data.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d("intent=%s", intent.toString());

        return new GorillaRemote.Stub()
        {
            public int addNumbers(int int1, int int2)
            {
                return int1 + int2;
            }
        };
    }

    //endregion Instance stuff.
}
