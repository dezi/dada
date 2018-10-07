/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.util.Log;

import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;

/**
 * The class {@code GorillaListener} ist just a stub implementation
 * of callbacks from the {@code GorillaClient} instance.
 *
 * @author Dennis Zierahn
 */
public abstract class GorillaListener
{
    /**
     * All purpose log tag.
     */
    private final static String LOGTAG = GorillaListener.class.getSimpleName();

    /**
     * Called whenever the current service connection state changes.
     *
     * @param connected current service connection state.
     */
    public void onServiceChange(boolean connected)
    {
        Log.d(LOGTAG, "onServiceChange: STUB!");
    }

    /**
     * Called whenever the current uplink connection state changes.
     *
     * @param connected current uplink connection state
     */
    public void onUplinkChange(boolean connected)
    {
        Log.d(LOGTAG, "onUplinkChange: STUB!");
    }

    /**
     * Called whenever the current owner identity changes.
     *
     * @param owner GorillaOwner object.
     */
    public void onOwnerReceived(GorillaOwner owner)
    {
        Log.d(LOGTAG, "onOwnerReceived: STUB!");
    }

    /**
     * Called whenever a payload was received.
     *
     * @param payload GorillaPayload object.
     */
    public void onPayloadReceived(GorillaPayload payload)
    {
        Log.d(LOGTAG, "onPayloadReceived: STUB!");
    }

    /**
     * Called whenever a payload result was received.
     *
     * @param result JSON object containg the message result.
     */
    public void onPayloadResultReceived(GorillaPayloadResult result)
    {
        Log.d(LOGTAG, "onPayloadResultReceived: STUB!");
    }
}
