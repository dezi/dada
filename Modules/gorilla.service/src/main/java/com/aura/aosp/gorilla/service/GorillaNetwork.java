package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

public class GorillaNetwork extends BroadcastReceiver
{
    private static String wifiName;

    private static boolean isWifiAvailable;
    private static boolean isWifiConnected;

    private static String mobileName;

    private static boolean isMobileAvailable;
    private static boolean isMobileConnected;

    static
    {
        getNetworkState();
    }

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if ((intent == null) || (intent.getAction() == null)  ||
                ! (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED"))
               || intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
            return;
        }

        getNetworkState();
        logNetworkState();
    }

    private static void getNetworkState()
    {
        Application context = GorillaBase.getAppContext();

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) return;

        android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isMobileAvailable = mobile.isAvailable();
        isMobileConnected = mobile.isConnected();

        if (isMobileAvailable)
        {
            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (telMgr != null)
            {
                mobileName = telMgr.getNetworkOperatorName();
            }
            else
            {
                mobileName = null;
            }
        }
        else
        {
            mobileName = null;
        }

        android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        isWifiAvailable = wifi.isAvailable();
        isWifiConnected = wifi.isConnected();

        if (isWifiConnected)
        {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr != null)
            {
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                wifiName = wifiInfo.getSSID();

                if ((wifiName != null)
                        && wifiName.startsWith("\"")
                        && wifiName.endsWith("\""))
                {
                    wifiName = wifiName.substring(1, wifiName.length() - 1);
                }
            }
            else
            {
                wifiName = null;
            }
        }
        else
        {
            wifiName = null;
        }
    }

    public static void logNetworkState()
    {
        Log.d("network state mobile=%b/%b prov=<%s> wifi=%b/%b ssid=<%s>",
                isMobileAvailable, isMobileConnected, mobileName,
                isWifiAvailable, isWifiConnected, wifiName
        );
    }

    @Nullable
    public static String getMobileName()
    {
        if (mobileName == null)
        {
            Err.err("mobile not available.");
        }

        return mobileName;
    }

    public static boolean isMobileAvailable()
    {
        return isMobileAvailable;
    }

    public static boolean isMobileConnected()
    {
        return isMobileConnected;
    }

    @Nullable
    public static String getWifiName()
    {
        if (wifiName == null)
        {
            Err.err("wifi not connected.");
        }

        return wifiName;
    }

    public static boolean isWifiAvailable()
    {
        return isWifiAvailable;
    }

    public static boolean isWifiConnected()
    {
        return isWifiConnected;
    }
}
