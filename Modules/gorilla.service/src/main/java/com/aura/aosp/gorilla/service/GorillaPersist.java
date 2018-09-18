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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GorillaPersist
{
    private final static Object mutex = new Object();
    private final static List<File> persisteds = new ArrayList<>();

    @Nullable
    static Err persistFile(String subdirname, Long timeStamp, byte[] keyUUID, byte[] nonceUUID, JSONObject json)
    {
        if ((subdirname == null) || (timeStamp == null) || (nonceUUID == null) || (keyUUID == null) || (json == null))
        {
            return Err.errp();
        }

        File filesdir = GorillaBase.getAppContext().getFilesDir();
        File gorilladir = new File(filesdir, "gorilla");
        File subdir = new File(gorilladir, subdirname);

        Err err = mkdirs(gorilladir, subdir);
        if (err != null) return err;

        String saveFilename = Dates.getUniversalDateAndTimeMillis(timeStamp)
                + "." + UID.getUUIDString(keyUUID)
                + "." + UID.getUUIDString(nonceUUID)
                + ".json";

        File saveFile = new File(subdir, saveFilename);

        Log.d("saveFile=%s", saveFile, json.toString());
        Log.d("saveData=%s", json.toString());

        return Json.putFileContent(saveFile, json);
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

    @Nullable
    static JSONObject unpersistNext(String subdirname)
    {
        while (true)
        {
            synchronized (mutex)
            {
                if (persisteds.size() > 0)
                {
                    File persisted = persisteds.remove(0);
                    JSONObject json = unpersistFile(persisted);
                    if (json == null) continue;

                    return json;
                }
            }

            getPersistedFiles("gorilla");
            if (persisteds.size() == 0) return null;
        }
    }

    private static void getPersistedFiles(String subdirname)
    {
        File filesdir = GorillaBase.getAppContext().getFilesDir();
        File gorilladir = new File(filesdir, "gorilla");
        File subdir = new File(gorilladir, subdirname);

        File[] files = subdir.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".json");
            }
        });

        synchronized (mutex)
        {
            persisteds.addAll(Arrays.asList(files));
        }
    }

    @Nullable
    private static Err mkdirs(File... dirs)
    {
        synchronized (mutex)
        {
            for (File dir : dirs)
            {
                if (! (dir.exists() || dir.mkdirs()))
                {
                    return Err.errp("cannot create directory=%s", dir.toString());
                }
            }
        }

        return null;
    }
}
