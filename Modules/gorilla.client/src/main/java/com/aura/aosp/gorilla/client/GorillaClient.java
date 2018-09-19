package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

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

        if (this.context != null)
        {
            //
            // Service already bound.
            //

            return;
        }

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

                boolean c1 = GorillaIntercon.setServiceStatus(sysApkName, false);
                boolean c2 = GorillaIntercon.setUplinkStatus(sysApkName, false);

                if (c1 || c2) receiveStatus();

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

            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName,  apkname, clientSecret);

            boolean svlink = gr.initClientSecret(apkname, clientSecret, checksum);

            if (GorillaIntercon.setServiceStatus(sysApkName, svlink))
            {
                receiveStatus();
            }

            Log.d(LOGTAG, "initClientSecret: call apkname=" + apkname + " clientSecret=" + clientSecret + " svlink=" + svlink);

            if (! svlink) return;

            checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);

            if (GorillaIntercon.setUplinkStatus(sysApkName, uplink))
            {
                receiveStatus();
            }

            checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveOwnerUUID(ownerUUID);

            checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            boolean valid = gr.requestPersisted(apkname, checksum);
            Log.d(LOGTAG, "requestPersisted valid=" + valid);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    void getUplinkStatus()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);

            if (GorillaIntercon.setUplinkStatus(sysApkName, uplink))
            {
                GorillaClient.getInstance().receiveStatus();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    void getOwnerUUID()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveOwnerUUID(ownerUUID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    void requestPersisted()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname);

            boolean valid = gr.requestPersisted(apkname, checksum);
            Log.d(LOGTAG, "requestPersisted valid=" + valid);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    void receiveStatus()
    {
        JSONObject status = new JSONObject();

        putJSON(status, "svlink", GorillaIntercon.getServiceStatus(sysApkName));
        putJSON(status, "uplink", GorillaIntercon.getUplinkStatus(sysApkName));

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

    void receiveOwnerUUID(String ownerUUID)
    {
        this.ownerUUID = ownerUUID;

        Log.d(LOGTAG, "receiveOwner: ownerUUID=" + ownerUUID);

        final JSONObject owner = new JSONObject();
        putJSON(owner, "ownerUUID", ownerUUID);

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

    void receivePayload(long time, String uuid, String senderUUID, String deviceUUID, String payload)
    {
        final JSONObject message = new JSONObject();

        putJSON(message, "uuid", uuid);
        putJSON(message, "time", time);
        putJSON(message, "sender", senderUUID);
        putJSON(message, "device", deviceUUID);
        putJSON(message, "payload", payload);

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

    void receivePayloadResult(String resultStr)
    {
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

    @Nullable
    public JSONObject sendPayload(String userUUID, String deviceUUID, String payload)
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return null;

        try
        {
            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname, userUUID, deviceUUID, payload);

            String resultStr = gr.sendPayload(apkname, userUUID, deviceUUID, payload, checksum);

            Log.d(LOGTAG, "sendPayload: resultStr=" + resultStr);

            final JSONObject result = fromStringJSONOBject(resultStr);

            if (result == null)
            {
                Log.e(LOGTAG, "sendPayload: result failed!");
                return null;
            }

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean sendPayloadRead(String userUUID, String deviceUUID, String messageUUID)
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return false;

        try
        {
            String checksum = GorillaIntercon.createSHASignatureBase64(sysApkName, apkname, userUUID, deviceUUID, messageUUID);

            boolean result = gr.sendPayloadRead(apkname, userUUID, deviceUUID, messageUUID, checksum);

            Log.d(LOGTAG, "sendPayloadRead: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
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

    //endregion Private helpers.

    private static void putJSON(JSONObject json, String key, Object val)
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
    private static JSONObject fromStringJSONOBject(String jsonstr)
    {
        if (jsonstr == null) return null;

        try
        {
            return new JSONObject(jsonstr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    //endregion Private helpers.
}
