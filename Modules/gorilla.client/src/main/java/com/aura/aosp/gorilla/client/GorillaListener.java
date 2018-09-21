package com.aura.aosp.gorilla.client;

import android.util.Log;

import org.json.JSONObject;

public class GorillaListener
{
    private static final String LOGTAG = GorillaListener.class.getSimpleName();

    public void onStatusReceived(JSONObject result)
    {
        Log.d(LOGTAG, "onStatusReceived: STUB!");
    }

    public void onOwnerReceived(JSONObject owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: STUB!");
    }

    public void onResultReceived(JSONObject result)
    {
        Log.d(LOGTAG, "onResultReceived: STUB!");
    }

    public void onMessageReceived(JSONObject message)
    {
        Log.d(LOGTAG, "onMessageReceived: STUB!");
    }
}
