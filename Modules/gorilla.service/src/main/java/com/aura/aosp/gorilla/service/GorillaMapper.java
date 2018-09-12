package com.aura.aosp.gorilla.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.client.IGorillaClientService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class GorillaMapper
{
    private static class AppData
    {
        private IGorillaClientService clientService;

        private byte[] serverSecret;
        private byte[] clientSecret;
    }

    private final static Map<String, AppData> apkDatas = new HashMap<>();

    public static String mapAPK2UUID(String apkname)
    {
        if (apkname.equals("com.aura.aosp.gorilla.sysapp"))
        {
            return "A0Et8SbAQ4e7O3tVhYdJOQ==";
        }

        if (apkname.equals("com.aura.aosp.gorilla.messenger"))
        {
            return "B0Et8SbAQ4e7O3tVhYdJOQ==";
        }

        Err.err("unknown apk=%s", apkname);

        return null;
    }

    public static String mapUUID2APK(String apkuuid)
    {
        if (apkuuid.equals("A0Et8SbAQ4e7O3tVhYdJOQ=="))
        {
            return "com.aura.aosp.gorilla.sysapp";
        }

        if (apkuuid.equals("B0Et8SbAQ4e7O3tVhYdJOQ=="))
        {
            return "com.aura.aosp.gorilla.messenger";
        }

        Err.err("unknown apkuuid=%s", apkuuid);

        return null;
    }

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

    public static List<String> getAllAttachedAPKNames()
    {
        synchronized (apkDatas)
        {
            return new ArrayList<>(apkDatas.keySet());
        }
    }

    public static void setServerSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).serverSecret = secret;
    }

    @Nullable
    public static byte[] getServerSecret(String apkname)
    {
        return getAppData(apkname).serverSecret;
    }

    public static void setClientSecret(String apkname, byte[] secret)
    {
        getAppData(apkname).clientSecret = secret;
    }

    public static void setClientSecret(String apkname, String secretBase64)
    {
        getAppData(apkname).clientSecret = Base64.decode(secretBase64, Base64.DEFAULT);
    }

    @Nullable
    public static byte[] getClientSecret(String apkname)
    {
        return getAppData(apkname).clientSecret;
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

                GorillaMapper.setClientService(apkname, gorillaRemote);
            }

            public void onServiceDisconnected(ComponentName className)
            {
                Log.d("Client apkname=%s className=%s", apkname, className.toString());

                GorillaMapper.delAppData(apkname);

                GorillaBase.getAppContext().unbindService(this);
            }
        };

        Log.d("apkname=%s", apkname);

        ComponentName componentName = new ComponentName(apkname, "com.aura.aosp.gorilla.client.GorillaService");

        Intent serviceIntent = new Intent();
        serviceIntent.setComponent(componentName);

        GorillaBase.getAppContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
