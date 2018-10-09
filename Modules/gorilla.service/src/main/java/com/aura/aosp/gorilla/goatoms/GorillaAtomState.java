package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code Status} extends to basic
 * {@code GorillaAtom} by status values.
 *
 * @author Dennis Zierahn
 */
public class GorillaAtomState extends GorillaAtom
{
    /**
     * Create empty state atom.
     */
    public GorillaAtomState()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param status JSON status atom object.
     */
    public GorillaAtomState(JSONObject status)
    {
        super(status);
    }

    /**
     * Set time of state in milliseconds.
     *
     * @param time time of state in milliseconds.
     */
    public void setStateTime(@NonNull Long time)
    {
        putJSON(getLoad(), "time", time);
    }

    /**
     * Get time of state in milliseconds.
     *
     * @return time of state in milliseconds or null.
     */
    @Nullable
    public Long setStateTime()
    {
        return getJSONLong(getLoad(), "time");
    }

    public void setLatLon(Double lat, Double lon)
    {
        if ((lat != null) && (lon != null))
        {
            lat = ((double) Math.round(lat * 1000) / 1000.0);
            lon = ((double) Math.round(lon * 1000) / 1000.0);
        }

        putJSON(getLoad(), "gps.lat", lat);
        putJSON(getLoad(), "gps.lon", lon);
    }

    @Nullable
    public String getLat()
    {
        return getJSONString(getLoad(), "gps.lat");
    }

    @Nullable
    public String getLon()
    {
        return getJSONString(getLoad(), "gps.lon");
    }

    public void setMobileConnected(boolean connected)
    {
        putJSON(getLoad(), "net.mobile", connected);
    }

    public void setWifiConnected(boolean connected)
    {
        putJSON(getLoad(), "net.wifi", connected);
    }

    public void setWifiName(@Nullable String wifiname)
    {
        putJSON(getLoad(), "wifi", wifiname);
    }

    public void setDeviceUUIDBase64(@Nullable String deviceUUIDbase64)
    {
        putJSON(getLoad(), "device", deviceUUIDbase64);
    }
}
