/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.atoms;

import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code GorillaSuggestion} extends the basic
 * {@code GorillaAtom} by suggestion values.
 *
 * @author Dennis Zierahn
 */
public class GorillaSuggestion extends GorillaAtom
{
    /**
     * Create empty suggestion atom.
     */
    public GorillaSuggestion()
    {
        super();
    }

    /**
     * Create suggestion atom from JSONObject.
     *
     * @param owner JSON owner atom object.
     */
    public GorillaSuggestion(JSONObject owner)
    {
        super(owner);
    }

    /**
     * Set action domain string.
     *
     * @param domain action domain string.
     */
    public void setDomain(String domain)
    {
        putJSON(getLoad(), "domain", domain);
    }

    /**
     * Get action domain string.
     *
     * @return action domain string or null.
     */

    @Nullable
    public String getDomain()
    {
        return getJSONString(getLoad(), "domain");
    }

    /**
     * Set domain context string.
     *
     * @param context domain context string.
     */
    public void setContext(String context)
    {
        putJSON(getLoad(), "context", context);
    }

    /**
     * Get domain context string.
     *
     * @return domain context or null.
     */
    @Nullable
    public String getContext()
    {
        return getJSONString(getLoad(), "context");
    }

    /**
     * Set action string.
     *
     * @param action action string.
     */
    public void setAction(String action)
    {
        putJSON(getLoad(), "action", action);
    }

    /**
     * Get action string.
     *
     * @return action string or null.
     */
    @Nullable
    public String getAction()
    {
        return getJSONString(getLoad(), "action");
    }

    /**
     * Set score value.
     *
     * @param score score value.
     */
    public void setScore(Double score)
    {
        putJSON(getLoad(), "score", score);
    }

    /**
     * Get score value.
     *
     * @return score value or null.
     */
    @Nullable
    public Double getScore()
    {
        return getJSONDouble(getLoad(), "score");
    }
}
