package com.aura.aosp.aura.gui.base;

import android.annotation.SuppressLint;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;

import org.json.JSONObject;

public class GUIPrefs
{
    public static JSONObject readPrefJSON(String key)
    {
        String prefkey = "gui.pref." + key;
        String jsonpref = Simple.getPrefs().getString(prefkey, null);
        return Json.fromStringObject(jsonpref);
    }

    @SuppressLint("ApplySharedPref")
    public static void savePrefJSON(String key, JSONObject pref)
    {
        String prefkey = "gui.pref." + key;
        Simple.getPrefs().edit().putString(prefkey, Json.toPretty(pref)).commit();

        Log.d("prefkey=" + prefkey + " pref=" + pref.toString());
    }

    public static String readPrefString(String key)
    {
        String prefkey = "gui.pref." + key;
        return Simple.getPrefs().getString(prefkey, null);
    }

    @SuppressLint("ApplySharedPref")
    public static void savePrefString(String key, String pref)
    {
        String prefkey = "gui.pref." + key;
        Simple.getPrefs().edit().putString(prefkey, pref).commit();

        Log.d("prefkey=" + prefkey + " pref=" + pref);
    }
}
