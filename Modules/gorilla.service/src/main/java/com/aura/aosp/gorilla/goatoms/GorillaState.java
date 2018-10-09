package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code Status} extends to basic
 * {@code GorillaAtom} by status values.
 *
 * @author Dennis Zierahn
 */
public class GorillaState extends GorillaAtom
{
    /**
     * Create empty state atom.
     */
    public GorillaState()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param status JSON status atom object.
     */
    public GorillaState(JSONObject status)
    {
        super(status);
    }

    public void setLatLon(Double lat, Double lon)
    {
        if ((lat != null) && (lon != null))
        {
            lat = ((double) Math.round(lat * 1000) / 1000.0);
            lon = ((double) Math.round(lon * 1000) / 1000.0);
        }

        putJSON(getAtom(), "gps.lat", lat);
        putJSON(getAtom(), "gps.lon", lon);
    }

    @Nullable
    public String getLat()
    {
        return getJSONString(getAtom(), "gps.lat");
    }

    @Nullable
    public String getLon()
    {
        return getJSONString(getAtom(), "gps.lon");
    }

    public void setMobileConnected(boolean connected)
    {
        putJSON(getAtom(), "net.mobile", connected);
    }

    public void setWifiConnected(boolean connected)
    {
        putJSON(getAtom(), "net.wifi", connected);
    }

    public void setWifiName(@Nullable String wifiname)
    {
        putJSON(getAtom(), "wifi", wifiname);
    }

    public void setDeviceUUIDBase64(@Nullable String deviceUUIDbase64)
    {
        putJSON(getAtom(), "device", deviceUUIDbase64);
    }
}
