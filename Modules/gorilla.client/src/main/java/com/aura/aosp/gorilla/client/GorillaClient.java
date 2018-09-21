package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    //region Instance implemention.

    private final List<GorillaListener> gorillaListeners = new ArrayList<>();
    private final Handler handler = new Handler();

    private Context context;

    private ServiceConnection serviceConnection;
    private String ownerUUID;
    private String apkname;

    public void bindService(Context context)
    {
        Log.d(LOGTAG, "bindService: ...");

        if (this.context != null)
        {
            //
            // Service already bound.
            //

            return;
        }

        this.context = context;
        this.apkname = context.getPackageName();

        serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());

                IGorillaSystemService gorillaRemote = IGorillaSystemService.Stub.asInterface(service);
                GorillaIntercon.setSystemService(sysApkName, gorillaRemote);

                validateConnect();
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

    public void unbindService()
    {
        Log.d(LOGTAG, "unbindService: ...");

        if (context != null)
        {
            context.unbindService(serviceConnection);

            context = null;
            apkname = null;
            serviceConnection = null;
        }
    }

    void startMainActivity()
    {
        Log.d(LOGTAG, "startMainActivity: ...");

        Intent startIntent = new Intent();

        startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startIntent.setAction(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setPackage(context.getPackageName());

        try
        {
            context.startActivity(startIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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

    private void validateConnect()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String serverSecret = gr.returnYourSecret(apkname);
            GorillaIntercon.setServerSecret(sysApkName, serverSecret);

            Log.d(LOGTAG, "validateConnect: call"
                            + " apkname=" + apkname
                            + " serverSecret=" + GorillaIntercon.getServerSecretBase64(sysApkName));

            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

            boolean svlink = gr.validateConnect(apkname, checksum);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaIntercon.getServerSecretBase64(sysApkName)
                    + " clientSecret=" + GorillaIntercon.getClientSecretBase64(sysApkName)
                    + " svlink=" + svlink);

            if (!svlink) return;

            GorillaIntercon.setServiceStatus(sysApkName, true);

            checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);
            GorillaIntercon.setUplinkStatus(sysApkName, uplink);

            checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveStatus();
            receiveOwnerUUID(ownerUUID);
            startMainActivity();
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
            String serverSecret = GorillaIntercon.getServerSecretBase64(sysApkName);
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

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
            String serverSecret = GorillaIntercon.getServerSecretBase64(sysApkName);
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveOwnerUUID(ownerUUID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void requestPersisted()
    {
        IGorillaSystemService gr = GorillaIntercon.getSystemService(sysApkName);
        if (gr == null) return;

        try
        {
            String serverSecret = GorillaIntercon.getServerSecretBase64(sysApkName);
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

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

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onStatusReceived(status);
                    }
                }
            }
        });
   }

    private void receiveOwnerUUID()
    {
        Log.d(LOGTAG, "receiveOwner: ownerUUID=" + ownerUUID);

        final JSONObject owner = new JSONObject();
        putJSON(owner, "ownerUUID", ownerUUID);

        receiveOwnerUUID(owner);
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

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onOwnerReceived(owner);
                    }
                }
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

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onMessageReceived(message);
                    }
                }
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

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onResultReceived(result);
                    }
                }
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
            String serverSecret = GorillaIntercon.getServerSecretBase64(sysApkName);
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, userUUID, deviceUUID, payload);

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
            String serverSecret = GorillaIntercon.getServerSecretBase64(sysApkName);
            String clientSecret = GorillaIntercon.getClientSecretBase64(sysApkName);

            String checksum = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, userUUID, deviceUUID, messageUUID);

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

    public void subscribeGorillaListener(GorillaListener gorillaListener)
    {
        synchronized (gorillaListeners)
        {
            if (! gorillaListeners.contains(gorillaListener))
            {
                gorillaListeners.add(gorillaListener);
            }
        }
    }

    public void unsubscribeGorillaListener(GorillaListener gorillaListener)
    {
        synchronized (gorillaListeners)
        {
            if (gorillaListeners.contains(gorillaListener))
            {
                gorillaListeners.remove(gorillaListener);
            }
        }
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
