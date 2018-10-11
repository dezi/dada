/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Ubiquitous static utility methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings("unused")
class GorillaUtils
{
    /**
     * Put key value to JSON object w/o fucking an exception.
     *
     * @param json JSON object.
     * @param key  key to put.
     * @param val  value to put.
     */
    static void putJSON(@NonNull JSONObject json, @NonNull String key, Object val)
    {
        try
        {
            json.put(key, val);
        }
        catch (Exception ignore)
        {
        }
    }

    /**
     * Put key value to JSON as base64 encoded byte array.
     *
     * @param json JSON object.
     * @param key  key to put.
     * @param val  value to put.
     */
    static void putJSONByteArray(@NonNull JSONObject json, @NonNull String key, byte[] val)
    {
        try
        {
            if (val == null)
            {
                json.remove(key);
            }
            else
            {
                json.put(key, Base64.encodeToString(val, Base64.NO_WRAP));
            }
        }
        catch (Exception ignore)
        {
        }
    }

    /**
     * Get key value from JSON as JSON object.
     *
     * @param json JSON object.
     * @param key  key to getAtom.
     * @return JSON object or null.
     */
    @Nullable
    static JSONObject getJSONObject(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getJSONObject(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    static JSONObject getJSONObject(@NonNull JSONArray json, int index)
    {
        try
        {
            return json.getJSONObject(index);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Get key value from JSON as Long object.
     *
     * @param json JSON object.
     * @param key  key to getAtom.
     * @return Long object or null.
     */
    @Nullable
    static Long getJSONLong(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getLong(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Get key value from JSON as String object.
     *
     * @param json JSON object.
     * @param key  key to getAtom.
     * @return String object or null.
     */
    @Nullable
    static String getJSONString(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getString(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Get key value from JSON as base64 encoded byte array.
     *
     * @param json JSON object.
     * @param key  key to getAtom.
     * @return byte array or null.
     */
    @Nullable
    static byte[] getJSONByteArray(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            String val = json.getString(key);
            if (val == null) return null;

            return Base64.decode(val, Base64.DEFAULT);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Convert JSON string to JSON object w/o fucking an exception.
     *
     * @param jsonstr JSON string.
     * @return JSON Object from string or null.
     */
    @Nullable
    static JSONObject fromStringJSONOBject(@NonNull String jsonstr)
    {
        try
        {
            return new JSONObject(jsonstr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * Convert JSON string to JSON array w/o fucking an exception.
     *
     * @param jsonstr JSON string.
     * @return JSON array from string or null.
     */
    @Nullable
    static JSONArray fromStringJSONArray(@NonNull String jsonstr)
    {
        try
        {
            return new JSONArray(jsonstr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
