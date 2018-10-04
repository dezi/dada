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

import org.json.JSONObject;

/**
 * Ubiquouos static utility methods.
 *
 * @author Dennis Zierahn
 */
class GorillaUtils
{
    /**
     * Put key value to JSON object w/o fucking an exception.
     *
     * @param json JSON object.
     * @param key  key to put.
     * @param val value to put.
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

    @Nullable
    static byte[] getJSONByteArray(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return Base64.decode(json.getString(key), Base64.DEFAULT);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Convert JSON string to JSON object w/o fucking an exception.
     *
     * @param jsonstr
     * @return
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
}
