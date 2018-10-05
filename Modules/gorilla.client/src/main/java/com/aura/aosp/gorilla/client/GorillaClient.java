/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.support.annotation.Nullable;
import android.annotation.SuppressLint;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The class {@code GorillaClient} is the one and only interface
 * for third party apps into the Gorilla system space.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings("unused")
@SuppressLint("StaticFieldLeak")
public class GorillaClient
{
    /**
     * All purpose log tag.
     */
    private final static String LOGTAG = GorillaClient.class.getSimpleName();

    /**
     * Private singleton {@code GorillaClient} instance with application process lifetime.
     * <p>
     * Therefore context bla bla leaks are to be ignored.
     */
    private final static GorillaClient instance = new GorillaClient();

    /**
     * Deliver singleton instance to the public.
     *
     * @return sigleton instance of {@code GorillaClient}.
     */
    public static GorillaClient getInstance()
    {
        return instance;
    }

    /**
     * All purpose context.
     */
    private Context context;

    /**
     * Coutesy copy of APK package name.
     */
    private String apkname;

    /**
     * All purpose handler for postíng into UI thread.
     */
    private final Handler handler = new Handler();

    /**
     * Collection of {@code GorillaListener} instances which are
     * currently listening to Gorilla events.
     */
    private final List<GorillaListener> gorillaListeners = new ArrayList<>();

    /**
     * Coutesy copy of destination Gorilla system service.
     */
    private final ComponentName componentName = new ComponentName(
            "com.aura.aosp.gorilla.sysapp",
            "com.aura.aosp.gorilla.service.GorillaSystem");

    /**
     * Service connection instance which monitors service connects
     * and disconnects of the Gorilla system service app.
     */
    private final ServiceConnection serviceConnection = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            Log.d(LOGTAG, "onServiceConnected: className=" + className.toString());

            IGorillaSystemService gorillaSystemService = IGorillaSystemService.Stub.asInterface(service);
            GorillaConnect.setSystemService(gorillaSystemService);

            //
            // The Gorilla system service app has established a
            // connection to this third party app via a service
            // intent.
            //
            // Validate the connect now by checking server and client
            // signatures.
            //

