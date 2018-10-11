package com.aura.aosp.gorilla.gopoor;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.service.GorillaState;
import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.goatoms.GorillaAtomEvent;

public class GopoorRegister
{
    @Nullable
    public static Err registerActionEvent(String actionDomain)
    {
        GorillaAtomEvent event = new GorillaAtomEvent();

        event.setType("aura.event.action");
        event.setState(GorillaState.getState());
        event.setDomain(actionDomain);

        Log.d("event=%s", event.toString());

        Err err = GoatomStorage.putAtom(event.getAtom());
        if (err != null) return err;

        return GopoorSuggest.precomputeSuggestionsByEvent(event);
    }

    @Nullable
    public static Err registerActionEvent(String actionDomain, String subAction)
    {
        GorillaAtomEvent event = new GorillaAtomEvent();

        event.setType("aura.event.action");
        event.setState(GorillaState.getState());
        event.setDomain(actionDomain);
        event.setAction(subAction);

        Log.d("event=%s", event.toString());

        Err err = GoatomStorage.putAtom(event.getAtom());
        if (err != null) return err;

        return GopoorSuggest.precomputeSuggestionsByEvent(event);
    }

    @Nullable
    public static Err registerContextEvent(String actionDomain, String subContext, String subAction)
    {
        GorillaAtomEvent event = new GorillaAtomEvent();

        event.setType("aura.event.context");
        event.setState(GorillaState.getState());
        event.setDomain(actionDomain);
        event.setAction(subAction);
        event.setContext(subContext);

        Log.d("event=%s", event.toString());

        Err err = GoatomStorage.putAtom(event.getAtom());
        if (err != null) return err;

        return GopoorSuggest.precomputeSuggestionsByEvent(event);
   }
}
