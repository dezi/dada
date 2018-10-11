package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code GorillaAtomAction} extends to basic
 * {@code GorillaAtom} by status values.
 *
 * @author Dennis Zierahn
 */
public class GorillaAtomAction extends GorillaAtom
{
    /**
     * Create empty state atom.
     */
    public GorillaAtomAction()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param status JSON status atom object.
     */
    public GorillaAtomAction(JSONObject status)
    {
        super(status);
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

    public String getSerializedAction()
    {
        JSONObject serialized = new JSONObject();

        putJSON(serialized, "domain", getDomain());
        putJSON(serialized, "context", getContext());
        putJSON(serialized, "action", getAction());

        return serialized.toString();
    }

    public void setSerializedAction(String serialized)
    {
        JSONObject deserialized = fromStringJSONOBject(serialized);
        if (deserialized == null) return;

        setDomain(getJSONString(deserialized, "domain"));
        setContext(getJSONString(deserialized, "context"));
        setAction(getJSONString(deserialized, "action"));
    }
}
