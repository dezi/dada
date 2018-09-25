package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.gorilla.client.GorillaListener;

import org.json.JSONObject;

public class GorillaState
{
    @NonNull
    public static JSONObject getState()
    {
        JSONObject state = new JSONObject();

        Json.put(state, "time", System.currentTimeMillis());

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
}
