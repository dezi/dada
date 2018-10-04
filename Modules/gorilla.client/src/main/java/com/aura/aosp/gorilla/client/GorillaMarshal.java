package com.aura.aosp.gorilla.client;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface GorillaMarshal
{
    @Nullable
    JSONObject marshal();

    boolean unmarhal(@NonNull JSONObject json);
}
