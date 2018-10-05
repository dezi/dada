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
 * The class {@code GorillaPayloadResult} extends to basic
 * {@code GorillaAtom} by owner values.
 *
 * @author Dennis Zierahn
 */
public class GorillaPayloadResult extends GorillaAtom
{
    /**
     * Create empty payload atom.
     */
    public GorillaPayloadResult()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param owner JSON owner atom object.
     */
    public GorillaPayloadResult(JSONObject owner)
    {
        super(owner);
    }

    /**
     * Set status tag.
     *
     * @param status status tag.
     */
    public void setStatus(String status)
    {
        putJSON(getLoad(), "status", status);
    }

    /**
     * Get status tag.
     *
     * @return status tag or null.
     */
    @Nullable
    public String getStatus()
    {
        return getJSONString(getLoad(), "status");
    }

    /**
     * Set error text.
     *
     * @param error error text.
     */
    public void setError(String error)
    {
        putJSON(getLoad(), "error", error);
    }

    /**
     * Get error text.
     *
     * @return error text or null.
     */
    @Nullable
    public String getError()
    {
        return getJSONString(getLoad(), "error");
    }
}
