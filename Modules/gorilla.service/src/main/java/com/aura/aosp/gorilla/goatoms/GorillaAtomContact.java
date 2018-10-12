package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public class GorillaAtomContact extends GorillaAtom
{
    /**
     * Create empty contact atom.
     */
    public GorillaAtomContact()
    {
        super();
    }

    /**
     * Create contact atom from JSONObject.
     *
     * @param contact JSON contact atom object.
     */
    public GorillaAtomContact(JSONObject contact)
    {
        super(contact);
    }

    public void setUserUUIDBase64(String userUUID)
    {
        putJSON(getLoad(), "userUUID", userUUID);
    }

    @Nullable
    public String getUserUUIDBase64()
    {
        return getJSONString(getLoad(), "userUUID");
    }

    public void setNick(String nick)
    {
        putJSON(getLoad(), "nick", nick);
    }

    @Nullable
    public String getNick()
    {
        return getJSONString(getLoad(), "nick");
    }

    public void setFull(String full)
    {
        putJSON(getLoad(), "full", full);
    }

    @Nullable
    public String getFull()
    {
        return getJSONString(getLoad(), "full");
    }

    public void setCountry(String country)
    {
        putJSON(getLoad(), "country", country);
    }

    @Nullable
    public String getCountry()
    {
        return getJSONString(getLoad(), "country");
    }
}
