package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code GorillaAtomState} extends to basic
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
     * Create state atom from JSONObject.
     *
     * @param status JSON state atom object.
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
    public Long getStateTime()
    {
        return getJSONLong(getLoad(), "time");
    }

    public void setLatLonCoarse(Double lat, Double lon)
    {
        if ((lat == null) || (lon == null))
        {
            lat = null;
            lon = null;
        }
        else
        {
            lat = ((double) Math.round(lat * 1000) / 1000.0);
            lon = ((double) Math.round(lon * 1000) / 1000.0);
        }

        putJSON(getLoad(), "gps.lat", lat);
        putJSON(getLoad(), "gps.lon", lon);
    }

    public void setLatLonFine(Double lat, Double lon)
    {
        if ((lat == null) || (lon == null))
        {
            lat = null;
            lon = null;
        }

        putJSON(getLoad(), "gps.lat", lat);
        putJSON(getLoad(), "gps.lon", lon);
    }

    @Nullable
    public Double getLat()
    {
        return getJSONDouble(getLoad(), "gps.lat");
    }

    @Nullable
    public Double getLon()
    {
        return getJSONDouble(getLoad(), "gps.lon");
    }

    public void setMobileConnected(boolean connected)
    {
        putJSON(getLoad(), "net.mobile", connected);
    }

    @Nullable
    public Boolean getMobileConnected()
    {
        return getJSONBoolean(getLoad(), "net.mobile");
    }

    public void setWifiConnected(boolean connected)
    {
        putJSON(getLoad(), "net.wifi", connected);
    }

    @Nullable
    public Boolean getWifiConnected()
    {
        return getJSONBoolean(getLoad(), "net.wifi");
    }

    public void setWifiName(@Nullable String wifiname)
    {
        putJSON(getLoad(), "wifi", wifiname);
    }

    @Nullable
    public String getWifiName()
    {
        return getJSONString(getLoad(), "wifi");
    }

    public void setDeviceUUIDBase64(@Nullable String deviceUUIDbase64)
    {
        putJSON(getLoad(), "device", deviceUUIDbase64);
    }

    @Nullable
    public String getDeviceUUIDBase64()
    {
        return getJSONString(getLoad(), "device");
    }
}
