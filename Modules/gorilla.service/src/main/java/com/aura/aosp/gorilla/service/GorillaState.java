package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.goatoms.GorillaAtomState;
import com.aura.aosp.gorilla.gopoor.GopoorSuggest;

import org.json.JSONObject;

public class GorillaState
{
    private static String lastState;

    @NonNull
    private static GorillaAtomState getStateTimeless()
    {
        GorillaAtomState state = new GorillaAtomState();

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
    public static GorillaAtomState getState()
    {
        GorillaAtomState state = getStateTimeless();

        state.setStateTime(System.currentTimeMillis());

        return state;
    }

    @NonNull
    public static JSONObject getStateAsJsonObject()
    {
        GorillaAtomState state = getStateTimeless();

        state.setTime(System.currentTimeMillis());

        return state.getLoad();
    }

    static void onStateChanged()
    {
        String thisState = getStateTimeless().toString();

        if (Simple.nequals(lastState, thisState))
        {
            GorillaAtomState realState = getState();
            realState.setType("aura.event.state");
            GoatomStorage.putAtom(realState.getAtom());

            lastState = thisState;

            GopoorSuggest.precomputeSuggestionsByState(realState.getLoad());

            Log.d("state=%s", realState.toPretty());
        }
    }
}
