package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import android.telephony.TelephonyManager;
import android.content.BroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;

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
        if ((intent == null) || (intent.getAction() == null) ||
                ! (intent.getAction().equals("android.net.wifi.STATE_CHANGE"))
               || intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")
               || intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE"))
        {
            return;
        }

        getNetworkState();
        logNetworkState();
    }

    private static void getNetworkState()
    {
        String lastMobileName = mobileName;
        boolean lastIsMobileAvailable = isMobileAvailable;
        boolean lastIsMobileConnected = isMobileConnected;

        String lastWifiName = wifiName;
        boolean lastIsWifiAvailable = isWifiAvailable;
        boolean lastIsWifiConnected = isWifiConnected;

        Application context = GorillaBase.getAppContext();

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) return;

        android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mobile == null)
        {
            isMobileAvailable = false;
            isMobileConnected = false;
        }
        else
        {
            isMobileAvailable = mobile.isAvailable();
            isMobileConnected = mobile.isConnectedOrConnecting();
        }

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

        if (wifi == null)
        {
            isWifiAvailable = false;
            isWifiConnected = false;
        }
        else
        {
            isWifiAvailable = wifi.isAvailable();
            isWifiConnected = wifi.isConnectedOrConnecting();
        }

        if (isWifiConnected)
        {
            WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiMgr != null)
            {
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                String tempWifiName = wifiInfo.getSSID();

                if ((tempWifiName != null)
                        && tempWifiName.startsWith("\"")
                        && tempWifiName.endsWith("\""))
                {
                    //
                    // Wifi name is enclosed in quotes.
                    //

                    tempWifiName = tempWifiName.substring(1, tempWifiName.length() - 1);
                }

                wifiName = tempWifiName;
            }
            else
            {
                wifiName = null;
            }
        }
        else
        {
            wifiName = null;

            Simple.getHandler().removeCallbacks(checkReconnect);
            Simple.getHandler().postDelayed(checkReconnect, 1000);
        }

        if ((lastIsMobileAvailable != isMobileAvailable)
            || (lastIsMobileConnected != isMobileConnected)
            || (lastIsWifiAvailable != isWifiAvailable)
            || (lastIsWifiConnected != isWifiConnected)
            || Simple.nequals(lastMobileName, mobileName)
            || Simple.nequals(lastWifiName, wifiName))
        {
            GorillaState.onStateChanged();
        }
    }

    private static final Runnable checkReconnect = new Runnable()
    {
        @Override
        public void run()
        {
            getNetworkState();

            if (! isWifiConnected)
            {
                Simple.getHandler().removeCallbacks(checkReconnect);
                Simple.getHandler().postDelayed(checkReconnect, 1000);
            }
        }
    };

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
