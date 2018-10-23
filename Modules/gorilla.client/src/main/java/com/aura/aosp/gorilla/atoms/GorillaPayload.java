/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code GorillaPayload} extends the basic
 * {@code GorillaAtom} by payload values.
 *
 * @author Dennis Zierahn
 */
public class GorillaPayload extends GorillaAtom
{
    /**
     * Create empty payload atom.
     */
    public GorillaPayload()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param payload JSON payload atom object.
     */
    public GorillaPayload(JSONObject payload)
    {
        super(payload);
    }

    /**
     * Set sender UUID base64.
     *
     * @param senderUUIDBase64 sender UUID base64 encoded
     */
    public void setSenderUUID(@NonNull String senderUUIDBase64)
    {
        putJSON(getLoad(), "sender", senderUUIDBase64);
    }

    /**
     * Get sender UUID base64 encoded.
     *
     * @return sender UUID base64 encoded or null.
     */
    @Nullable
    public String getSenderUUIDBase64()
    {
        return getJSONString(getLoad(), "sender");
    }

    /**
     * Set device UUID base64.
     *
     * @param deviceUUIDBase64 device UUID base64 encoded
     */
    public void setDeviceUUID(@NonNull String deviceUUIDBase64)
    {
        putJSON(getLoad(), "device", deviceUUIDBase64);
    }

    /**
     * Get device UUID base64 encoded.
     *
     * @return device UUID base64 encoded or null.
     */
    @Nullable
    public String getDeviceUUIDBase64()
    {
        return getJSONString(getLoad(), "device");
    }

    /**
     * Set payload string.
     *
     * @param payload arbitrary payload string
     */
    public void setPayload(@NonNull String payload)
    {
        putJSON(getLoad(), "payload", payload);
    }

    /**
     * Get payload string.
     *
     * @return arbitrary payload string or null.
     */
    @Nullable
    public String getPayload()
    {
        return getJSONString(getLoad(), "payload");
    }

}
