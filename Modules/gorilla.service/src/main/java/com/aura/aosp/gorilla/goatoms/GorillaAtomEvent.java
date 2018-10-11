package com.aura.aosp.gorilla.goatoms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * The class {@code GorillaAtomEvent} extends to basic
 * {@code GorillaAtom} by status values.
 *
 * @author Dennis Zierahn
 */
public class GorillaAtomEvent extends GorillaAtomAction
{
    /**
     * Create empty state atom.
     */
    public GorillaAtomEvent()
    {
        super();
    }

    /**
     * Create payload atom from JSONObject.
     *
     * @param status JSON status atom object.
     */
    public GorillaAtomEvent(JSONObject status)
    {
        super(status);
    }

    /**
     * Set event state.
     *
     * @param state event state.
     */
    public void setState(@NonNull GorillaAtomState state)
    {
        putJSON(getLoad(), "state", state.getLoad());
    }

    /**
     * Get event state.
     *
     * @return event state or null.
     */
    @Nullable
    public GorillaAtomState getState()
    {
        JSONObject stateLoad = getJSONObject(getLoad(), "state");
        GorillaAtomState state = new GorillaAtomState();
        state.setLoad(stateLoad);

        return state;
    }
}
