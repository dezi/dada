package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.UUID;

@SuppressWarnings("unused")
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

    private final Handler handler = new Handler();

    private final ServiceConnection serviceConnection;
    private GorillaRemote gorillaRemote;
    private boolean isBound;

    private OnResultReceivedListener onResultReceivedListener;
    private OnOwnerReceivedListener onOwnerReceivedListener;
    private OnMessageReceivedListener onMessageReceivedListener;

    public GorillaClient()
    {
        super();

        serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());
                gorillaRemote = GorillaRemote.Stub.asInterface(service);
                isBound = true;

                sendServiceMessage();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());
                gorillaRemote = null;
                isBound = false;
            }
        };
    }

    public void bindGorillaService(Context context)
    {
        Intent intent = new Intent();
        intent.setAction("com.aura.android.gorillaservice.REMOTE_CONNECT");
        intent.setPackage("com.aura.aosp.gorilla.sysapp");
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendServiceMessage()
    {
        if (!isBound) return;

        try
        {
            Log.d(LOGTAG, "sendServiceMessage: add=" + gorillaRemote.addNumbers(12, 13));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

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

            Log.d(LOGTAG, "onReceive: SEND_PAYLOAD_RESULT result=" + result);

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

            Log.d(LOGTAG, "onReceive: RECV_PAYLOAD uuid=" + uuid + " time=" + time + " sender=" + sender + " device=" + device + " payload=" + payload);

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

        UUID uuid1 = UUID.randomUUID();
        String uuid = uuid1.toString();
        long time = System.currentTimeMillis();

        Log.d(LOGTAG, "sendPayload: uuid=" + uuid);

        byte[] uuidbytes = asBytes(uuid1);
        Log.d(LOGTAG, "sendPayload: hexu=" + getHexBytesToString(uuidbytes, 0, uuidbytes.length, false));

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

    private void putJSON(JSONObject json, String key, Object val)
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
    private JSONObject fromStringJSONOBject(String jsonstr)
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

    private UUID asUuid(byte[] bytes)
    {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    private byte[] asBytes(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    private String getHexBytesToString(byte[] bytes, int offset, int length, boolean space)
    {
        if (bytes == null) return "null";
        if (bytes.length == 0) return "empty";

        int clen = (length << 1) + (space && (length > 0) ? (length - 1) : 0);

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ clen ];

        int pos = 0;

        for (int inx = offset; inx < (length + offset); inx++)
        {
            if (space && (inx > offset)) hexChars[ pos++ ] = ' ';

            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
    }

    //endregion Instance implemention.
}
