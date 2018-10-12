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
 * The class {@code GorillaContact} extends to basic
 * {@code GorillaAtom} by contact values.
 *
 * @author Dennis Zierahn
 */
public class GorillaContact extends GorillaAtom
{
    /**
     * Create empty suggestion atom.
     */
    public GorillaContact()
    {
        super();
    }

    /**
     * Create suggestion atom from JSONObject.
     *
     * @param contact JSON contact atom object.
     */
    public GorillaContact(JSONObject contact)
    {
        super(contact);
    }

    @Nullable
    public String getUserUUIDBase64()
    {
        return getJSONString(getLoad(), "userUUID");
    }

    @Nullable
    public String getNick()
    {
        return getJSONString(getLoad(), "nick");
    }

    @Nullable
    public String getFull()
    {
        return getJSONString(getLoad(), "full");
    }

    @Nullable
    public String getCountry()
    {
        return getJSONString(getLoad(), "country");
    }

}
