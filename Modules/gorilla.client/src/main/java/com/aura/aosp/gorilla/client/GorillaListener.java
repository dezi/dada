/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import com.aura.aosp.gorilla.atoms.GorillaOwner;
import com.aura.aosp.gorilla.atoms.GorillaPayload;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.atoms.GorillaPhraseSuggestion;

/**
 * The class {@code GorillaListener} ist just a stub implementation
 * of callbacks from the {@code GorillaClient} instance.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"EmptyMethod", "WeakerAccess"})
public abstract class GorillaListener
{
    /**
     * Called whenever the current service connection state changes.
     *
     * @param connected current service connection state.
     */
    public void onServiceChange(boolean connected)
    {
    }

    /**
     * Called whenever the current uplink connection state changes.
     *
     * @param connected current uplink connection state
     */
    public void onUplinkChange(boolean connected)
    {
    }

    /**
     * Called whenever the current owner identity changes.
     *
     * @param owner GorillaOwner object.
     */
    public void onOwnerReceived(GorillaOwner owner)
    {
    }

    /**
     * Called whenever a payload was received.
     *
     * @param payload GorillaPayload object.
     */
    public void onPayloadReceived(GorillaPayload payload)
    {
    }

    /**
     * Called whenever a payload result was received.
     *
     * @param result JSON object containg the message result.
     */
    public void onPayloadResultReceived(GorillaPayloadResult result)
    {
    }

    /**
     * Called whenever a phrase suggestion result was received.
     *
     * @param result JSON object containg the phrase suggestion result.
     */
    public void onPhraseSuggestionsReceived(GorillaPhraseSuggestion result)
    {
    }
}