            validateConnect();
        }

        public void onServiceDisconnected(ComponentName className)
        {
            Log.d(LOGTAG, "onServiceDisconnected: className=" + className.toString());

            //
            // The service connect to the Gorilla service app has been
            // disconnected by whatsoever reason.
            //
            // Register this and inform all listeners.
            //

            GorillaConnect.setSystemService(null);

            if (GorillaConnect.setServiceStatus(false))
            {
                dispatchServiceStatus();
            }

            if (GorillaConnect.setUplinkStatus(false))
            {
                dispatchUplinkStatus();
            }

            handler.post(serviceConnector);
        }
    };

    /**
     * Self calling runnable which tries to re-establish a valid
     * connection to Gorilla system services.
     */
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

    /**
     * Called by third party app to establish a connection between
     * the third party app and the Gorilla system service.
     *
     * @param context an abitrary context.
     */
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

    /**
     * Called by third party app to disconnect from the
     * Gorilla system service. Hardly ever used.
     */
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

    /**
     * Validate the bi-directional service connection between third party
     * app and Gorilla system service app.
     * <p>
     * This is done by checking the checksum over the current APK package name.
     * <p>
     * If the service can be validated, owner identity and online status is
     * requested and all listeners are informed.
     * <p>
     * If not, the action is silently ignored.
     */
    private void validateConnect()
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String serverSecret = gr.returnYourSignature(apkname);
            GorillaConnect.setServerSignatureBase64(serverSecret);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaConnect.getServerSignatureBase64());

            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

            boolean svlink = gr.validateConnect(apkname, checksum);

            Log.d(LOGTAG, "validateConnect: call"
                    + " apkname=" + apkname
                    + " serverSecret=" + GorillaConnect.getServerSignatureBase64()
                    + " clientSecret=" + GorillaConnect.getClientSignatureBase64()
                    + " svlink=" + svlink);

            if (!svlink) return;

            if (GorillaConnect.setServiceStatus(true))
            {
                dispatchServiceStatus();
            }

            getUplinkStatus();

            getOwnerUUID();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Package private request device owner identity and inform all listeners of this.
     * <p>
     * If the services are not validated, this action is silently ignored.
     */
    void getOwnerUUID()
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

            String ownerUUID = gr.getOwnerUUID(apkname, checksum);

            if (GorillaConnect.setOwnerUUID(ownerUUID))
            {
                dispatchOwnerUUID();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Package private request uplink status of Gorilla system
     * service app and inform all listeners of this.
     * <p>
     * If the services are not validated, this action is silently ignored.
     */
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
                GorillaClient.getInstance().dispatchUplinkStatus();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Package private dispatch current service status to all subscribed listeners.
     */
    void dispatchServiceStatus()
    {
        final boolean connected = GorillaConnect.getServiceStatus();

        Log.d(LOGTAG, "dispatchServiceStatus: connected=" + connected);

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onServiceChange(connected);
                    }
                }
            }
        });
    }

    /**
     * Package private dispatch current uplink status to all subscribed listeners.
     */
    void dispatchUplinkStatus()
    {
        final boolean connected = GorillaConnect.getUplinkStatus();

        Log.d(LOGTAG, "dispatchUplinkStatus: connected=" + connected);

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onUplinkChange(connected);
                    }
                }
            }
        });
    }

    /**
     * Package private dispatch current owner identity UUID to all subscribed listeners.
     */
    void dispatchOwnerUUID()
    {
        String ownerUUID = GorillaConnect.getownerUUIDBase64();
        if (ownerUUID == null) return;

        Log.d(LOGTAG, "dispatchOwnerUUID: ownerUUID=" + ownerUUID);

        final GorillaOwner owner = new GorillaOwner();
        owner.setOwnerUUID(ownerUUID);

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

    /**
     * Package private handle a received payload. Build a JSON message object and dispatch
     * to all subscribed listeners.
     *
     * @param time       origin time in milliseconds of payload.
     * @param uuid       UUID of this payload.
     * @param senderUUID sending user identity UUID.
     * @param deviceUUID sending device identity UUID.
     * @param payloadStr binary payload in base64 encoding.
     */
    void receivePayload(long time, String uuid, String senderUUID, String deviceUUID, String payloadStr)
    {
        final GorillaPayload payload = new GorillaPayload();

        payload.setUUID(uuid);
        payload.setTime(time);
        payload.setSenderUUID(senderUUID);
        payload.setDeviceUUID(deviceUUID);
        payload.setPayload(payloadStr);

        Log.d(LOGTAG, "receivePayload: payload=" + payload.toString());

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (gorillaListeners)
                {
                    for (GorillaListener gl : gorillaListeners)
                    {
                        gl.onPayloadReceived(payload);
                    }
                }
            }
        });
    }

    /**
     * Package private handle a received payload result. Contains message UUID,
     * timing and state information.
     *
     * @param resultJson result JSON object in string format.
     */
    void receivePayloadResult(String resultJson)
    {
        Log.d(LOGTAG, "receivePayloadResult: resultJson=" + resultJson);

        final GorillaPayloadResult result = new GorillaPayloadResult();

        if (result.set(resultJson))
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    synchronized (gorillaListeners)
                    {
                        for (GorillaListener gl : gorillaListeners)
                        {
                            gl.onPayloadResultReceived(result);
                        }
                    }
                }
            });
        }
        else
        {
            Log.e(LOGTAG, "receivePayloadResult: failed result=" + resultJson);
        }
    }

    /**
     * Send a payload to a target user and device.
     *
     * @param userUUID   target user identity UUID.
     * @param deviceUUID target device identity UUID.
     * @param payload    binary payload in base64 encoding.
     * @return GorillaPayloadResult or null if transfer to Gorilla system app failed.
     */
    @Nullable
    public GorillaPayloadResult sendPayload(String userUUID, String deviceUUID, String payload)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, deviceUUID, payload);

            String resultStr = gr.sendPayload(apkname, userUUID, deviceUUID, payload, checksum);

            Log.d(LOGTAG, "sendPayload: resultStr=" + resultStr);

            GorillaPayloadResult result = new GorillaPayloadResult();

            if (! result.set(resultStr))
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

    /**
     * Send a read state for a particula payload.
     *
     * @param userUUID    target user identity UUID.
     * @param deviceUUID  target device identity UUID.
     * @param messageUUID target message UUID.
     * @return true if transferred to Gorilla system app.
     */
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

    /**
     * Put an atom created by owner identity and shared with nobody into storage.
     *
     * @param atom JSON atom object.
     * @return true if the atom could be persisted.
     */
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

    /**
     * Put an atom created by user identity UUID and shared with owner identity into storage.
     *
     * @param userUUID target user identity UUID.
     * @param atom     JSON atom object.
     * @return true if the atom could be persisted.
     */
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

    /**
     * Put an atom created by owner identity and shared with user identity UUID into storage.
     *
     * @param userUUID target user identity UUID.
     * @param atom     JSON atom object.
     * @return true if the atom could be persisted.
     */
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

    /**
     * Query atoms create by owner identity shared with nobody.
     *
     * @param atomType atom type in reverse domain format.
     * @param timeFrom time frame from in milliseconds or zero for the epoch.
     * @param timeUpto time frame upto in milliseconds or zero for the epoch.
     * @return JSON array of result atoms or null on failure.
     */
    @Nullable
    public JSONArray queryAtoms(String atomType, long timeFrom, long timeUpto)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, atomType, timeFrom, timeUpto);

            String resultsStr = gr.queryAtoms(apkname, atomType, timeFrom, timeUpto, checksum);

            Log.d(LOGTAG, "queryAtoms: resultsStr=" + resultsStr);

            return (resultsStr == null) ? null : new JSONArray(resultsStr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Query atoms shared by user identity UUID with owner identy.
     *
     * @param userUUID target user identity UUID.
     * @param atomType atom type in reverse domain format.
     * @param timeFrom time frame from in milliseconds or zero for the epoch.
     * @param timeUpto time frame upto in milliseconds or zero for the epoch.
     * @return JSON array of result atoms or null on failure.
     */
    @Nullable
    public JSONArray queryAtomsSharedBy(String userUUID, String atomType, long timeFrom, long timeUpto)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeUpto);

            String resultsStr = gr.queryAtomsSharedBy(apkname, userUUID, atomType, timeFrom, timeUpto, checksum);

            Log.d(LOGTAG, "queryAtomsSharedBy: resultsStr=" + resultsStr);

            return (resultsStr == null) ? null : new JSONArray(resultsStr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Query atoms shared with user identity UUID by owner identy.
     *
     * @param userUUID target user identity UUID.
     * @param atomType atom type in reverse domain format.
     * @param timeFrom time frame from in milliseconds or zero for the epoch.
     * @param timeUpto time frame upto in milliseconds or zero for the epoch.
     * @return JSON array of result atoms or null on failure.
     */
    @Nullable
    public JSONArray queryAtomsSharedWith(String userUUID, String atomType, long timeFrom, long timeUpto)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return null;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, userUUID, atomType, timeFrom, timeUpto);

            String resultsStr = gr.queryAtomsSharedWith(apkname, userUUID, atomType, timeFrom, timeUpto, checksum);
            Log.d(LOGTAG, "queryAtomsSharedWith: resultsStr=" + resultsStr);

            return (resultsStr == null) ? null : new JSONArray(resultsStr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Register event on domain.
     *
     * @param actionDomain action domain reverse domain order.
     * @return true if event was registered.
     */
    public boolean registerActionEvent(String actionDomain)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, actionDomain);

            boolean result = gr.registerActionEvent(apkname, actionDomain, checksum);

            Log.d(LOGTAG, "registerActionEvent: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Register event on domain and subaction.
     *
     * @param actionDomain action domain reverse domain order.
     * @param subAction sub action to be recorded.
     * @return true if event was registered.
     */
    public boolean registerActionEventDomain(String actionDomain, String subAction)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, actionDomain, subAction);

            boolean result = gr.registerActionEventDomain(apkname, actionDomain, subAction, checksum);

            Log.d(LOGTAG, "registerActionEventDomain: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Register event on domain, context and subaction.
     *
     * @param actionDomain action domain reverse domain order.
     * @param subContext sub context of action domain.
     * @param subAction sub action to be recorded.
     * @return true if event was registered.
     */
    public boolean registerActionEventDomainContext(String actionDomain, String subContext, String subAction)
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname, actionDomain, subAction);

            boolean result = gr.registerActionEventDomain(apkname, actionDomain, subAction, checksum);

            Log.d(LOGTAG, "registerActionEventDomain: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Subscribe a {@code GorillaListener} for service connection.
     *
     * @param gorillaListener Third party app listener.
     */
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

    /**
     * Unsubscribe a {@code GorillaListener} from service connection.
     *
     * @param gorillaListener Third party app listener.
     */
    public void unsubscribeGorillaListener(GorillaListener gorillaListener)
    {
        synchronized (gorillaListeners)
        {
            gorillaListeners.remove(gorillaListener);
        }
    }

    /**
     * Request all persisted messages after validated
     * connection to Gorilla system service.
     *
     * @return true if call accepted by Gorilla service.
     */
    private boolean requestPersisted()
    {
        IGorillaSystemService gr = GorillaConnect.getSystemService();
        if (gr == null) return false;

        try
        {
            String checksum = GorillaConnect.createSHASignatureBase64(apkname);

            boolean valid = gr.requestPersisted(apkname, checksum);

            Log.d(LOGTAG, "requestPersisted valid=" + valid);

            return valid;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }
    }
}
