package com.aura.aosp.gorilla.goatom;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoatomStorage
{
    @Nullable
    public static Err putAtom(String userUUID, JSONObject atom)
    {
        return null;
    }

    @Nullable
    public static JSONObject getAtom(String userUUID, String atomUUID)
    {
        return null;
    }

    @Nullable
    public static JSONArray queryAtoms(String userUUID, String atomType, long timeFrom, long timeTo)
    {
        return null;
    }
}
