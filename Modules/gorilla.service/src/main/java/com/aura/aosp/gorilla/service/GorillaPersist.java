package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;

import org.json.JSONObject;

import java.io.File;

public class GorillaPersist
{
    private final static Object mutex = new Object();

    @Nullable
    static Err persistTicketForLocalClientApp(String apkname, Long timeStamp, byte[] keyUUID, byte[] nonceUUID, JSONObject json)
    {
        if ((apkname == null) || (timeStamp == null) || (nonceUUID == null) || (keyUUID == null) || (json == null))
        {
            return Err.errp();
        }

        File apkdir = getLocalClientAppDir(apkname, true);
        if (apkdir == null) return Err.getLastErr();

        String saveFilename = Dates.getUniversalDateAndTimeMillis(timeStamp)
                + "." + UID.convertUUIDToString(keyUUID)
                + "." + UID.convertUUIDToString(nonceUUID)
                + ".json";

        File saveFile = new File(apkdir, saveFilename);

        Log.d("saveFile=%s", saveFile, json.toString());
        Log.d("saveData=%s", json.toString());

        return Json.putFileContent(saveFile, json);
    }

    @Nullable
    static JSONObject unpersistNextTicketForLocalClientApp(String apkname)
    {
        File apkdir = getLocalClientAppDir(apkname, false);
        if (apkdir == null) return null;

        synchronized (mutex)
        {
            File[] files = apkdir.listFiles();

            for (File file : files)
            {
                JSONObject json = unpersistFile(file);
                if (json != null) return json;
            }
        }

        return null;
    }

    @Nullable
    private static File getLocalClientAppDir(String apkname, boolean create)
    {
        File appfilesdir = GorillaBase.getAppContext().getFilesDir();
        File gorilladir = new File(appfilesdir, "gorilla");
        File ticketdir = new File(gorilladir, "ticket");
        File localdir = new File(ticketdir, "local");
        File apkdir = new File(localdir, apkname);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(gorilladir, ticketdir, localdir, apkdir);
                if (err != null) return null;
            }
        }

        return apkdir;
    }

    @Nullable
    private static JSONObject unpersistFile(File persisted)
    {
        JSONObject json = Json.getFileContent(persisted);

        if (! persisted.delete())
        {
            Err.errp("cannot remove file=%s", persisted.toString());

            return null;
        }

        return json;
    }
}
