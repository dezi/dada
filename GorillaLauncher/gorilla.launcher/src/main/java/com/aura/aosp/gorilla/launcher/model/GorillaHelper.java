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

    public static final Map<String, String> atomTypes;

    static {
        atomTypes = new HashMap<String, String>();
        atomTypes.put("state", "aura.event.state");
        atomTypes.put("action", "aura.event.action");
        atomTypes.put("context", "aura.event.context");
        atomTypes.put("chatMessage", "aura.chat.message");
        atomTypes.put("draft", "aura.draft");
    }

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
