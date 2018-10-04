package com.aura.aosp.gorilla.client;

import android.util.Log;

import org.json.JSONObject;

public class GorillaListener
{
    private final static String LOGTAG = GorillaListener.class.getSimpleName();

    public void onServiceChange(boolean connected)
    {
        Log.d(LOGTAG, "onServiceChange: STUB!");
    }

    public void onUplinkChange(boolean connected)
    {
        Log.d(LOGTAG, "onUplinkChange: STUB!");
    }

    public void onOwnerReceived(JSONObject owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: STUB!");
    }

    public void onMessageReceived(JSONObject message)
    {
        Log.d(LOGTAG, "onMessageReceived: STUB!");
    }

    public void onResultReceived(JSONObject result)
    {
        Log.d(LOGTAG, "onResultReceived: STUB!");
    }
}
