package com.aura.aosp.gorilla.messenger;

import android.util.Log;

import com.aura.aosp.gorilla.client.GorillaListener;

import org.json.JSONObject;

public class EventManager extends GorillaListener
{
    private static final String LOGTAG = EventManager.class.getSimpleName();

    @Override
    public void onStatusReceived(JSONObject status)
    {
        Log.d(LOGTAG, "onStatusReceived: status=" + status.toString());
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
    }

    @Override
    public void onResultReceived(JSONObject result)
    {
        Log.d(LOGTAG, "onResultReceived: result=" + result.toString());
    }
}
