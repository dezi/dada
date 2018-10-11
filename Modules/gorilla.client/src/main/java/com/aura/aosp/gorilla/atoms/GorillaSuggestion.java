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
public class GorillaSuggestion extends GorillaAtom
{
    /**
     * Create empty payload atom.
     */
    public GorillaSuggestion()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param owner JSON owner atom object.
     */
    public GorillaSuggestion(JSONObject owner)
    {
        super(owner);
    }

    public void setDomain(String domain)
    {
        putJSON(getLoad(), "domain", domain);
    }

    @Nullable
    public String getDomain()
    {
        return getJSONString(getLoad(), "domain");
    }

    public void setContext(String context)
    {
        putJSON(getLoad(), "context", context);
    }

    @Nullable
    public String getContext()
    {
        return getJSONString(getLoad(), "context");
    }

    public void setAction(String action)
    {
        putJSON(getLoad(), "action", action);
    }

    @Nullable
    public String getAction()
    {
        return getJSONString(getLoad(), "action");
    }

    public void setScore(Double score)
    {
        putJSON(getLoad(), "score", score);
    }

    @Nullable
    public Double getScore()
    {
        return getJSONDouble(getLoad(), "score");
    }
}
