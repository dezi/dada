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
public class GorillaClient extends Service
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

    private final List<GorillaListener> gorillaListeners = new ArrayList<>();
    private final Handler handler = new Handler();

    private Context context;

    private ServiceConnection serviceConnection;
    private ComponentName componentName;
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

        componentName = new ComponentName(
                "com.aura.aosp.gorilla.sysapp",
                "com.aura.aosp.gorilla.service.GorillaSystem");

        serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());

                IGorillaSystemService gorillaRemote = IGorillaSystemService.Stub.asInterface(service);
                GorillaCredentials.setSystemService(gorillaRemote);

                validateConnect();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());

                GorillaCredentials.setSystemService(null);

                boolean c1 = GorillaCredentials.setServiceStatus(false);
                boolean c2 = GorillaCredentials.setUplinkStatus(false);

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

    private final Runnable serviceConnector = new Runnable()
    {
        @Override
        public void run()
        {
            if (GorillaCredentials.getSystemService() == null)
            {
                Log.d(LOGTAG, "serviceConnector: ...");

                Intent serviceIntent = new Intent();
                serviceIntent.setComponent(componentName);

                context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                handler.postDelayed(serviceConnector, 1000);
            }
        }
    };

    private void validateConnect()
    {
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return;

        try
        {
            String serverSecret = gr.returnYourSecret(apkname);
            GorillaCredentials.setServerSecretBase64(serverSecret);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaCredentials.getServerSecretBase64());

            String checksum = GorillaCredentials.createSHASignatureBase64(apkname);

            boolean svlink = gr.validateConnect(apkname, checksum);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaCredentials.getServerSecretBase64()
                    + " clientSecret=" + GorillaCredentials.getClientSecretBase64()
                    + " svlink=" + svlink);

            if (!svlink) return;

            GorillaCredentials.setServiceStatus(true);

            checksum = GorillaCredentials.createSHASignatureBase64(apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);
            GorillaCredentials.setUplinkStatus(uplink);

            checksum = GorillaCredentials.createSHASignatureBase64(apkname);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname);

            boolean uplink = gr.getUplinkStatus(apkname, checksum);

            if (GorillaCredentials.setUplinkStatus(uplink))
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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname);

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

        putJSON(status, "svlink", GorillaCredentials.getServiceStatus());
        putJSON(status, "uplink", GorillaCredentials.getUplinkStatus());

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, deviceUUID, payload);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, deviceUUID, messageUUID);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, atomStr);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, atomStr);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return false;

        try
        {
            String atomStr = atom.toString(2);

            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, atomStr);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeTo);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeTo);

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
        IGorillaSystemService gr = GorillaCredentials.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaCredentials.createSHASignatureBase64(apkname, actionDomain, subAction);

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

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG,"onBind: intent=" + intent.toString());

        return new GorillaClientService();
    }
}
