package com.aura.aosp.gorilla.gopmai;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.service.GorillaState;

import org.json.JSONObject;

public class GopmaiRegister
{
    @Nullable
    public static Err registerActionEvent(String actionDomain, String subAction)
    {
        JSONObject load = new JSONObject();

        Json.put(load, "domain", actionDomain);
        Json.put(load, "action", subAction);
        Json.put(load, "state", GorillaState.getState());

        JSONObject atom = new JSONObject();

        Json.put(atom, "type", "aura.event.action");
        Json.put(atom, "load", load);

        return GoatomStorage.putAtom(atom);
    }
}
