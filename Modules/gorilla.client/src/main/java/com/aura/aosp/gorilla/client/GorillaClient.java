package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

@SuppressWarnings("unused")
@SuppressLint("StaticFieldLeak")
public class GorillaClient
{
    private static final String LOGTAG = GorillaClient.class.getSimpleName();

    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

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
    private String ownerUUID;

    private String apkname;

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

                IGorillaSystemService gorillaRemote = IGorillaSystemService.Stub.asInterface(service);
                GorillaIntercon.setSystemService(sysApkName, gorillaRemote);

                initClientSecret();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());

                GorillaIntercon.setSystemService(sysApkName, null);
                GorillaIntercon.setServiceStatus(sysApkName, false);
                GorillaIntercon.setUplinkStatus(sysApkName, false);

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
            if (GorillaIntercon.getSystemService(sysApkName) == null)
            {
                Log.d(LOGTAG, "serviceConnector: ...");

                ComponentName componentName = new ComponentName(sysApkName, "com.aura.aosp.gorilla.service.GorillaService");

                Intent serviceIntent = new Intent();
                serviceIntent.setComponent(componentName);

                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                handler.postDelayed(serviceConnector, 1000);
            }
        }
    };

    private void initClientSecret()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaHelpers.createSHASignatureBase64(
                    GorillaIntercon.getServerSecret(sysApkName),
                    apkname.getBytes(), clientSecret.getBytes());

            boolean valid = gr.initClientSecret(apkname, clientSecret, checksum);

            Log.d(LOGTAG, "initClientSecret: call apkname=" + apkname + " clientSecret=" + clientSecret + " valid=" + valid);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveServerSecret(Context context, Intent intent)
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            byte[] clientSecret = GorillaIntercon.getClientSecret(sysApkName);
            String secret = intent.getStringExtra("serverSecret");
            String challenge = intent.getStringExtra("challenge");
            String solution = GorillaHelpers.createSHASignatureBase64(clientSecret);

            if ((challenge == null) || (solution == null) || !challenge.equals(solution))
            {
                Log.e(LOGTAG, "receiveServerSecret: failed!");
                return;
            }

            GorillaIntercon.setServerSecret(sysApkName, secret);
            Log.d(LOGTAG, "receiveServerSecret: serverSecret=" + secret);

            challenge = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getServerSecret(sysApkName));

            boolean svlink = gr.validateConnect(apkname, challenge);
            GorillaIntercon.setServiceStatus(sysApkName, svlink);

            if (!svlink)
            {
                Log.e(LOGTAG, "receiveServerSecret: validate failed!");
                return;
            }

            Log.d(LOGTAG, "receiveServerSecret: validated.");

            String checksum = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getServerSecret(sysApkName), apkname.getBytes());

            boolean uplink = gr.getOnlineStatus(apkname, checksum);
            GorillaIntercon.setUplinkStatus(sysApkName, uplink);

            receiveStatus();

            checksum = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getServerSecret(sysApkName), apkname.getBytes());

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveOwnerUUID(ownerUUID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void receiveStatus(Context context, Intent intent)
    {
        boolean uplink = intent.getBooleanExtra("uplink", false);

        Log.d(LOGTAG, "receiveStatus: uplink=" + uplink);

        byte[] bytes = new byte[ 1 ];
        bytes[ 0 ] = (byte) (uplink ? 1 : 0);

        String checksum = intent.getStringExtra("checksum");

        String solution;

        solution = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getClientSecret(sysApkName), apkname.getBytes(), bytes);

        if ((checksum == null) || (solution == null) || !checksum.equals(solution))
        {
            Log.e(LOGTAG, "receiveStatus: failed!");
            return;
        }

        GorillaIntercon.setUplinkStatus(sysApkName, uplink);

        receiveStatus();
    }

    private void receiveStatus()
    {
        JSONObject status = new JSONObject();

        GorillaHelpers.putJSON(status, "svlink", GorillaIntercon.getServiceStatus(sysApkName));
        GorillaHelpers.putJSON(status, "uplink", GorillaIntercon.getUplinkStatus(sysApkName));

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
            solution = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getClientSecret(sysApkName), apkname.getBytes());
        }
        else
        {
            solution = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getClientSecret(sysApkName), apkname.getBytes(), ownerUUID.getBytes());
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

        solution = GorillaHelpers.createSHASignatureBase64(GorillaIntercon.getClientSecret(sysApkName), apkname.getBytes(), resultStr.getBytes());

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

        if ((intent.getAction() != null) && intent.getAction().equals("com.aura.aosp.gorilla.service.RECV_STATUS"))
        {
            receiveStatus(context, intent);
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
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String checksum = GorillaHelpers.createSHASignatureBase64(
                    GorillaIntercon.getServerSecret(sysApkName),
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
