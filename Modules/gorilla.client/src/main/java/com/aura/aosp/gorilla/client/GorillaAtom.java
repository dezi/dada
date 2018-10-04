/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import org.json.JSONObject;

/**
 * The class {@code GorillaAtom} is a basic item of information
 * in the Gorilla world.
 *
 * @author Dennis Zierahn
 */
public abstract class GorillaAtom
{
    /**
     * All atoms are based on JSON.
     */
    private JSONObject atom;

    /**
     * Create empty atom.
     */
    public GorillaAtom()
    {
        atom = new JSONObject();
    }

    /**
     * Create atom from JSONObject.
     *
     * @param atom JSON atom object.
     */
    public GorillaAtom(JSONObject atom)
    {
        this.atom = atom;
    }

    /**
     * Get JSON representation of atom.
     *
     * @return JSON object of atom.
     */
    @NonNull
    public JSONObject get()
    {
        return atom;
    }

    /**
     * Set time of atom in milliseconds.
     *
     * @param time time of atom in milliseconds.
     */
    public void setTime(@NonNull Long time)
    {
        GorillaUtils.putJSON(atom, "time", time);
    }

    /**
     * Get time of atom in milliseconds.
     *
     * @return time of atom in milliseconds or null.
     */
    @Nullable
    public Long getTime()
    {
        return GorillaUtils.getJSONLong(atom, "time");
    }

    /**
     * Set atom type in reverse domain order.
     *
     * @param type atom type in reverse domain order.
     */
    public void setType(@NonNull String type)
    {
        GorillaUtils.putJSON(atom, "type", type);
    }

    /**
     * Get atom type in reverse domain order.
     *
     * @return atom type in reverse domain order or null.
     */
    @Nullable
    public String getType()
    {
        return GorillaUtils.getJSONString(atom, "type");
    }

    /**
     * Set atom UUID as byte array.
     */
    public void setUUID(@NonNull byte[] uuid)
    {
        GorillaUtils.putJSONByteArray(atom, "uuid", uuid);
    }

    /**
     * Set atom UUID as base64 encoded string.
     */
    public void setUUID(@NonNull String uuidBase64)
    {
        byte[] uuid = Base64.decode(uuidBase64, Base64.DEFAULT);
        if (uuid == null) return;

        GorillaUtils.putJSONByteArray(atom, "uuid", uuid);
    }

    /**
     * Get atom UUID as byte array.
     *
     * @return atom UUID as byte array or null.
     */
    @Nullable
    public byte[] getUUID()
    {
        return GorillaUtils.getJSONByteArray(atom, "uuid");
    }

    /**
     * Get atom UUID as base64 encoded string
     *
     * @return atom UUID as base64 encoded string or null.
     */
    @Nullable
    public String getUUIDBase64()
    {
        byte[] uuid = getUUID();
        if (uuid == null) return null;

        return Base64.encodeToString(uuid, Base64.NO_WRAP);
    }

    /**
     * Get load part of atom as JSON object.
     *
     * @return load part of atom as JSON object.
     */
    @NonNull
    protected JSONObject getLoad()
    {
        JSONObject load = GorillaUtils.getJSONObject(atom, "load");

        if (load == null)
        {
            load = new JSONObject();
            GorillaUtils.putJSON(atom, "load", load);
        }

        return load;
    }

    /**
     * Put an atom created by owner identity and shared with nobody into storage.
     *
     * @return true if the atom could be persisted.
     */
    public boolean putAtom()
    {
        return GorillaClient.getInstance().putAtom(atom);
    }

    /**
     * Put an atom created by user identity UUID and shared with owner identity into storage.

     * @param userUUID target user identity UUID.
     * @return true if the atom could be persisted.
     */
    public boolean putAtomSharedBy(@NonNull String userUUID)
    {
        return GorillaClient.getInstance().putAtomSharedBy(userUUID, atom);
    }

    /**
     * Put an atom created by owner identity and shared with user identity UUID into storage.
     *
     * @param userUUID target user identity UUID.
     * @return true if the atom could be persisted.
     */
    public boolean putAtomSharedWidth(@NonNull String userUUID)
    {
        return GorillaClient.getInstance().putAtomSharedWith(userUUID, atom);
    }
}
