package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.util.UUID;

@SuppressLint("StaticFieldLeak")
public class GorillaClient extends BroadcastReceiver
{
    private static final String LOGTAG = GorillaClient.class.getSimpleName();

    //region Static implemention.

    private static final Object mutex = new Object();
    private static GorillaClient instance;

    public static GorillaClient getInstance()
    {
        if (instance == null)
        {
            synchronized (mutex)
            {
                if (instance == null)
                {
                    instance = new GorillaClient();
                }
            }
        }

        return instance;
    }

    public static class OnResultReceivedListener
    {
        public void onResultReceived(JSONObject result)
        {
            Log.d(LOGTAG, "onResultReceived: STUB!");
        }
    }

    public static class OnOwnerReceivedListener
    {
        public void onOwnerReceived(JSONObject owner)
        {
            Log.d(LOGTAG, "onOwnerReceived: STUB!");
        }
    }

    public static class OnMessageReceivedListener
    {
        public void onMessageReceived(JSONObject message)
        {
            Log.d(LOGTAG, "onMessageReceived: STUB!");
        }
    }

    //endregion Static implemention.

    //region Instance implemention.

    private OnResultReceivedListener onResultReceivedListener;
    private OnOwnerReceivedListener onOwnerReceivedListener;
    private OnMessageReceivedListener onMessageReceivedListener;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            String result = intent.getStringExtra("result");

            Log.d(LOGTAG,"onReceive: SEND_PAYLOAD_RESULT result=" + result);

            if (onResultReceivedListener != null)
            {
                onResultReceivedListener.onResultReceived(fromStringJSONOBject(result));
            }

            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_PAYLOAD"))
        {
            long time = intent.getLongExtra("time", -1);

            String uuid = intent.getStringExtra("uuid");
            String sender = intent.getStringExtra("sender");
            String device = intent.getStringExtra("device");
            String payload = intent.getStringExtra("payload");

            Log.d(LOGTAG,"onReceive: RECV_PAYLOAD uuid=" + uuid + " time=" + time + " sender=" + sender + " device=" + device + " payload=" + payload);

            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_OWNER"))
        {
            String ownerUUID = intent.getStringExtra("ownerUUID");

            Log.d(LOGTAG, "onReceive: RECV_OWNER: ownerUUID=" + ownerUUID);

            getInstance().setOwner(ownerUUID);

            return;
        }

        //
        // Silently ignore.
        //

        Log.d(LOGTAG, "onReceive: wrong action.");
    }

    public void wantOwner(Context context)
    {
        Intent requestIntent = new Intent();

        requestIntent.setPackage("com.aura.aosp.gorilla.sysapp");
        requestIntent.setAction("com.aura.aosp.gorilla.service.WANT_OWNER");
        requestIntent.putExtra("apkname", context.getPackageName());

        Log.d(LOGTAG, "wantOwner: requestIntent=" + requestIntent.toString());

        context.sendBroadcast(requestIntent);
    }

    public void sendPayload(Context context, String receiver, String device, String payload)
    {
        JSONObject result = new JSONObject();

        String uuid = UUID.randomUUID().toString();
        long time = System.currentTimeMillis();

        putJSON(result, "uuid", uuid);
        putJSON(result, "time", time);
        putJSON(result, "status", "pending");

        Intent requestIntent = new Intent();

        requestIntent.setPackage("com.aura.aosp.gorilla.sysapp");
        requestIntent.setAction("com.aura.aosp.gorilla.service.SEND_PAYLOAD");

        requestIntent.putExtra("uuid", uuid);
        requestIntent.putExtra("time", time);
        requestIntent.putExtra("apkname", context.getPackageName());
        requestIntent.putExtra("receiver", receiver);
        requestIntent.putExtra("device", device);
        requestIntent.putExtra("payload", payload);

        Log.d(LOGTAG, "sendPayload: requestIntent=" + requestIntent.toString());

        context.sendBroadcast(requestIntent);

        if (onResultReceivedListener != null)
        {
            onResultReceivedListener.onResultReceived(result);
        }
    }

    public void setOnResultReceivedListener(OnResultReceivedListener onResultReceivedListener)
    {
        this.onResultReceivedListener = onResultReceivedListener;
    }

    public void setOnOwnerReceivedListener(OnOwnerReceivedListener onOwnerReceivedListener)
    {
        this.onOwnerReceivedListener = onOwnerReceivedListener;
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener)
    {
        this.onMessageReceivedListener = onMessageReceivedListener;
    }

    private void setOwner(String ownerUUID)
    {
    }

    public void putJSON(JSONObject json, String key, Object val)
    {
        try
        {
            json.put(key, val);
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }
    }

    public JSONObject fromStringJSONOBject(String jsonstr)
    {
        if (jsonstr != null)
        {
            try
            {
                return new JSONObject(jsonstr);
            }
            catch (Exception ex)
            {
                Log.d(LOGTAG, ex.toString());
            }
        }

        return new JSONObject();
    }

    //endregion Instance implemention.
}
