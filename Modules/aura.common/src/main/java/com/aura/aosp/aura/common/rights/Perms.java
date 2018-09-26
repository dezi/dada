package com.aura.aosp.aura.common.rights;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.aura.aosp.aura.common.simple.Log;

public class Perms
{
    public static boolean checkLocationPermission(Context context)
    {
        boolean coarse = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean fine = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        Log.d("checkLocationPermission: fine=" + fine + " coarse=" + coarse);

        return coarse && fine;
    }

    public static boolean checkExternalPermission(Context context)
    {
        boolean external = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        Log.d("checkExternalPermission: external=" + external);

        return external;
    }

    public static void requestPermissionForNeed(Activity activity, String need, int requestCode)
    {
        String which = null;

        if (need.equals("mic")) which = Manifest.permission.RECORD_AUDIO;
        if (need.equals("loc")) which = Manifest.permission.ACCESS_FINE_LOCATION;
        if (need.equals("ble")) which = Manifest.permission.BLUETOOTH_ADMIN;
        if (need.equals("cam")) which = Manifest.permission.CAMERA;

        if (need.equals("ext")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (need.equals("usb")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (need.equals("ssd")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if ((which != null) && ! havePermission(activity, which))
        {
            ActivityCompat.requestPermissions(activity, new String[]{which}, requestCode);

            return;
        }

        //
        // Open the complete permissions
        // setup page for current app.
        //

        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static boolean havePermission(Context context, String manifestperm)
    {
        int permission = ContextCompat.checkSelfPermission(context, manifestperm);

        return (permission == PackageManager.PERMISSION_GRANTED);
    }


}
