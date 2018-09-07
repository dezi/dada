package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.UUID;

@SuppressWarnings("unused")
@SuppressLint("StaticFieldLeak")
public class GorillaClient
{
    private static final String LOGTAG = GorillaClient.class.getSimpleName();

    //region Static implemention.

    private static GorillaClient instance = new GorillaClient();

    public static GorillaClient getInstance()
    {
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

    private Context context;
    private Handler handler;

    private ServiceConnection serviceConnection;
    private IGorillaRemote gorillaRemote;
    private String ownerUUID;

    private String apkname;
    private boolean validated;
    private byte[] clientSecret;
    private byte[] serverSecret;

    private OnResultReceivedListener onResultReceivedListener;
    private OnOwnerReceivedListener onOwnerReceivedListener;
    private OnMessageReceivedListener onMessageReceivedListener;

    public void bindGorillaService(Context context)
    {
        Log.d(LOGTAG, "bindGorillaService: ...");

        this.context = context;
        this.handler = new Handler();
        this.apkname = context.getPackageName();

        serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());

                gorillaRemote = IGorillaRemote.Stub.asInterface(service);

                sendClientSecret();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());

                gorillaRemote = null;

                validated = false;
                clientSecret = null;
                serverSecret = null;

                handler.post(serviceConnector);
            }
        };

        handler.post(serviceConnector);
    }

    private final Runnable serviceConnector = new Runnable()
    {
        @Override
        public void run()
        {
            if (gorillaRemote == null)
            {
                Log.d(LOGTAG, "serviceConnector: ...");

                Intent intent = new Intent();
                intent.setPackage("com.aura.aosp.gorilla.sysapp");
                intent.setAction("com.aura.android.gorillaservice.REMOTE_CONNECT");

                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

                handler.postDelayed(serviceConnector, 1000);
            }
        }
    };

    private void sendClientSecret()
    {
        IGorillaRemote gr = gorillaRemote;
        if (gr == null) return;

        try
        {
            clientSecret = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(clientSecret);

            String secret = Base64.encodeToString(clientSecret, Base64.NO_WRAP);
            gr.sendClientSecret(apkname, secret);

            Log.d(LOGTAG, "sendClientSecret: clientSecret=" + secret);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveServerSecret(Context context, Intent intent)
    {
        IGorillaRemote gr = gorillaRemote;
        if (gr == null) return;

        try
        {
            String secret = intent.getStringExtra("serverSecret");
            String challenge = intent.getStringExtra("challenge");
            String solution = createSHASignatureBase64(clientSecret);

            if ((challenge == null) || (solution == null) || !challenge.equals(solution))
            {
                Log.e(LOGTAG, "receiveServerSecret: failed!");
                return;
            }

            serverSecret = Base64.decode(secret, Base64.DEFAULT);
            Log.d(LOGTAG, "receiveServerSecret: serverSecret=" + secret);

            challenge = createSHASignatureBase64(serverSecret);

            validated = gr.validateConnect(apkname, challenge);

            if (! validated)
            {
                Log.e(LOGTAG, "receiveServerSecret: validate failed!");
                return;
            }

            Log.d(LOGTAG, "receiveServerSecret: validated.");

            String checksum = createSHASignatureBase64(serverSecret, apkname.getBytes());

            receiveOwner(gr.getOwnerUUID(apkname, checksum));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveOwner(String ownerUUID)
    {
        this.ownerUUID = ownerUUID;

        Log.d(LOGTAG, "receiveOwner: ownerUUID=" + ownerUUID);

        final JSONObject owner = new JSONObject();

        putJSON(owner, "ownerUUID", ownerUUID);

        final OnOwnerReceivedListener listener = onOwnerReceivedListener;

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
    }

    private static String createSHASignatureBase64(byte[] secret, byte[]... buffers)
    {
        return Base64.encodeToString(createSHASignature(secret, buffers), Base64.NO_WRAP);
    }

    @Nullable
    private static byte[] createSHASignature(byte[] secret, byte[]... buffers)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            md.update(secret);

            for (byte[] buffer : buffers)
            {
                md.update(buffer);
            }

            return md.digest();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    void onReceive(Context context, Intent intent)
    {
        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_SECRET"))
        {
            receiveServerSecret(context, intent);
            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_OWNER"))
        {
            receiveOwner(intent.getStringExtra("ownerUUID"));
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
        requestIntent.putExtra("apkname", apkname);
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
