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

public class GoatomStorage
{
    @Nullable
    public static Err putAtom(@NonNull String userUUID, @NonNull JSONObject atom)
    {
        prepareAtom(atom);

        File atomfile = getStorageFile(userUUID, atom);

        Log.d("userUUID=%s atomfile=%s atom=%s", userUUID, atomfile, atom);

        return Json.putFileContent(atomfile, atom);
    }

    @Nullable
    public static JSONObject getAtom(@NonNull String userUUID, @NonNull String atomUUID)
    {
        return null;
    }

    @Nullable
    public static JSONArray queryAtoms(@NonNull String userUUID, @NonNull String atomType, long timeFrom, long timeTo)
    {
        return null;
    }

    private final static Object mutex = new Object();

    private static void prepareAtom(@NonNull JSONObject atom)
    {
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
    private static File getStorageDir(@NonNull String userUUID, @NonNull String dateStr, @NonNull String atomType)
    {
        if (dateStr.length() < 8)
        {
            Err.err("wrong format date=%s", dateStr);
            return null;
        }

        String datedayStr = dateStr.substring(0,8);

        String ownerUUID = Owner.getOwnerUUIDBase64();
        if (ownerUUID == null) return null;

        String ownerUUIDStr = UID.getUUIDString(ownerUUID);
        if (ownerUUIDStr == null) return null;

        String userUUIDStr = UID.getUUIDString(userUUID);
        if (userUUIDStr == null) return null;

        File appfilesdir = GorillaBase.getAppContext().getFilesDir();
        File goatomdir = new File(appfilesdir, "goatom");
        File ownerdir = new File(goatomdir, ownerUUIDStr);
        File userdir = new File(ownerdir, userUUIDStr);
        File datedir = new File(userdir, datedayStr);
        File typedir = new File(datedir, atomType);

        synchronized (mutex)
        {
            Err err = Simple.mkdirs(appfilesdir, goatomdir, ownerdir, userdir, typedir);
            if (err != null) return null;
        }

        return typedir;
    }

    @Nullable
    private static File getStorageFile(@NonNull String userUUID, @NonNull JSONObject atom)
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

        File storageDir = getStorageDir(userUUID, dateStr, atomType);
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
