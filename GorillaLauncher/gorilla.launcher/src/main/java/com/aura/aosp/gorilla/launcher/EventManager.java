package com.aura.aosp.gorilla.launcher;

import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.client.GorillaListener;

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
        Log.d("onServiceChange: connected=" + connected);
    }

    @Override
    public void onUplinkChange(boolean connected)
    {
        Log.d("onUplinkChange: connected=" + connected);

        if (connected) GorillaClient.getInstance().getAtom(UID.randomUUIDBase64());
    }

    @Override
    public void onOwnerReceived(GorillaOwner owner)
    {
        Log.d("onOwnerReceived: owner=" + owner.toString());
    }

    @Override
    public void onPayloadReceived(GorillaPayload payload)
    {
        Log.d("onPayloadReceived: message=" + payload.toString());

        //startMainActivity();
    }

    @Override
    public void onPayloadResultReceived(GorillaPayloadResult result)
    {
        Log.d("onPayloadResultReceived: result=" + result.toString());
    }

    private void startMainActivity()
    {
        Log.d("startMainActivity: ...");

        Intent startIntent = new Intent(context, StreamActivity.class);

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
