package com.aura.aosp.aura.common.simple;

import android.support.annotation.Nullable;
import android.annotation.SuppressLint;

import android.app.Application;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Simple
{
    public static int compareTo(String str1, String str2)
    {
        if ((str1 != null) && (str2 != null)) return str1.compareTo(str2);

        return 0;
    }

    @Nullable
    public static String getFileContent(File file)
    {
        byte[] bytes = getFileBytes(file);
        return (bytes == null) ? null : new String(bytes);
    }

    @Nullable
    public static byte[] getFileBytes(File file)
    {
        try
        {
            if (file.exists())
            {
                InputStream in = new FileInputStream(file);
                int len = (int) file.length();
                byte[] bytes = new byte[len];

                int xfer = 0;
                while (xfer < len) xfer += in.read(bytes, xfer, len - xfer);
                in.close();

                return bytes;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean putFileContent(File file, String content)
    {
        return (content != null) && putFileBytes(file, content.getBytes());
    }

    public static boolean putFileBytes(File file, byte[] bytes)
    {
        if (bytes == null) return false;

        try
        {
            OutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.close();

            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    @Nullable
    public static String getAllInputString(InputStream input)
    {
        StringBuilder string = new StringBuilder();
        byte[] buffer = new byte[4096];
        int xfer;

        try
        {
            while ((xfer = input.read(buffer)) > 0)
            {
                string.append(new String(buffer, 0, xfer));
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            return null;
        }

        return string.toString();
    }

    @Nullable
    public static byte[] getAllInputBytes(InputStream input)
    {
        byte[] buffer = new byte[0];
        byte[] chunk = new byte[8192];
        int xfer;

        try
        {
            while ((xfer = input.read(chunk)) > 0)
            {
                buffer = appendBytes(buffer, chunk, 0, xfer);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            return null;
        }

        return buffer;
    }

    @Nullable
    public static byte[] appendBytes(byte[] buffer, byte[] append)
    {
        if (append == null) return buffer;

        return appendBytes(buffer, append, 0, append.length);
    }

    @Nullable
    public static byte[] appendBytes(byte[] buffer, byte[] append, int offset, int size)
    {
        if (append == null) return buffer;
        if (buffer == null) return null;

        byte[] newbuf = new byte[buffer.length + size];

        System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
        System.arraycopy(append, offset, newbuf, buffer.length, size);

        return newbuf;
    }

    @Nullable
    public static byte[] getHTTPBytes(String urlstr)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream inputStream = conn.getInputStream();
            byte[] bytes = getAllInputBytes(inputStream);
            inputStream.close();

            return bytes;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String getHTTPString(String urlstr)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream inputStream = conn.getInputStream();
            String string = getAllInputString(inputStream);
            inputStream.close();

            return string;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static JSONArray getHTTPJSONArray(String urlstr)
    {
        String jstr = getHTTPString(urlstr);
        if (jstr == null) return null;

        return Json.fromStringArray(jstr);
    }

    @Nullable
    public static JSONObject getHTTPJSONObject(String urlstr)
    {
        String jstr = getHTTPString(urlstr);
        if (jstr == null) return null;

        return Json.fromStringObject(jstr);
    }

    public static String encodeBase64(byte bytes[])
    {
        return Base64.encodeToString(bytes, android.util.Base64.NO_WRAP);
    }

    public static byte[] decodeBase64(String base64)
    {
        return Base64.decode(base64, 0);
    }

    public static String getHexBytesToString(byte[] bytes)
    {
        if (bytes == null) return "null";

        return getHexBytesToString(bytes, 0, bytes.length, true);
    }

    public static String getHexBytesToString(byte[] bytes, boolean space)
    {
        if (bytes == null) return "null";

        return getHexBytesToString(bytes, 0, bytes.length, space);
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length, boolean space)
    {
        if (bytes == null) return "null";
        if (bytes.length == 0) return "empty";

        int clen = (length << 1) + (space && (length > 0) ? (length - 1) : 0);

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ clen ];

        int pos = 0;

        for (int inx = offset; inx < (length + offset); inx++)
        {
            if (space && (inx > offset)) hexChars[ pos++ ] = ' ';

            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
    }

    public static byte[] sliceBytes(byte[] bytes, int from)
    {
        return sliceBytes(bytes,from, bytes.length);
    }

    public static byte[] sliceBytes(byte[] bytes, int from, int toto)
    {
        byte[] slice = new byte[toto - from];

        System.arraycopy(bytes, from, slice, 0, slice.length);

        return slice;
    }

    public static byte[] concatBuffers(byte[]... buffers)
    {
        int total = 0;

        for (byte[] buffer : buffers)
        {
            total += buffer.length;
        }

        byte[] result = new byte[total];
        int offset = 0;

        for (byte[] buffer : buffers)
        {
            System.arraycopy(buffer, 0, result, offset, buffer.length);
            offset += buffer.length;
        }

        return result;
    }


    //region Basic defines.

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //endregion Basic defines.

    //region Device features.

    private static boolean istv;
    private static boolean issony;
    private static boolean istouch;
    private static boolean istablet;
    private static boolean iswidescreen;
    private static boolean isspeechin;
    private static boolean isretina;
    private static boolean iscamera;

    private static int deviceWidth;
    private static int deviceHeight;
    private static float deviceDensity;

    private static Handler handler;
    private static Resources resources;
    private static SharedPreferences prefs;
    private static ContentResolver contentResolver;

    private static String packageName;
    private static WifiManager wifiManager;
    private static AudioManager audioManager;
    private static WindowManager windowManager;
    private static PackageManager packageManager;
    private static LocationManager locationManager;
    private static ConnectivityManager connectivityManager;

    public static void initialize(Application app)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app);
        handler = new Handler();
        resources = app.getResources();
        contentResolver = app.getContentResolver();

        packageName = app.getPackageName();
        packageManager = app.getPackageManager();
        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        windowManager = ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE));
        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (windowManager != null)
        {
            Point size = new Point();
            windowManager.getDefaultDisplay().getRealSize(size);

            deviceWidth = size.x;
            deviceHeight = size.y;
        }

        deviceDensity = Resources.getSystem().getDisplayMetrics().density;

        UiModeManager uiModeManager = (UiModeManager) app.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        issony = istv && getDeviceModelName().startsWith("BRAVIA");
        iscamera = packageManager.hasSystemFeature("android.hardware.camera");
        istouch = packageManager.hasSystemFeature("android.hardware.touchscreen");

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        iswidescreen = (deviceWidth / (float) deviceHeight) > (4 / 3f);

        isspeechin = android.speech.SpeechRecognizer.isRecognitionAvailable(app);

        isretina = (deviceDensity >= 2.0);
    }

    public static boolean isTV()
    {
        return istv;
    }

    public static boolean isSony()
    {
        return issony;
    }

    public static boolean isTouch()
    {
        return istouch;
    }

    public static boolean isPhone()
    {
        return ! istablet;
    }

    public static boolean isTablet()
    {
        return istablet;
    }

    public static boolean isWideScreen()
    {
        return iswidescreen;
    }

    public static boolean isRetina()
    {
        return isretina;
    }

    public static boolean isSpeechIn()
    {
        return isspeechin;
    }

    public static boolean isIscamera()
    {
        return iscamera;
    }

    public static boolean isOnline(Context context)
    {
        if (connectivityManager == null) return false;

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        return (netInfo != null) && netInfo.isConnectedOrConnecting();
    }

    public static boolean isDeveloper()
    {
        int devEnabled = Settings.Global.getInt(contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

        return (devEnabled == 1);
    }

    public static int getDeviceOrientation()
    {
        int orientation = Configuration.ORIENTATION_PORTRAIT;

        if (windowManager != null)
        {
            Point size = new Point();
            windowManager.getDefaultDisplay().getRealSize(size);

            if (size.x <= size.y)
            {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }
            else
            {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }

        return orientation;
    }

    public static int getDeviceWidth()
    {
        if (getDeviceOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
            return Math.min(deviceWidth, deviceHeight);
        }
        else
        {
            return Math.max(deviceWidth, deviceHeight);
        }
    }

    public static int getDeviceHeight()
    {
        if (getDeviceOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
            return Math.max(deviceWidth, deviceHeight);
        }
        else
        {
            return Math.min(deviceWidth, deviceHeight);
        }
    }

    public static int getDeviceWidthDip()
    {
        return pxToDip(getDeviceWidth());
    }

    public static int getDeviceHeightDip()
    {
        return pxToDip(getDeviceHeight());
    }

    public static float getDeviceDensity()
    {
        return deviceDensity;
    }

    //endregion Device features.

    //region Simple getters.

    public static Handler getHandler()
    {
        return handler;
    }

    public static Resources getResources()
    {
        return resources;
    }

    public static LocationManager getLocationManager()
    {
        return locationManager;
    }

    public static String getConnectedWifiName()
    {
        String wifi = wifiManager.getConnectionInfo().getSSID();
        return wifi.replace("\"", "");
    }

    public static String getDeviceType()
    {
        if (isTV()) return "tv";
        if (isPhone()) return "phone";
        if (isTablet()) return "tablet";

        return "unknown";
    }

    public static String getDeviceUserName()
    {
        return Settings.Secure.getString(contentResolver, "bluetooth_name");
    }

    public static String getDeviceBrandName()
    {
        return Build.BRAND.toUpperCase();
    }

    public static String getDeviceModelName()
    {
        return Build.MODEL.toUpperCase();
    }

    public static String getDeviceFullName()
    {
        String brand = Build.BRAND.toUpperCase();
        String model = Build.MODEL.toUpperCase();

        if (model.startsWith(brand))
        {
            return model;
        }

        return brand + " " + model;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId()
    {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
    }

    public static String getAndroidVersion()
    {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static SharedPreferences getPrefs()
    {
        return prefs;
    }

    public static ContentResolver getContentResolver()
    {
        return contentResolver;
    }

    //endregion Simple getters.

    //region Smart helpers.

    public static int dipToPx(int dp)
    {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dipToPx(float dp)
    {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static int pxToDip(int px)
    {
        return Math.round(px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDip(float px)
    {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static int getRGBAlpha(int color)
    {
        return (color >> 24) & 0xff;
    }

    public static int setRGBAlpha(int color, int alpha)
    {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static int colorRGB(int hue, int saturation, int brightness)
    {
        float[] hsv = new float[3];

        hsv[ 0 ] = hue;
        hsv[ 1 ] = saturation / 100f;
        hsv[ 2 ] = brightness / 100f;

        return Color.HSVToColor(hsv);
    }

    public static int colorRGB(int rgbcolor, int brightness)
    {
        int r = (rgbcolor >> 16) & 0xff;
        int g = (rgbcolor >> 8) & 0xff;
        int b = rgbcolor& 0xff;

        float scale = 255 / (float) Math.max(r, Math.max(g, b));
        scale = scale * brightness / 100f;

        r = Math.round(r * scale);
        g = Math.round(g * scale);
        b = Math.round(b * scale);

        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;

        return ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
    }

    public static void colorHSV(String color)
    {
        int rgbcolor = Integer.parseInt(color, 16);
    }

    public static void colorHSV(int rgbcolor)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(rgbcolor, hsv);

        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1] * 100);
        int brightness = Math.round(hsv[2] * 100);
    }

    public static Iterator<String> sortedIterator(Iterator<String> iterator)
    {
        ArrayList<String> list = new ArrayList<>();

        while (iterator.hasNext()) list.add(iterator.next());

        Collections.sort(list);

        return list.iterator();
    }

    @SuppressLint("ApplySharedPref")
    public static void removeALLPrefs()
    {
        prefs.edit().clear().commit();
    }

    @SuppressLint("ApplySharedPref")
    public static void removeALLPrefs(Context context)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    public static void dumpIntent(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);

            Log.d("key=%s value=%s", key, value);
        }
    }

    public static void hideSoftKeyBoard(View view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftKeyBoard(View view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static String getTrans(int resid, Object... args)
    {
        return String.format(resources.getString(resid), args);
    }

    public static String getRounded3(double val)
    {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    public static String getRounded3(float val)
    {
        return getRounded3((double) val);
    }

    public static String getRounded6(double val)
    {
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    public static String getRounded6(float val)
    {
        return getRounded6((double) val);
    }

    @Nullable
    public static InetAddress getInetAddress(String host)
    {
        try
        {
            return InetAddress.getByName(host);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static boolean getInetPing(InetAddress inetAddress, int timeout)
    {
        try
        {
            return inetAddress.isReachable(timeout);
        }
        catch (Exception ignore)
        {
        }

        return false;
    }

    @Nullable
    public static Boolean getMapBoolean(Map<String, Boolean> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static Long getMapLong(Map<String, Long> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static Integer getMapInteger(Map<String, Integer> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }


    @Nullable
    public static String getMapString(Map<String, String> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static JSONObject getMapJSONObject(Map<String, JSONObject> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    public static void sleep(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (Exception ignore)
        {
        }
    }

    public static boolean isUIThread()
    {
        return (Looper.getMainLooper().getThread() == Thread.currentThread());
    }

    public static void setSystemProp(String prop, String level)
    {
        try
        {
            String command = "setprop " + prop + " " + level;

            Process setprop = Runtime.getRuntime().exec(command);
            int res = setprop.waitFor();

            Log.d("setSystemProp: res=%d command=%s", res, command);
        }
        catch (Exception ignore)
        {
        }
    }

    @Nullable
    public static String getManifestMetaData(String name)
    {
        try
        {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String value = ai.metaData.getString(name);

            Log.d("name=%s value=%s", name, value);

            return value;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String getImageResourceBase64(int resid)
    {
        try
        {
            InputStream is = resources.openRawResource(+resid);
            byte[] buffer = new byte[16 * 1024];
            int xfer = is.read(buffer);
            is.close();

            return Base64.encodeToString(buffer, 0 ,xfer, android.util.Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    //endregion Smart helpers.
}
