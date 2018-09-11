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

    //endregion Static implemention.

    //region Listener templates.

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

    //endregion Listener templates.

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

    private OnOwnerReceivedListener onOwnerReceivedListener;
    private OnResultReceivedListener onResultReceivedListener;
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

                validated = false;
                gorillaRemote = null;
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

                Intent serviceIntent = new Intent();
                serviceIntent.setPackage("com.aura.aosp.gorilla.sysapp");
                serviceIntent.setAction("com.aura.android.gorillaservice.REMOTE_CONNECT");

                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

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

            receiveOwnerUUID(gr.getOwnerUUID(apkname, checksum));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveOwnerUUID(Context context, Intent intent)
    {
        String ownerUUID = intent.getStringExtra("ownerUUID");
        String checksum = intent.getStringExtra("checksum");

        String solution;

        if (ownerUUID == null)
        {
            solution = createSHASignatureBase64(clientSecret, apkname.getBytes());
        }
        else
        {
            solution = createSHASignatureBase64(clientSecret, apkname.getBytes(), ownerUUID.getBytes());
        }

        if ((checksum == null) || (solution == null) || !checksum.equals(solution))
        {
            Log.e(LOGTAG, "receiveOwnerUUID: failed!");
            return;
        }

        receiveOwnerUUID(ownerUUID);
    }

    private void receiveOwnerUUID(String ownerUUID)
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

    private void receivePayload(Context context, Intent intent)
    {
        long time = intent.getLongExtra("time", -1);

        String uuid = intent.getStringExtra("uuid");
        String sender = intent.getStringExtra("sender");
        String device = intent.getStringExtra("device");
        String payload = intent.getStringExtra("payload");

        Log.d(LOGTAG, "onReceive: RECV_PAYLOAD uuid=" + uuid + " time=" + time + " sender=" + sender + " device=" + device + " payload=" + payload);

        final JSONObject message = new JSONObject();

        putJSON(message, "uuid", uuid);
        putJSON(message, "time", time);
        putJSON(message, "sender", sender);
        putJSON(message, "device", device);
        putJSON(message, "payload", payload);

        receivePayload(message);
    }

    private void receivePayload(final JSONObject message)
    {
        Log.d(LOGTAG, "receivePayload: message=" + message.toString());

        final OnMessageReceivedListener listener = onMessageReceivedListener;

        if (listener != null)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    listener.onMessageReceived(message);
                }
            });
        }
    }

    private void receivePayloadResult(Context context, Intent intent)
    {
        String resultStr = intent.getStringExtra("result");
        String checksum = intent.getStringExtra("checksum");

        String solution;

        solution = createSHASignatureBase64(clientSecret, apkname.getBytes(), resultStr.getBytes());

        if ((checksum == null) || (solution == null) || !checksum.equals(solution))
        {
            Log.e(LOGTAG, "receivePayloadResult: failed!");
            return;
        }

        JSONObject result = fromStringJSONOBject(resultStr);

        if (result == null)
        {
            Log.e(LOGTAG, "receivePayloadResult: result failed!");
            return;
        }

        receivePayloadResult(result);
    }

    private void receivePayloadResult(final JSONObject result)
    {
        Log.d(LOGTAG, "receivePayloadResult: result=" + result.toString());

        final OnResultReceivedListener listener = onResultReceivedListener;

        if (listener != null)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    listener.onResultReceived(result);
                }
            });
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
            receiveOwnerUUID(context, intent);
            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.SEND_PAYLOAD_RESULT"))
        {
            receivePayloadResult(context, intent);
            return;
        }

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_PAYLOAD"))
        {
            receivePayload(context, intent);
            return;
        }

        //
        // Silently ignore.
        //

        Log.d(LOGTAG, "onReceive: wrong action.");
    }

    public void sendPayload(Context context, String userUUID, String deviceUUID, String payload)
    {
        IGorillaRemote gr = gorillaRemote;
        if (gr == null) return;

        try
        {
            String checksum = createSHASignatureBase64(serverSecret,
                    apkname.getBytes(),
                    userUUID.getBytes(),
                    deviceUUID.getBytes(),
                    payload.getBytes()
            );

            String resultStr = gr.sendPayload(apkname, userUUID, deviceUUID, payload, checksum);

            Log.d(LOGTAG, "sendPayload: resultStr=" + resultStr);

            JSONObject result = fromStringJSONOBject(resultStr);

            if (result == null)
            {
                Log.e(LOGTAG, "sendPayload: result failed!");
                return;
            }

            receivePayloadResult(result);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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
                if (buffer != null) md.update(buffer);
            }

            return md.digest();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    //endregion Instance implemention.
}
