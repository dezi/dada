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
 * The class {@code GorillaOwner} extends to basic
 * {@code GorillaAtom} by owner values.
 *
 * @author Dennis Zierahn
 */
public class GorillaOwner extends GorillaAtom
{
    /**
     * Create empty payload atom.
     */
    public GorillaOwner()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param owner JSON owner atom object.
     */
    public GorillaOwner(JSONObject owner)
    {
        super(owner);
    }

    /**
     * Set sender UUID base64.
     *
     * @param ownerUUIDBase64 sender UUID base64 encoded
     */
    public void setOwnerUUID(@NonNull String ownerUUIDBase64)
    {
        putJSON(getLoad(), "ownerUUID", ownerUUIDBase64);
    }

    /**
     * Get sender UUID base64 encoded.
     *
     * @return sender UUID base64 encoded or null.
     */
    @Nullable
    public String getOwnerUUIDBase64()
    {
        return getJSONString(getLoad(), "ownerUUID");
    }
}
