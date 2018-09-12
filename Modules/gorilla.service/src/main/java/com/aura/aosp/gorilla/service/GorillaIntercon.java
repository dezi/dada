package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

public class GorillaIntercon
{
    private static class AppData
    {
        private IGorillaClientService clientService;
        private IGorillaSystemService systemService;

        private byte[] serverSecret;
        private byte[] clientSecret;

        private boolean svlink;
    }

    private final static Map<String, AppData> apkDatas = new HashMap<>();

    @NonNull
    private static AppData getAppData(String apkname)
    {
        synchronized (apkDatas)
        {
            AppData appData = apkDatas.get(apkname);

            if (appData == null)
            {
                appData = new AppData();
                apkDatas.put(apkname, appData);
            }

            return appData;
        }
    }

    public static void delAppData(String apkname)
    {
        synchronized (apkDatas)
        {
            apkDatas.remove(apkname);
        }
    }

    public static void setServerSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSecret = secret;
    }

    public static void setServerSecret(String apkname, String secretBase64)
    {
        getAppData(apkname).serverSecret = Base64.decode(secretBase64, Base64.DEFAULT);;
    }

    @Nullable
    public static byte[] getServerSecret(String apkname)
    {
        return getAppData(apkname).serverSecret;
    }

    @Nullable
    public static String getServerSecretBase64(String apkname)
    {
        byte[] serverSecret = getServerSecret(apkname);
        if (serverSecret == null) return null;

        return Base64.encodeToString(serverSecret, Base64.NO_WRAP);
    }

    public static void setClientSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).clientSecret = secret;
    }

    public static void setClientSecret(String apkname, String secretBase64)
    {
        setClientSecret(apkname, Base64.decode(secretBase64, Base64.DEFAULT));
    }

    @Nullable
    public static byte[] getClientSecret(String apkname)
    {
        return getAppData(apkname).clientSecret;
    }

    @Nullable
    public static String getClientSecretBase64(String apkname)
    {
        byte[] clientSecret = getClientSecret(apkname);
        if (clientSecret == null) return null;

        return Base64.encodeToString(clientSecret, Base64.NO_WRAP);
    }

    public static void setClientService(String apkname, IGorillaClientService service)
    {
        getAppData(apkname).clientService = service;
    }

    @Nullable
    public static IGorillaClientService getClientService(String apkname)
    {
        return getAppData(apkname).clientService;
    }

    public static void setSystemService(String apkname, IGorillaSystemService service)
    {
        getAppData(apkname).systemService = service;
    }

    @Nullable
    public static IGorillaSystemService getSystemService(String apkname)
    {
        return getAppData(apkname).systemService;
    }

    public static void startClientService(final String apkname)
    {
        IGorillaClientService service = getAppData(apkname).clientService;
        if (service != null) return;

        ServiceConnection serviceConnection = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                Log.d("Client apkname=%s className=%s", apkname, className.toString());

                IGorillaClientService gorillaRemote = IGorillaClientService.Stub.asInterface(service);

                GorillaIntercon.setClientService(apkname, gorillaRemote);

                initServerSecret(apkname);
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d("Client apkname=%s className=%s", apkname, className.toString());

                GorillaIntercon.delAppData(apkname);

                GorillaBase.getAppContext().unbindService(this);
            }
        };

        Log.d("apkname=%s", apkname);

        ComponentName componentName = new ComponentName(apkname, "com.aura.aosp.gorilla.client.GorillaService");

        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(componentName);

        GorillaBase.getAppContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private static void initServerSecret(String apkname)
    {
        IGorillaClientService gr = GorillaIntercon.getClientService(apkname);
        if (gr == null) return;

        try
        {
            //gr.initServerSecret(apkname, secret);

            //android.util.Log.d("initClientSecret: serverSecret=" + secret);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
