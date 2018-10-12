package com.aura.aosp.gorilla.goatom;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.os.Environment;

import com.aura.aosp.aura.common.crypter.GZP;
import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Owner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

public class GoatomStorage
{
    @Nullable
    public static Err putAtom(@NonNull JSONObject atom)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        return putAtom(ownerUUID, ownerUUID, atom);
    }

    @Nullable
    public static Err putAtomSharedBy(@NonNull String userUUID, @NonNull JSONObject atom)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        return putAtom(userUUID, ownerUUID, atom);
    }

    @Nullable
    public static Err putAtomSharedWith(@NonNull String userUUID, @NonNull JSONObject atom)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return Err.getLastErr();

        return putAtom(ownerUUID, userUUID, atom);
    }

    @Nullable
    private static Err putAtom(@NonNull String ownerUUID, @NonNull String sharedUUID, @NonNull JSONObject atom)
    {
        prepareAtom(atom);

        File atomfile = getStorageFile(ownerUUID, sharedUUID, atom);

        Log.d("ownerUUID=%s sharedUUID=%s atomfile=%s atom=%s", ownerUUID, sharedUUID, atomfile, atom);

        Err errAtom = Json.putFileContent(atomfile, atom);
        Err errSync = putSync(ownerUUID, sharedUUID, atom);

        return (errAtom != null) ? errAtom : errSync;
    }

    private static Err putSync(@NonNull String ownerUUID, @NonNull String sharedUUID, @NonNull JSONObject atom)
    {
        File appfilesdir = Environment.getExternalStorageDirectory();
        File gosyncdir = new File(appfilesdir, "gosync");

        synchronized (mutex)
        {
            Err err = Simple.mkdirs(appfilesdir, gosyncdir);
            if (err != null) return err;
        }

        String syncUUID = UID.randomUUIDString();
        String dateStr = Dates.getUniversalDateAndTimeMillis(System.currentTimeMillis());
        if (dateStr == null) return Err.getLastErr();

        JSONObject sync = new JSONObject();

        Json.put(sync, "ownerUUID", ownerUUID);
        Json.put(sync, "sharedUUID", sharedUUID);
        Json.put(sync, "syncAtom", atom);

        String syncStr = Json.toPretty(sync);
        if (syncStr == null) return Err.getLastErr();

        byte[] syncBytes = GZP.enGzip(syncStr.getBytes());
        File syncFile = new File(gosyncdir, dateStr + "." + syncUUID + ".json.gz");

        return Simple.putFileBytes(syncFile, syncBytes);
    }

    @Nullable
    public static JSONObject getAtom(@NonNull String atomUUID)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return getAtom(ownerUUID, ownerUUID, atomUUID);
    }

    @Nullable
    public static JSONObject getAtomSharedBy(@NonNull String userUUID, @NonNull String atomUUID)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return getAtom(userUUID, ownerUUID, atomUUID);
    }

    @Nullable
    public static JSONObject getAtomSharedWith(@NonNull String userUUID, @NonNull String atomUUID)
    {
        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        return getAtom(ownerUUID, userUUID, atomUUID);
    }

    @Nullable
    private static JSONObject getAtom(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomUUID)
    {
        String atomUUIDStr = UID.convertUUIDToString(atomUUID);
        if (atomUUIDStr == null) return null;

        String atomSuffix = atomUUIDStr + ".json";

        File userdir = getStorageDir(ownerUUID, userUUID, false);
        if (userdir == null) return null;

        File[] typeDirs = userdir.listFiles();
        if (typeDirs == null) return null;

        long count = 0;
        long start = System.currentTimeMillis();
        Log.d("start:...");

        for (File typeDir : typeDirs)
        {
            File[] dateDirs = typeDir.listFiles();
            if (dateDirs == null) continue;

            for (File dateDir : dateDirs)
            {
                File[] jsonFiles = dateDir.listFiles();
                if (jsonFiles == null) continue;

                for (File jsonFile : jsonFiles)
                {
                    if (jsonFile.getName().endsWith(atomSuffix))
                    {
                        return Json.getFileContent(jsonFile);
                    }

                    count++;
                }
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        Log.d("count=%d elapsed=%d", count, elapsed);

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
        File typeDir = getStorageDir(ownerUUID, userUUID, atomType, false);
        if (typeDir == null) return null;

        Log.d("###### typeDir=%s", typeDir.toString());

        JSONArray results = new JSONArray();

        File[] dateDirs = typeDir.listFiles();
        if (dateDirs == null) return null;

        String dateFromStr = (timeFrom != 0) ? Dates.getUniversalDate(timeFrom) : null;
        String dateToStr = (timeFrom != 0) ? Dates.getUniversalDate(timeTo) : null;

        for (File dateDir : dateDirs)
        {
            String dateDirName = dateDir.getName();

            if ((dateFromStr != null) && (dateFromStr.compareTo(dateDirName) > 0))
            {
                continue;
            }

            if ((dateToStr != null) && (dateToStr.compareTo(dateDirName) < 0))
            {
                continue;
            }

            Log.d("scan dir=%s", dateDir.toString());

            File[] jsonFiles = dateDir.listFiles();
            if (jsonFiles == null) return null;

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

    private static void prepareAtom(@NonNull JSONObject atom)
    {
        Json.cleanJsonTempKeys(atom);

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

        Json.sortJsonKeys(atom);
    }

    @Nullable
    private static File getStorageDir(@NonNull String ownerUUID, @NonNull String userUUID, boolean create)
    {
        String ownerUUIDStr = UID.convertUUIDToString(ownerUUID);
        if (ownerUUIDStr == null) return null;

        String userUUIDStr = UID.convertUUIDToString(userUUID);
        if (userUUIDStr == null) return null;

        File appfilesdir = Environment.getExternalStorageDirectory();
        File goatomdir = new File(appfilesdir, "goatom");
        File ownerdir = new File(goatomdir, ownerUUIDStr);
        File userdir = new File(ownerdir, userUUIDStr);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(appfilesdir, goatomdir, ownerdir, userdir);
                if (err != null) return null;
            }
        }

        return userdir;
    }

    @Nullable
    private static File getStorageDir(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomType, boolean create)
    {
        File userdir = getStorageDir(ownerUUID, userUUID, create);
        if (userdir == null) return null;

        File typedir = new File(userdir, atomType);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(typedir);
                if (err != null) return null;
            }
        }

        return typedir;
    }

    @Nullable
    private static File getStorageDir(@NonNull String ownerUUID, @NonNull String userUUID, @NonNull String atomType, @NonNull String dateStr, boolean create)
    {
        File typedir = getStorageDir(ownerUUID, userUUID, atomType, create);
        if (typedir == null) return null;

        if (dateStr.length() < 8)
        {
            Err.err("wrong format date=%s", dateStr);
            return null;
        }

        String datedayStr = dateStr.substring(0,8);
        File datedir = new File(typedir, datedayStr);

        if (create)
        {
            synchronized (mutex)
            {
                Err err = Simple.mkdirs(datedir);
                if (err != null) return null;
            }
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
        if (dateStr == null) return null;

        String atomType = Json.getString(atom, "type");
        if (atomType == null)
        {
            Err.errp("Type missing atom=%s", atom.toString());
            return null;
        }

        File storageDir = getStorageDir(ownerUUID, userUUID, atomType, dateStr, true);
        if (storageDir == null) return null;

        String atomUUID = Json.getString(atom, "uuid");
        if (atomUUID == null)
        {
            Err.errp("missing uuid atom=%s", atom.toString());
            return null;
        }

        String timeStr = dateStr.substring(8);

        String atomUUIDStr = UID.convertUUIDToString(atomUUID);
        if (atomUUIDStr == null) return null;

        String atomname = timeStr + "." + atomUUIDStr + ".json";
        return new File(storageDir, atomname);
    }
}
