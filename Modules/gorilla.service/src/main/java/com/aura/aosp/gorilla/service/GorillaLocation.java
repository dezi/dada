package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import android.location.LocationProvider;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.rights.Perms;
import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

public class GorillaLocation implements LocationListener
{
    private final static int MINDIST = 10;
    private final static int MINTIME = 10 * 1000;
    private final static int MAXAGEINMILLIS = 60 * 1000;

    private final static GorillaLocation instance = new GorillaLocation();

    private boolean isGpsEnabled;
    private boolean isNetworkEnabled;

    private Long time;

    private Double lat;
    private Double lon;
    private Double alt;

    private Float accuracy;
    private String provider;

    public static GorillaLocation getInstance()
    {
        return instance;
    }

    public static void startService()
    {
        instance.start();
    }

    public static void stopService()
    {
        instance.stop();
    }

    private void start()
    {
        try
        {
            LocationManager locationManager = Simple.getLocationManager();

            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location last = null;

            if (isNetworkEnabled)
            {
                Log.d("NETWORK_PROVIDER installed.");

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINTIME, MINDIST, instance);
                Location netLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (netLoc != null) last = netLoc;
            }

            if (isGpsEnabled)
            {
                Log.d("GPS_PROVIDER installed.");

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINTIME, MINDIST, instance);
                Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (gpsLoc != null) last = gpsLoc;
            }

            if (last == null)
            {
                Log.e("no last location.");
            }
            else
            {
                storeLocation(last);
            }
        }
        catch (SecurityException ex)
        {
            Log.e("no permission!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void stop()
    {
        if (isNetworkEnabled || isGpsEnabled)
        {
            LocationManager locationManager = Simple.getLocationManager();
            locationManager.removeUpdates(instance);
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        storeLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("provider=%s", provider);
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Log.d("provider=%s", provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        String statusStr = "unknown";

        switch (status)
        {
            case LocationProvider.AVAILABLE:
                statusStr = "available";
                break;
            case LocationProvider.OUT_OF_SERVICE:
                statusStr = "out of service";
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                statusStr = "temporary unavailable";
                break;
        }

        Log.d("provider=%s status=%d=%s extras=%s", provider, status, statusStr, extras);
    }

    @Nullable
    public Double getLat()
    {
        return (checkError() == null) ? lat : null;
    }

    @Nullable
    public Double getLon()
    {
        return (checkError() == null) ? lon : null;
    }

    @Nullable
    public Double getAlt()
    {
        return (checkError() == null) ? alt : null;
    }

    private Err checkError()
    {
        if (time == null)
        {
            return Err.err("no known location.");
        }

        if (Dates.getAgeInSeconds(time) > MAXAGEINMILLIS)
        {
            return Err.err("location expired age=%d", Dates.getAgeInSeconds(time));
        }

        return null;
    }

    private void storeLocation(Location location)
    {
        if (location == null) return;

        Double lastLat = lat;
        Double lastLon = lon;
        Double lastAlt = alt;

        lat = location.getLatitude();
        lon = location.getLongitude();
        alt = location.getAltitude();

        time = location.getTime();
        accuracy = location.getAccuracy();
        provider = location.getProvider();

        Log.d("lat+lon=%f %f alt=%f acc=%f pro=%s age=%d",
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getAccuracy(),
                location.getProvider(),
                Dates.getAgeInSeconds(location.getTime())
        );

        if (Simple.nequals(lastLat, lat, 1000)
                || Simple.nequals(lastLon, lon, 1000)
                || Simple.nequals(lastAlt, alt, 1000))
        {
            GorillaState.onStateChanged();
        }
    }
}
