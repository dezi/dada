package com.aura.aosp.gorilla.client;

import android.annotation.SuppressLint;
import android.app.Service;
import android.support.annotation.Nullable;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    //region Instance implemention.

    private Context context;
    private String ownerUUID;
    private String apkname;

    private final Handler handler = new Handler();

    private final List<GorillaListener> gorillaListeners = new ArrayList<>();

    private final ComponentName componentName = new ComponentName(
            "com.aura.aosp.gorilla.sysapp",
            "com.aura.aosp.gorilla.service.GorillaSystem");

    private final ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());

            IGorillaSystemService gorillaRemote = IGorillaSystemService.Stub.asInterface(service);
            GorillaConnect.setSystemService(gorillaRemote);

            validateConnect();
        }

        public void onServiceDisconnected(ComponentName className)
        {
            Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());

            GorillaConnect.setSystemService(null);

            boolean c1 = GorillaConnect.setServiceStatus(false);
            boolean c2 = GorillaConnect.setUplinkStatus(false);

            if (c1 || c2) receiveStatus();

            handler.post(serviceConnector);
        }
    };

    private final Runnable serviceConnector = new Runnable()
    {
        @Override
        public void run()
        {
            if (GorillaConnect.getSystemService() == null)
            {
                Log.d(LOGTAG, "serviceConnector: ...");

                Intent serviceIntent = new Intent();
                serviceIntent.setComponent(componentName);

                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                handler.postDelayed(serviceConnector, 1000);
            }
        }
    };

    public void connectService(Context context)
    {
        Log.d(LOGTAG, "connectService: ...");

        if (this.context != null)
        {
            //
            // Service already bound.
            //

            return;
        }

        this.context = context;
        this.apkname = context.getPackageName();

        handler.post(serviceConnector);
    }

    public void disconnectService()
    {
        Log.d(LOGTAG, "disconnectService: ...");

        if (context != null)
        {
            context.unbindService(serviceConnection);

            apkname = null;
            context = null;
        }
    }

    private void validateConnect()
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String serverSecret = gr.returnYourSecret(apkname);
            GorillaConnect.setServerSecretBase64(serverSecret);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaConnect.getServerSecretBase64());

            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

            boolean svlink = gr.validateConnect(apkname, checksum);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaConnect.getServerSecretBase64()
                    + " clientSecret=" + GorillaConnect.getClientSecretBase64()
                    + " svlink=" + svlink);

            if (!svlink) return;

            GorillaConnect.setServiceStatus(true);

            checksum = GorillaConnect.createSHASignatureBase64(apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);
            GorillaConnect.setUplinkStatus(uplink);

            checksum = GorillaConnect.createSHASignatureBase64(apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            receiveStatus();
            receiveOwnerUUID(ownerUUID);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    void getUplinkStatus()
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);

            if (GorillaConnect.setUplinkStatus(uplink))
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
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

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
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

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

        putJSON(status, "svlink", GorillaConnect.getServiceStatus());
        putJSON(status, "uplink", GorillaConnect.getUplinkStatus());

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
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, deviceUUID, payload);

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
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, deviceUUID, messageUUID);

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

    public boolean putAtom(JSONObject atom)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaConnect.createSHASignatureBase64(apkname, atomStr);

            boolean result = gr.putAtom(apkname, atomStr, checksum);

            Log.d(LOGTAG, "putAtom: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean putAtomSharedBy(String userUUID, JSONObject atom)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomStr);

            boolean result = gr.putAtomSharedBy(apkname, userUUID, atomStr, checksum);

            Log.d(LOGTAG, "putAtomSharedBy: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean putAtomSharedWith(String userUUID, JSONObject atom)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomStr);

            boolean result = gr.putAtomSharedWith(apkname, userUUID, atomStr, checksum);

            Log.d(LOGTAG, "putAtomSharedWith: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Nullable
    public JSONArray queryAtomsSharedBy(String userUUID, String atomType, long timeFrom, long timeTo)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeTo);

            String resultsStr = gr.queryAtomsSharedBy(apkname, userUUID, atomType, timeFrom, timeTo, checksum);

            Log.d(LOGTAG, "queryAtomsSharedBy: resultsStr=" + resultsStr);

            return (resultsStr == null) ? null : new JSONArray(resultsStr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    @Nullable
    public JSONArray queryAtomsSharedWith(String userUUID, String atomType, long timeFrom, long timeTo)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeTo);

            String resultsStr = gr.queryAtomsSharedWith(apkname, userUUID, atomType, timeFrom, timeTo, checksum);
            Log.d(LOGTAG, "queryAtomsSharedWith: resultsStr=" + resultsStr);

            return (resultsStr == null) ? null : new JSONArray(resultsStr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean registerActionEvent(String actionDomain, String subAction)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, actionDomain, subAction);

            boolean result = gr.registerActionEvent(apkname, actionDomain, subAction, checksum);

            Log.d(LOGTAG, "registerActionEvent: result=" + result);

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
            if (!gorillaListeners.contains(gorillaListener))
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
