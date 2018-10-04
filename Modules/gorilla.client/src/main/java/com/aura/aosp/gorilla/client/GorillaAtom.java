package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

public abstract class GorillaAtom
{
    private JSONObject atom;

    public GorillaAtom()
    {
        atom = new JSONObject();
    }

    @NonNull
    public JSONObject get()
    {
        return atom;
    }

    public void put(@NonNull JSONObject atom)
    {
        this.atom = atom;
    }

    public void setTime(@NonNull Long time)
    {
        GorillaUtils.putJSON(atom, "time", time);
    }

    @Nullable
    public Long getTime()
    {
        return GorillaUtils.getJSONLong(atom, "time");
    }

    public void setType(@NonNull String type)
    {
        GorillaUtils.putJSON(atom, "type", type);
    }

    @Nullable
    public String getType()
    {
        return GorillaUtils.getJSONString(atom, "type");
    }

    public void setUUID(@NonNull byte[] uuid)
    {
        GorillaUtils.putJSONByteArray(atom, "uuid", uuid);
    }

    @Nullable
    public byte[] getUUID()
    {
        return GorillaUtils.getJSONByteArray(atom, "uuid");
    }

    public boolean putAtom()
    {
        return GorillaClient.getInstance().putAtom(atom);
    }

    public boolean putAtomSharedBy(@NonNull String userUUID)
    {
        return GorillaClient.getInstance().putAtomSharedBy(userUUID, atom);
    }

    public boolean putAtomSharedWidth(@NonNull String userUUID)
    {
        return GorillaClient.getInstance().putAtomSharedWith(userUUID, atom);
    }
}
