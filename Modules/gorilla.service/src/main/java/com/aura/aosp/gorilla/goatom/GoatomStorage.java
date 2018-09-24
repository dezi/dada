package com.aura.aosp.gorilla.goatom;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.gorilla.service.GorillaBase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

public class GoatomStorage
{
    @Nullable
    public static Err putAtom(@NonNull JSONObject atom)
    {
        prepareAtom(atom);

        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        File atomfile = getStorageFile(ownerUUID, ownerUUID, atom);

        Log.d("ownerUUID=%s userUUID=%s atomfile=%s atom=%s", ownerUUID, ownerUUID, atomfile, atom);

        return Json.putFileContent(atomfile, atom);
    }

    @Nullable
    public static Err putAtomSharedBy(@NonNull String userUUID, @NonNull JSONObject atom)
    {
        prepareAtom(atom);

        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        File atomfile = getStorageFile(userUUID, ownerUUID, atom);

        Log.d("ownerUUID=%s userUUID=%s atomfile=%s atom=%s", userUUID, ownerUUID, atomfile, atom);

        return Json.putFileContent(atomfile, atom);
    }

    @Nullable
    public static Err putAtomSharedWith(@NonNull String userUUID, @NonNull JSONObject atom)
    {
        prepareAtom(atom);

        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        File atomfile = getStorageFile(ownerUUID, userUUID, atom);

        Log.d("ownerUUID=%s userUUID=%s atomfile=%s atom=%s", ownerUUID, userUUID, atomfile, atom);

        return Json.putFileContent(atomfile, atom);
    }

    @Nullable
    public static JSONObject getAtom(@NonNull String userUUID, @NonNull String atomUUID)
    {
        return null;
    }

    @Nullable
    public static JSONArray queryAtoms(@NonNull String atomType, long timeFrom, long timeTo)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return queryAtoms(ownerUUID, ownerUUID, atomType, timeFrom, timeTo);
    }

    @Nullable
    public static JSONArray queryAtomsSharedBy(@NonNull String userUUID, @NonNull String atomType, long timeFrom, long timeTo)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return queryAtoms(userUUID, ownerUUID, atomType, timeFrom, timeTo);
    }

    @Nullable
    public static JSONArray queryAtomsSharedWith(@NonNull String userUUID, @NonNull String atomType, long timeFrom, long timeTo)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return queryAtoms(ownerUUID, userUUID, atomType, timeFrom, timeTo);
    }

    @Nullable
    private static JSONArray queryAtoms(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomType, long timeFrom, long timeTo)
    {
        File typeDir = getStorageDir(ownerUUID, userUUID, atomType);
        if (typeDir == null) return null;

        JSONArray results = new JSONArray();

        File[] dateDirs = typeDir.listFiles();

        String dateFromStr = (timeFrom != 0) ? Dates.getUniversalDate(timeFrom) : null;
        String dateToStr = (timeFrom != 0) ? Dates.getUniversalDate(timeTo) : null;

        for (File dateDir : dateDirs)
        {
            String dateDirName = dateDir.getName();

            if ((dateFromStr != null) && (dateFromStr.compareTo(dateDirName) < 0))
            {
                continue;
            }

            if ((dateToStr != null) && (dateToStr.compareTo(dateDirName) > 0))
            {
                continue;
            }

            Log.d("scan dir=%s", dateDir.toString());

            File[] jsonFiles = dateDir.listFiles();

            for (File jsonFile : jsonFiles)
            {
                Log.d("read file=%s", jsonFile.toString());

                JSONObject json = Json.getFileContent(jsonFile);
                if (json == null) continue;

                Json.put(results, json);
            }
        }

        return results;
    }

    private final static Object mutex = new Object();

    private static void cleanJson(@NonNull JSONObject json)
    {
        Iterator<String> keys = json.keys();

        while (keys.hasNext())
        {
            String key = keys.next();

            if (key.endsWith("_"))
            {
                Json.remove(json, key);
                continue;
            }

            Object obj = Json.get(json, key);

            if (obj instanceof JSONObject)
            {
                cleanJson((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                cleanJson((JSONArray) obj);
            }
        }
    }

    private static void cleanJson(@NonNull JSONArray json)
    {
        for (int inx = 0; inx < json.length(); inx++)
        {
            Object obj = Json.get(json, inx);

            if (obj instanceof JSONObject)
            {
                cleanJson((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                cleanJson((JSONArray) obj);
            }
        }
    }

    private static void prepareAtom(@NonNull JSONObject atom)
    {
        cleanJson(atom);

        if (!Json.has(atom, "uuid"))
        {
            Json.put(atom, "uuid", UID.randomUUIDBase64());
        }

        if (!Json.has(atom, "time"))
        {
            Json.put(atom, "time", System.currentTimeMillis());
        }

        if (!Json.has(atom, "type"))
        {
            Json.put(atom, "type", "aura.unknown");
        }
    }

    @Nullable
    private static File getStorageDir(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomType)
    {
        String ownerUUIDStr = UID.getUUIDString(ownerUUID);
        if (ownerUUIDStr == null) return null;

        String userUUIDStr = UID.getUUIDString(userUUID);
        if (userUUIDStr == null) return null;

        File appfilesdir = GorillaBase.getAppContext().getFilesDir();
        File goatomdir = new File(appfilesdir, "goatom");
        File ownerdir = new File(goatomdir, ownerUUIDStr);
        File userdir = new File(ownerdir, userUUIDStr);
        File typedir = new File(userdir, atomType);

        synchronized (mutex)
        {
            Err err = Simple.mkdirs(appfilesdir, goatomdir, ownerdir, userdir, typedir);
            if (err != null) return null;
        }

        return typedir;
    }

    @Nullable
    private static File getStorageDir(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomType, @NonNull String dateStr)
    {
        File typedir = getStorageDir(ownerUUID, userUUID, atomType);
        if (typedir == null) return null;

        if (dateStr.length() < 8)
        {
            Err.err("wrong format date=%s", dateStr);
            return null;
        }

        String datedayStr = dateStr.substring(0,8);
        File datedir = new File(typedir, datedayStr);

        synchronized (mutex)
        {
            Err err = Simple.mkdirs(datedir);
            if (err != null) return null;
        }

        return datedir;
    }

    @Nullable
    private static File getStorageFile(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull JSONObject atom)
    {
        Long time = Json.getLong(atom, "time");
        if (time == null)
        {
            Err.errp("missing time atom=%s", atom.toString());
            return null;
        }

        String dateStr = Dates.getUniversalDateAndTimeMillis(time);

        String atomType = Json.getString(atom, "type");
        if (atomType == null)
        {
            Err.errp("Type missing atom=%s", atom.toString());
            return null;
        }

        File storageDir = getStorageDir(ownerUUID, userUUID, atomType, dateStr);
        if (storageDir == null) return null;

        String atomUUID = Json.getString(atom, "uuid");
        if (atomUUID == null)
        {
            Err.errp("missing uuid atom=%s", atom.toString());
            return null;
        }

        String timeStr = dateStr.substring(8);

        String atomUUIDStr = UID.getUUIDString(atomUUID);
        if (atomUUIDStr == null) return null;

        String atomname = timeStr + "." + atomUUIDStr + ".json";
        return new File(storageDir, atomname);
    }
}
