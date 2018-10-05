package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
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
    public void onOwnerReceived(GorillaOwner owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: owner=" + owner.toString());
    }

    @Override
    public void onPayloadReceived(GorillaPayload payload)
    {
        Log.d(LOGTAG, "onPayloadReceived: message=" + payload.toString());

        //startMainActivity();
    }

    @Override
    public void onPayloadResultReceived(GorillaPayloadResult result)
    {
        Log.d(LOGTAG, "onPayloadResultReceived: result=" + result.toString());
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
