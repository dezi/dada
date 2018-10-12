package com.aura.aosp.gorilla.gopoor;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.service.GorillaState;
import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.goatoms.GorillaAtomEvent;

public class GopoorRegister
{
    /**
     * Register event on an action domain.
     *
     * @param actionDomain action domain in reversed order.
     * @return error or null.
     */
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

    /**
     * Register event on a sub action in an action domain.
     *
     * @param actionDomain action domain in reversed order.
     * @param subAction sub action executed.
     * @return error or null.
     */
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

    /**
     * Register event on a sub action in a sub context of an action domain.
     *
     * @param actionDomain action domain in reversed order.
     * @param subContext sub context of action domain.
     * @param subAction sub action executed.
     * @return error or null.
     */
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
