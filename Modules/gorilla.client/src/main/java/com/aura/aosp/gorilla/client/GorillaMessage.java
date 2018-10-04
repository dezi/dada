package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.Iterator;

public class GorillaMessage extends GorillaAtom
{
    private JSONObject load;

    public GorillaMessage()
    {
        super();

        load = new JSONObject();

        GorillaUtils.putJSON(super.get(), "load", load);
    }

    public GorillaMessage(JSONObject message)
    {
        super(message);

        load = GorillaUtils.getJSONObject(message, "load");
    }

    public void setMessage(@NonNull String message)
    {
        GorillaUtils.putJSON(load, "message", message);
    }

    @Nullable
    public String getMessage()
    {
        return GorillaUtils.getJSONString(load, "message");
    }

    public void setStatusTime(@NonNull String status, @NonNull String deviceUUID, @NonNull Long time)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(load, status);

        if (statusJson == null)
        {
            statusJson = new JSONObject();
            GorillaUtils.putJSON(load, status, statusJson);
        }

        GorillaUtils.putJSON(statusJson, deviceUUID, time);
    }

    @Nullable
    public Long getStatusTime(@NonNull String status)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(load, status);
        if (statusJson == null) return null;

        Iterator<String> keys = statusJson.keys();

        if (keys.hasNext())
        {
            return GorillaUtils.getJSONLong(statusJson, keys.next());
        }

        return null;
    }

    @Nullable
    public Long getStatusTime(@NonNull String status, @NonNull String deviceUUID)
    {
        JSONObject statusJson = GorillaUtils.getJSONObject(load, status);
        if (statusJson == null) return null;

        return GorillaUtils.getJSONLong(statusJson, deviceUUID);
    }
}
