/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
