package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;

import org.json.JSONObject;

public class EventManager extends GorillaListener
{
    private static final String LOGTAG = EventManager.class.getSimpleName();

    private final Context context;

    public EventManager(Context context)
    {
        this.context = context;
    }

    @Override
    public void onServiceChange(boolean connected)
    {
        Log.d(LOGTAG, "onServiceChange: connected=" + connected);
    }

    @Override
    public void onUplinkChange(boolean connected)
    {
        Log.d(LOGTAG, "onUplinkChange: connected=" + connected);
    }

    @Override
    public void onOwnerReceived(JSONObject owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: owner=" + owner.toString());
    }

    @Override
    public void onMessageReceived(JSONObject message)
    {
        Log.d(LOGTAG, "onMessageReceived: message=" + message.toString());

        //startMainActivity();
    }

    @Override
    public void onMessageResultReceived(JSONObject result)
    {
        Log.d(LOGTAG, "onMessageResultReceived: result=" + result.toString());
    }

    private void startMainActivity()
    {
        Log.d(LOGTAG, "startMainActivity: ...");

        Intent startIntent = new Intent(context, MainActivity.class);

        try
        {
            context.startActivity(startIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
