package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.gopoor.GopoorSuggest;

import org.json.JSONObject;

public class GorillaState
{
    private static String lastState;

    @NonNull
    private static com.aura.aosp.gorilla.goatoms.GorillaState getStateTimeless()
    {
        com.aura.aosp.gorilla.goatoms.GorillaState state = new com.aura.aosp.gorilla.goatoms.GorillaState();

        state.setMobileConnected(GorillaNetwork.isMobileConnected());
        state.setWifiConnected(GorillaNetwork.isWifiConnected());
        state.setWifiName(GorillaNetwork.getWifiName());

        GorillaLocation gl = GorillaLocation.getInstance();
        state.setLatLon(gl.getLat(), gl.getLon());

        Identity identity = Owner.getOwnerIdentity();

        if (identity != null)
        {
            state.setDeviceUUIDBase64(identity.getDeviceUUIDBase64());
        }

        return state;
    }

    @NonNull
    public static JSONObject getState()
    {
        com.aura.aosp.gorilla.goatoms.GorillaState state = getStateTimeless();

        state.setTime(System.currentTimeMillis());

        return state.getAtom();
    }

    static void onStateChanged()
    {
        String thisState = getStateTimeless().toString();

        if (Simple.nequals(lastState, thisState))
        {
            JSONObject realState = getState();

            Log.d("state=%s", realState.toString());

            JSONObject atom = new JSONObject();

            Json.put(atom,"type", "aura.event.state");
            Json.put(atom,"load", realState);

            Log.d("############################ event=%s", Json.toPretty(atom));

            GoatomStorage.putAtom(atom);

            lastState = thisState;

            GopoorSuggest.precomputeSuggestionsByState(realState);
        }
    }
}
