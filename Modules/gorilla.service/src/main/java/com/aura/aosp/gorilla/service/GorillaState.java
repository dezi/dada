package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.goatoms.GorillaAtomState;
import com.aura.aosp.gorilla.gopoor.GopoorSuggest;

import org.json.JSONObject;

public class GorillaState
{
    private static String lastState;

    @NonNull
    private static GorillaAtomState getStateCoarse()
    {
        GorillaAtomState state = new GorillaAtomState();

        state.setMobileConnected(GorillaNetwork.isMobileConnected());
        state.setWifiConnected(GorillaNetwork.isWifiConnected());
        state.setWifiName(GorillaNetwork.getWifiName());

        GorillaLocation gl = GorillaLocation.getInstance();
        state.setLatLonCoarse(gl.getLat(), gl.getLon());

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
        GorillaAtomState state = getStateCoarse();

        GorillaLocation gl = GorillaLocation.getInstance();
        state.setLatLonFine(gl.getLat(), gl.getLon());
        state.setStateTime(System.currentTimeMillis());

        return state;
    }

    static void onStateChanged()
    {
        String thisState = getStateCoarse().toString();

        if (Simple.nequals(lastState, thisState))
        {
            GorillaAtomState realState = getState();
            realState.setType("aura.event.state");
            GoatomStorage.putAtom(realState.getAtom());

            Log.d("state=%s", realState.toPretty());

            lastState = thisState;

            Err err = GopoorSuggest.precomputeSuggestionsByState(realState.getLoad());
            if (err != null) Log.e("failed! err=%s", err.toString());
        }
    }
}
