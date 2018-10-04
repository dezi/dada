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

import java.util.Iterator;

/**
 * The class {@code GorillaMessage} extends to basic
 * {@code GorillaAtom} by message values.
 *
 * @author Dennis Zierahn
 */
public class GorillaMessage extends GorillaAtom
{
    /**
     * Create empty message atom.
     */
    public GorillaMessage()
    {
        super();
    }

    /**
     * Create message atom from JSONObject.
     *
     * @param message JSON message atom object.
     */
    public GorillaMessage(JSONObject message)
    {
        super(message);
    }

    /**
     * Set message text.
     *
     * @param messageText message text
     */
    public void setMessageText(@NonNull String messageText)
    {
        GorillaUtils.putJSON(getLoad(), "message", messageText);
    }

    /**
     * Get message text.
     *
     * @return message text or null.
     */
    @Nullable
    public String getMessageText()
    {
        return GorillaUtils.getJSONString(getLoad(), "message");
    }

    /**
     * Set status time for give device UUID.
     *
     * @param status     status tag.
     * @param deviceUUID device UUID.
     * @param time       time stamp in milliseconds.
     */
    public void setStatusTime(@NonNull String status, @NonNull String deviceUUID, @NonNull Long time)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(getLoad(), status);

        if (statusJson == null)
        {
            statusJson = new JSONObject();
            GorillaUtils.putJSON(getLoad(), status, statusJson);
        }

        GorillaUtils.putJSON(statusJson, deviceUUID, time);
    }

    /**
     * Get status time for any device.
     *
     * @param status status tag.
     * @return time stamp in milliseconds or null.
     */
    @Nullable
    public Long getStatusTime(@NonNull String status)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(getLoad(), status);
        if (statusJson == null) return null;

        Iterator<String> keys = statusJson.keys();

        if (keys.hasNext())
        {
            return GorillaUtils.getJSONLong(statusJson, keys.next());
        }

        return null;
    }

    /**
     * Get status time for given device UUID.
     *
     * @param status     status tag.
     * @param deviceUUID device UUID.
     * @return time stamp in milliseconds or null.
     */
    @Nullable
    public Long getStatusTime(@NonNull String status, @NonNull String deviceUUID)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(getLoad(), status);
        if (statusJson == null) return null;

        return GorillaUtils.getJSONLong(statusJson, deviceUUID);
    }
}
