package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.security.SecureRandom;

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

    public static class OnStatusReceivedListener
    {
        public void onStatusReceived(JSONObject result)
        {
            Log.d(LOGTAG, "onStatusReceived: STUB!");
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

    //endregion Listener templates.

    //region Instance implemention.

    private Context context;
    private Handler handler;

    private ServiceConnection serviceConnection;
    private IGorillaRemote gorillaRemote;
    private String ownerUUID;

    private String apkname;
    private boolean svlink;
    private boolean uplink;
    private byte[] clientSecret;
    private byte[] serverSecret;

    private OnStatusReceivedListener onStatusReceivedListener;
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

                uplink = false;
                svlink = false;
                gorillaRemote = null;
                clientSecret = null;
                serverSecret = null;

                handler.post(serviceConnector);

                receiveStatus();
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
            String solution = GorillaHelpers.createSHASignatureBase64(clientSecret);

            if ((challenge == null) || (solution == null) || !challenge.equals(solution))
            {
                Log.e(LOGTAG, "receiveServerSecret: failed!");
                return;
            }

            serverSecret = Base64.decode(secret, Base64.DEFAULT);
            Log.d(LOGTAG, "receiveServerSecret: serverSecret=" + secret);

            challenge = GorillaHelpers.createSHASignatureBase64(serverSecret);

            svlink = gr.validateConnect(apkname, challenge);

            if (!svlink)
            {
                Log.e(LOGTAG, "receiveServerSecret: validate failed!");
                return;
            }

            Log.d(LOGTAG, "receiveServerSecret: validated.");

            String checksum = GorillaHelpers.createSHASignatureBase64(serverSecret, apkname.getBytes());

            uplink = gr.getOnlineStatus(apkname, checksum);

            receiveStatus();

            checksum = GorillaHelpers.createSHASignatureBase64(serverSecret, apkname.getBytes());

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveOwnerUUID(ownerUUID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveStatus()
    {
        JSONObject status = new JSONObject();

        GorillaHelpers.putJSON(status, "svlink", svlink);
        GorillaHelpers.putJSON(status, "uplink", uplink);

        receiveStatus(status);
    }

    private void receiveStatus(final JSONObject status)
    {
        Log.d(LOGTAG, "receiveStatus: status=" + status.toString());

        final OnStatusReceivedListener listener = onStatusReceivedListener;
        if (listener == null) return;

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onStatusReceived(status);
            }
        });
    }

    private void receiveOwnerUUID(Context context, Intent intent)
    {
        String ownerUUID = intent.getStringExtra("ownerUUID");
        String checksum = intent.getStringExtra("checksum");

        String solution;

        if (ownerUUID == null)
        {
            solution = GorillaHelpers.createSHASignatureBase64(clientSecret, apkname.getBytes());
        }
        else
        {
            solution = GorillaHelpers.createSHASignatureBase64(clientSecret, apkname.getBytes(), ownerUUID.getBytes());
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
        GorillaHelpers.putJSON(owner, "ownerUUID", ownerUUID);

        receiveOwnerUUID(owner);
    }

    private void receiveOwnerUUID(final JSONObject owner)
    {
        Log.d(LOGTAG, "receiveOwnerUUID: owner=" + owner.toString());

        final OnOwnerReceivedListener listener = onOwnerReceivedListener;
        if (listener == null) return;

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onOwnerReceived(owner);
            }
        });
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

        GorillaHelpers.putJSON(message, "uuid", uuid);
        GorillaHelpers.putJSON(message, "time", time);
        GorillaHelpers.putJSON(message, "sender", sender);
        GorillaHelpers.putJSON(message, "device", device);
        GorillaHelpers.putJSON(message, "payload", payload);

        receivePayload(message);
    }

    private void receivePayload(final JSONObject message)
    {
        Log.d(LOGTAG, "receivePayload: message=" + message.toString());

        final OnMessageReceivedListener listener = onMessageReceivedListener;
        if (listener == null) return;

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onMessageReceived(message);
            }
        });
    }

    private void receivePayloadResult(Context context, Intent intent)
    {
        String resultStr = intent.getStringExtra("result");
        String checksum = intent.getStringExtra("checksum");

        String solution;

        solution = GorillaHelpers.createSHASignatureBase64(clientSecret, apkname.getBytes(), resultStr.getBytes());

        if ((checksum == null) || (solution == null) || !checksum.equals(solution))
        {
            Log.e(LOGTAG, "receivePayloadResult: failed!");
            return;
        }

        JSONObject result = GorillaHelpers.fromStringJSONOBject(resultStr);

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
        if (listener == null) return;

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                listener.onResultReceived(result);
            }
        });
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
            String checksum = GorillaHelpers.createSHASignatureBase64(serverSecret,
                    apkname.getBytes(),
                    userUUID.getBytes(),
                    deviceUUID.getBytes(),
                    payload.getBytes()
            );

            String resultStr = gr.sendPayload(apkname, userUUID, deviceUUID, payload, checksum);

            Log.d(LOGTAG, "sendPayload: resultStr=" + resultStr);

            JSONObject result = GorillaHelpers.fromStringJSONOBject(resultStr);

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

    public void setOnStatusReceivedListener(OnStatusReceivedListener onStatusReceivedListener)
    {
        this.onStatusReceivedListener = onStatusReceivedListener;
    }

    public void setOnOwnerReceivedListener(OnOwnerReceivedListener onOwnerReceivedListener)
    {
        this.onOwnerReceivedListener = onOwnerReceivedListener;
    }

    public void setOnResultReceivedListener(OnResultReceivedListener onResultReceivedListener)
    {
        this.onResultReceivedListener = onResultReceivedListener;
    }

    public void setOnMessageReceivedListener(OnMessageReceivedListener onMessageReceivedListener)
    {
        this.onMessageReceivedListener = onMessageReceivedListener;
    }

    //endregion Instance implemention.
}
