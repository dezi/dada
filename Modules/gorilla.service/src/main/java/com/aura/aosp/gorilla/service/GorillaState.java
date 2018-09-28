package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;

import org.json.JSONObject;

public class GorillaState
{
    private static String lastState;

    @NonNull
    private static JSONObject getStateTimeless()
    {
        JSONObject state = new JSONObject();

        GorillaLocation gl = GorillaLocation.getInstance();

        Double lat = gl.getLat();
        Double lon = gl.getLon();

        if ((lat != null) && (lon != null))
        {
            Json.put(state, "gps.lat", lat);
            Json.put(state, "gps.lon", lon);
        }

        boolean mobile = GorillaNetwork.isMobileConnected();
        Json.put(state, "net.mobile", mobile);

        boolean wifi = GorillaNetwork.isWifiConnected();
        Json.put(state, "net.wifi", wifi);

        String wifiName = GorillaNetwork.getWifiName();
        if (wifiName != null) Json.put(state, "wifi", wifiName);

        Identity identity = Owner.getOwnerIdentity();

        if (identity != null )
        {
            Json.put(state, "device", identity.getDeviceUUIDBase64());
        }

        return state;
    }

    @NonNull
    public static JSONObject getState()
    {
        JSONObject state = getStateTimeless();

        Json.put(state, "time", System.currentTimeMillis());

        return state;
    }

    static void onStateChanged()
    {
        String thisState = getStateTimeless().toString();

        if (Simple.nequals(lastState, thisState))
        {
            Log.d("state=%s", getState().toString());

            lastState = thisState;
        }
    }
}
