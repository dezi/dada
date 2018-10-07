/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.gorilla.client;

import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.util.Log;

/**
 * The class {@code GorillaService} ist just a stub service
 * implementation to be called by the Gorilla system app.
 *
 * @author Dennis Zierahn
 */
public class GorillaService extends Service
{
    /**
     * All purpose log tag.
     */
    private static final String LOGTAG = GorillaService.class.getSimpleName();

    /**
     * Called when the service is used for the first time.
     *
     * @param intent service bind intent.
     *
     * @return new instance of {@code GorillaClientService}.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        if (intent == null) return null;

        Log.d(LOGTAG, "onBind: intent=" + intent.toString());

        return new GorillaClientService();
    }
}
