package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aura.aosp.aura.common.gorilla.GorillaRemote;

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

    private ServiceConnection myConnection;
    private GorillaRemote myService;
    private boolean isBound;

    public GorillaClient()
    {
        super();

        myConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d(LOGTAG, "onServiceConnected:");
                myService = GorillaRemote.Stub.asInterface(service);
                isBound = true;

                sendServiceMessage();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d(LOGTAG, "onServiceDisconnected:.");
                myService = null;
                isBound = false;
            }
        };
    }

    public void bindGorillaService(Context context)
    {
        Intent intent = new Intent("com.aura.android.gorillaservice.REMOTE_CONNECT");
        intent.setPackage("com.aura.aosp.gorilla.sysapp");
        context.bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    public void sendServiceMessage()
    {
        if (!isBound) return;

        try
        {
            Log.d(LOGTAG, "sendServiceMessage: add=" + myService.addNumbers(12, 13));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    private Handler handler = new Handler();

    private OnResultReceivedListener onResultReceivedListener;
    private OnOwnerReceivedListener onOwnerReceivedListener;
    private OnMessageReceivedListener onMessageReceivedListener;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        getInstance().onReceiveInstance(context, intent);
    }

    private void onReceiveInstance(Context context, Intent intent)
    {
        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_OWNER"))
        {
            final OnOwnerReceivedListener listener = onOwnerReceivedListener;
            final String ownerUUID = intent.getStringExtra("ownerUUID");
            final JSONObject owner = new JSONObject();

            putJSON(owner, "ownerUUID", ownerUUID);

            if (listener != null)
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        listener.onOwnerReceived(owner);
                    }
                });
            }

            Log.d(LOGTAG, "onReceive: RECV_OWNER: ownerUUID=" + ownerUUID);

            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            JSONObject result = fromStringJSONOBject(intent.getStringExtra("result"));

            Log.d(LOGTAG,"onReceive: SEND_PAYLOAD_RESULT result=" + result);

            if (onResultReceivedListener != null)
            {
                onResultReceivedListener.onResultReceived(result);
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

            if (onMessageReceivedListener != null)
            {
                JSONObject message = new JSONObject();

                putJSON(message, "uuid", uuid);
                putJSON(message, "time", time);
                putJSON(message, "sender", sender);
                putJSON(message, "device", device);
                putJSON(message, "payload", payload);

                onMessageReceivedListener.onMessageReceived(message);
            }

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

    public void putJSON(JSONObject json, String key, Object val)
    {
        try
        {
            json.put(key, val);
        }
        catch (Exception ignore)
        {
        }
    }

    @Nullable
    public JSONObject fromStringJSONOBject(String jsonstr)
    {
        if (jsonstr == null) return null;

        try
        {
            return new JSONObject(jsonstr);
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
            return null;
        }
    }

    //endregion Instance implemention.
}
