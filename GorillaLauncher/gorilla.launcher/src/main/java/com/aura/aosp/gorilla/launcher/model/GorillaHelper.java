package com.aura.aosp.gorilla.launcher.model;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Json;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gorilla helper class.
 * TODO: Move to SysApp.
 */
public class GorillaHelper {

    public static final String ATOMTYPE_EVENT_STATE = "aura.event.state";
    public static final String ATOMTYPE_EVENT_ACTION = "aura.event.action";
    public static final String ATOMTYPE_EVENT_CONTEXT = "aura.event.action";
    public static final String ATOMTYPE_CHAT_MESSAGE = "aura.chat.message";
    public static final String ATOMTYPE_TEXT_DRAFT = "aura.text.draft";

    /**
     * Try to parse specific time fields from "load" segment of atom.
     *
     * @param jsonObject
     * @param state
     * @return
     */
    @Nullable
    public static Long getLoadTime(JSONObject jsonObject, String state) {

        JSONObject load = Json.getObject(jsonObject, "load");
        if (load == null) return null;

        JSONObject states = Json.getObject(load, state);
        if (states == null) return null;

        Iterator<String> keysIterator = states.keys();

        if (keysIterator.hasNext()) {
            String key = keysIterator.next();
            return Json.getLong(states, key);
        }

        return null;
    }
}
