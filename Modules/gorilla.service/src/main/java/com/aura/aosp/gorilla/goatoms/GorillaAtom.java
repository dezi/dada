/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Base64;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.gorilla.goatom.GoatomStorage;

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
     * Convert atom contents to JSON string.
     *
     * @return JSON string.
     */
    public String toString()
    {
        //
        // No no no escaped forward slashes...
        //

        return atom.toString().replace("\\/", "/");
    }

    /**
     * Convert atom contents to indented JSON string.
     *
     * @return JSON string.
     */
    public String toPretty()
    {
        try
        {
            //
            // No no no escaped forward slashes...
            //

            return atom.toString(2).replace("\\/", "/");
        }
        catch (Exception ignore)
        {
            return "";
        }
    }

    /**
     * Get JSON representation of atom.
     *
     * @return JSON object of atom.
     */
    @NonNull
    public JSONObject getAtom()
    {
        //
        // Since the content IS a JSON, we simply return it.
        //

        return atom;
    }

    /**
     * Populate atom from JSON string.
     *
     * @param jsonstr input JSON string.
     * @return true if successfull parsed.
     */
    public boolean setAtom(String jsonstr)
    {
        JSONObject newatom = fromStringJSONOBject(jsonstr);
        if (newatom == null) return false;

        atom = newatom;
        return true;
    }

    /**
     * Get atom load part as JSON object.
     *
     * @return load part as JSON object.
     */
    @NonNull
    public JSONObject getLoad()
    {
        JSONObject load = getJSONObject(atom, "load");

        if (load == null)
        {
            load = new JSONObject();
            putJSON(atom, "load", load);
        }

        return load;
    }

    /**
     * Set load part of atom from JSON object.
     *
     * @param load JSON object.
     */
    public void setLoad(JSONObject load)
    {
        putJSON(atom, "load", load);
    }

    /**
     * Set atom UUID as byte array.
     *
     * @param uuid byte array.
     */
    public void setUUID(@NonNull byte[] uuid)
    {
        putJSONByteArray(atom, "uuid", uuid);
    }

    /**
     * Set atom UUID as base64 encoded string.
     *
     * @param uuidBase64 base64 encoded byte array.
     */
    public void setUUID(@NonNull String uuidBase64)
    {
        byte[] uuid = Base64.decode(uuidBase64, Base64.DEFAULT);
        if (uuid == null) return;

        putJSONByteArray(atom, "uuid", uuid);
    }

    /**
     * Get atom UUID as byte array.
     *
     * @return atom UUID as byte array or null.
     */
    @Nullable
    public byte[] getUUID()
    {
        return getJSONByteArray(atom, "uuid");
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
     * Set time of atom in milliseconds.
     *
     * @param time time of atom in milliseconds.
     */
    public void setTime(@NonNull Long time)
    {
        putJSON(atom, "time", time);
    }

    /**
     * Get time of atom in milliseconds.
     *
     * @return time of atom in milliseconds or null.
     */
    @Nullable
    public Long getTime()
    {
        return getJSONLong(atom, "time");
    }

    /**
     * Set atom type in reverse domain order.
     *
     * @param type atom type in reverse domain order.
     */
    public void setType(@NonNull String type)
    {
        putJSON(atom, "type", type);
    }

    /**
     * Get atom type in reverse domain order.
     *
     * @return atom type in reverse domain order or null.
     */
    @Nullable
    public String getType()
    {
        return getJSONString(atom, "type");
    }

    /**
     * Put an atom created by owner identity and shared with nobody into storage.
     *
     * @return null if the atom could be persisted or error.
     */
    @Nullable
    public Err putStorage()
    {
        return GoatomStorage.putAtom(atom);
    }

    /**
     * Put an atom created by user identity UUID and shared with owner identity into storage.

     * @param userUUID target user identity UUID.
     * @return null if the atom could be persisted or error.
     */
    public Err putStorageSharedBy(@NonNull String userUUID)
    {
        return GoatomStorage.putAtomSharedBy(userUUID, atom);
    }

    /**
     * Put an atom created by owner identity and shared with user identity UUID into storage.
     *
     * @param userUUID target user identity UUID.
     * @return null if the atom could be persisted or error.
     */
    public Err putStorageSharedWidth(@NonNull String userUUID)
    {
        return GoatomStorage.putAtomSharedWith(userUUID, atom);
    }

    /**
     * Put key value to JSON object w/o fucking an exception.
     *
     * @param json JSON object.
     * @param key  key to put.
     * @param val value to put.
     */
    static void putJSON(@NonNull JSONObject json, @NonNull String key, Object val)
    {
        try
        {
            json.put(key, val);
        }
        catch (Exception ignore)
        {
        }
    }

    void putJSONByteArray(@NonNull JSONObject json, @NonNull String key, byte[] val)
    {
        try
        {
            if (val == null)
            {
                json.remove(key);
            }
            else
            {
                json.put(key, Base64.encodeToString(val, Base64.NO_WRAP));
            }
        }
        catch (Exception ignore)
        {
        }
    }

    @Nullable
    JSONObject getJSONObject(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getJSONObject(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    Long getJSONLong(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getLong(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    String getJSONString(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return json.getString(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    byte[] getJSONByteArray(@NonNull JSONObject json, @NonNull String key)
    {
        try
        {
            return Base64.decode(json.getString(key), Base64.DEFAULT);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    /**
     * Convert JSON string to JSON object w/o fucking an exception.
     *
     * @param jsonstr JSON string object.
     * @return JSONObject or null.
     */
    @Nullable
    JSONObject fromStringJSONOBject(@NonNull String jsonstr)
    {
        try
        {
            return new JSONObject(jsonstr);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
