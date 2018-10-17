/*
 * Copyright (C) 2018 Aura Software Inc.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 */

package com.aura.aosp.aura.common.simple;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.File;

/**
 * Exception safe, annotated and simplified
 * JSON helper methods.
 *
 * @author Dennis Zierahn
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Json
{
    /**
     * Read JSON object from file.
     *
     * @param jsonFile JSON file.
     * @return JSONObject or null on failure.
     */
    @Nullable
    public static JSONObject getFileContent(@NonNull File jsonFile)
    {
        //noinspection ConstantConditions
        if (jsonFile == null)
        {
            Err.errp();
            return null;
        }

        String jsonStr = Simple.getFileContent(jsonFile);
        if (jsonStr == null) return null;

        return fromStringObject(jsonStr);
    }

    /**
     * Write JSON object into file.
     *
     * @param jsonFile JSON file.
     * @return null or error on failure.
     */
    public static Err putFileContent(@NonNull File jsonFile, @NonNull JSONObject content)
    {
        //noinspection ConstantConditions
        if ((jsonFile == null) || (content == null))
        {
            return Err.errp();
        }

        return Simple.putFileContent(jsonFile, Json.toPretty(content));
    }

    /**
     * Create JSONOBject from JSON string.
     *
     * @param jsonStr JSON string.
     * @return JSONOBject or null on failure.
     */
    @Nullable
    public static JSONObject fromStringObject(@NonNull String jsonStr)
    {
        //noinspection ConstantConditions
        if (jsonStr == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            return new JSONObject(jsonStr);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Create JSONArray from JSON string.
     *
     * @param jsonStr JSON string.
     * @return JSONArray or null on failure.
     */
    @Nullable
    public static JSONArray fromStringArray(@NonNull String jsonStr)
    {
        //noinspection ConstantConditions
        if (jsonStr == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            return new JSONArray(jsonStr);
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Clone JSONObject.
     *
     * @param jsonObject JSONObject to clone.
     * @return cloned JSONObject or null on failure.
     */
    @Nullable
    public static JSONObject clone(@NonNull JSONObject jsonObject)
    {
        //noinspection ConstantConditions
        if (jsonObject == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            return new JSONObject(jsonObject.toString());
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Clone JSONArray.
     *
     * @param jsonArray JSONArray to clone.
     * @return cloned JSONArray or null on failure.
     */
    @Nullable
    public static JSONArray clone(@NonNull JSONArray jsonArray)
    {
        //noinspection ConstantConditions
        if (jsonArray == null)
        {
            Err.errp();
            return null;
        }

        try
        {
            return new JSONArray(jsonArray.toString());
        }
        catch (Exception ex)
        {
            Err.errp(ex);
            return null;
        }
    }

    /**
     * Remove key from JSONObject.
     *
     * @param jsonObject JSONObject to work on.
     * @param key key to remove from JSONObject.
     * @return null or error on failure.
     */
    @Nullable
    public static Err remove(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        //noinspection ConstantConditions
        if ((jsonObject == null) || (key == null))
        {
            return Err.errp();
        }

        jsonObject.remove(key);
        return null;
    }

    /**
     * Remove index from JSONArray.
     *
     * @param jsonArray JSONArray to work on.
     * @param index index to remove from JSONArray.
     * @return null or error on failure.
     */
    @Nullable
    public static Err remove(JSONArray jsonArray, int index)
    {
        //noinspection ConstantConditions
        if (jsonArray == null)
        {
            return Err.errp();
        }

        jsonArray.remove(index);
        return null;
    }

    /**
     * Compare key from withon JSONObject with given value.
     *
     * @param j1 JSONObject to take data from.
     * @param k1 key in JSONObject.
     * @param val value to compare to.
     * @return true if value matches content or both are null.
     */
    public static boolean equals(@NonNull JSONObject j1, @NonNull String k1, @Nullable String val)
    {
        //noinspection ConstantConditions
        if ((j1 == null) || (k1 == null))
        {
            //
            // Decided to return false on bogous comparisions.
            //

            return false;
        }

        String s1 = getString(j1, k1);

        return ((s1 == null) && (val == null)) || ((s1 != null) && s1.equals(val));
    }

    /**
     * Compare key from withon JSONObject with given value.
     *
     * @param j1 JSONObject to take data from.
     * @param k1 key in JSONObject.
     * @param j2 JSONObject to take data from.
     * @return true if both contents match or both are null.
     */
    public static boolean equals(@NonNull JSONObject j1, @NonNull String k1, @NonNull JSONObject j2)
    {
        //noinspection ConstantConditions
        if ((j1 == null) || (k1 == null) || (j2 == null))
        {
            //
            // Decided to return false on bogous comparisions.
            //

            return false;
        }

        Object s1 = get(j1, k1);
        Object s2 = get(j2, k1);

        return ((s1 == null) && (s2 == null)) || ((s1 != null) && s1.equals(s2));
    }

    /**
     * Copy value from one JSONObject to another.
     *
     * @param dst destination JSONObject.
     * @param dkey destination key.
     * @param src source JSONObject.
     * @param skey source key.
     * @return null on success or error.
     */
    @Nullable
    public static Err copy(@NonNull JSONObject dst, @NonNull String dkey,
                           @NonNull JSONObject src, @NonNull String skey)
    {
        //noinspection ConstantConditions
        if ((dst == null) || (dkey == null) || (src == null) || (skey == null))
        {
            return Err.errp();
        }

        //
        // Decided that if tmp is null it should remove key in target.
        //

        Object tmp = get(src, skey);
        put(dst, dkey, tmp);

        return null;
    }

    /**
     * Copy value from one JSONObject to another.
     *
     * @param dst destination JSONObject.
     * @param key key to copy.
     * @param src source JSONObject.
     * @return null on success or error.
     */
    @Nullable
    public static Err copy(@NonNull JSONObject dst, @NonNull String key, @NonNull JSONObject src)
    {
        //noinspection ConstantConditions
        if ((dst == null) || (key == null) || (src == null))
        {
            return Err.errp();
        }

        //
        // Decided that if tmp is null it should remove key in target.
        //

        Object tmp = get(src, key);
        put(dst, key, tmp);

        return null;
    }

    /**
     * Copy all values from one JSONObject to another.
     *
     * @param dst destination JSONObject.
     * @param src source JSONObject.
     * @return null on success or error.
     */
    @Nullable
    public static Err copy(@NonNull JSONObject dst, @NonNull JSONObject src)
    {
        //noinspection ConstantConditions
        if ((dst == null) || (src == null))
        {
            return Err.errp();
        }

        Iterator<String> keysIterator = src.keys();

        while (keysIterator.hasNext())
        {
            String key = keysIterator.next();

            Err err = copy(dst, key, src);
            if (err != null) return err;
        }

        return null;
    }

    /**
     * Append one JSONArray into another.
     *
     * @param destination destination JSONArray.
     * @param source source JSONArray.
     * @return null or error.
     */
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static Err append(@NonNull JSONArray destination, @NonNull JSONArray source)
    {
        //noinspection ConstantConditions
        if ((destination == null) || (source == null))
        {
            Err.errp();
            return null;
        }

        for (int inx = 0; inx < source.length(); inx++)
        {
            put(destination, get(source, inx));
        }

        return null;
    }

    /**
     * Put generic value into JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key key value.
     * @param val object value.
     * @return null or error.
     */
    @Nullable
    public static Err put(@NonNull JSONObject jsonObject, @NonNull String key, @Nullable Object val)
    {
        //noinspection ConstantConditions
        if ((jsonObject == null) || (key == null))
        {
            return Err.errp();
        }

        try
        {
            jsonObject.put(key, val);
            return null;
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
    }

    /**
     * Append generic value into JSONArray.
     *
     * @param jsonArray target JSONArray.
     * @param val object value.
     * @return null or error.
     */
    @Nullable
    public static Err put(@NonNull JSONArray jsonArray, @NonNull Object val)
    {
        //noinspection ConstantConditions
        if ((jsonArray == null) || (val == null))
        {
            return Err.errp();
        }

        try
        {
            jsonArray.put(val);
            return null;
        }
        catch (Exception ex)
        {
            return Err.errp(ex);
        }
    }

    /**
     * Check if JSONObject has key.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return true if JSONObject contains key.
     */
    public static boolean has(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        //noinspection ConstantConditions
        return (jsonObject != null) && jsonObject.has(key);
    }

    /**
     * Get generic object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Object get(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.get(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get generic object from JSONArray.
     *
     * @param jsonArray target JSONArray.
     * @param index target index.
     * @return object or null.
     */
    @Nullable
    public static Object get(@NonNull JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.get(index);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get float object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Float getFloat(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return (float) jsonObject.getDouble(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get double object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Double getDouble(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.getDouble(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get boolean object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Boolean getBoolean(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.getBoolean(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get byte object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Byte getByte(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return (byte) (jsonObject.getInt(key) & 0xff);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get short object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Short getShort(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return (short) (jsonObject.getInt(key) & 0xffff);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get int object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Integer getInt(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.getInt(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get int object from JSONArray.
     *
     * @param jsonArray target JSONArray.
     * @param index target index.
     * @return object or null.
     */
    @Nullable
    public static Integer getInt(@NonNull JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getInt(index);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get long object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Long getLong(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.getLong(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get string object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static String getString(@NonNull JSONObject jsonObject, @NonNull String key)
    {
        try
        {
            return jsonObject.getString(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get string object from JSONArray.
     *
     * @param jsonArray target JSONArray.
     * @param index target index.
     * @return object or null.
     */
    @Nullable
    public static String getString(@NonNull JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getString(index);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get generic object from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static Object getGeneric(@NonNull JSONObject jsonObject, String key)
    {
        try
        {
            return jsonObject.get(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get JSONObject from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static JSONObject getObject(@NonNull JSONObject jsonObject, String key)
    {
        try
        {
            return jsonObject.getJSONObject(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get JSONArray from JSONObject.
     *
     * @param jsonObject target JSONObject.
     * @param key target key.
     * @return object or null.
     */
    @Nullable
    public static JSONArray getArray(@NonNull JSONObject jsonObject, String key)
    {
        try
        {
            return jsonObject.getJSONArray(key);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Get JSONObject from JSONArray.
     *
     * @param jsonArray target JSONArray.
     * @param index target index.
     * @return object or null.
     */
    @Nullable
    public static JSONObject getObject(@NonNull JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getJSONObject(index);
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Pretty JSON string from JSONObject. NO Slash escapes!
     *
     * @param jsonObject target JSONObject.
     * @return pretty JSON string or null.
     */
    @Nullable
    public static String toPretty(@NonNull JSONObject jsonObject)
    {
        try
        {
            return defuck(jsonObject.toString(2));
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Pretty JSON string from JSONArray. NO Slash escapes!
     *
     * @param jsonArray target JSONArray.
     * @return pretty JSON string or null.
     */
    @Nullable
    public static String toPretty(@NonNull JSONArray jsonArray)
    {
        try
        {
            return defuck(jsonArray.toString(2));
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Compact JSON string from JSONObject. NO Slash escapes!
     *
     * @param jsonObject target JSONObject.
     * @return Normal JSON string or null.
     */
    @Nullable
    public static String toString(@NonNull JSONObject jsonObject)
    {
        try
        {
            return defuck(jsonObject.toString());
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Compact JSON string from JSONArray. NO Slash escapes!
     *
     * @param jsonArray target JSONArray.
     * @return pretty JSON string or null.
     */
    @Nullable
    public static String toString(@NonNull JSONArray jsonArray)
    {
        try
        {
            return defuck(jsonArray.toString());
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Convert JSONArray to set.
     *
     * @param jsonArray target JSONArray.
     * @return string set or null.
     */
    @Nullable
    public static Set<String> toSet(JSONArray jsonArray)
    {
        try
        {
            Set<String> set = new HashSet<>();

            for (int inx = 0; inx < jsonArray.length(); inx++)
            {
                set.add(jsonArray.getString(inx));
            }

            return set;
        }
        catch (Exception ex)
        {
            Err.err(ex);
            return null;
        }
    }

    /**
     * Defuck JSON string.
     *
     * @param jsonString target JSON string.
     * @return defucked JSON string.
     */
    @Nullable
    public static String defuck(String jsonString)
    {
        //
        // I hate slash escaping.
        //

        return (jsonString == null) ? null : jsonString.replace("\\/", "/");
    }

    public static JSONArray sort(JSONArray array, String field, boolean descending)
    {
        final String sort = field;
        final boolean desc = descending;

        class comparedat implements Comparator<JSONObject>
        {
            public int compare(JSONObject a, JSONObject b)
            {
                String astr = desc ? getString(b, sort) : getString(a, sort);
                String bstr = desc ? getString(a, sort) : getString(b, sort);

                return Simple.compareTo(astr, bstr);
            }
        }

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int inx = 0; inx < array.length(); inx++)
        {
            jsonValues.add(getObject(array, inx));
        }

        Collections.sort(jsonValues, new comparedat());

        return new JSONArray(jsonValues);
    }

    public static JSONArray sortNumber(JSONArray array, String field, boolean descending)
    {
        final String sort = field;
        final boolean desc = descending;

        class comparedat implements Comparator<JSONObject>
        {
            public int compare(JSONObject a, JSONObject b)
            {
                Long aval = desc ? getLong(b, sort) : getLong(a, sort);
                Long bval = desc ? getLong(a, sort) : getLong(b, sort);

                return Simple.compareTo(aval, bval);
            }
        }

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int inx = 0; inx < array.length(); inx++)
        {
            jsonValues.add(getObject(array, inx));
        }

        Collections.sort(jsonValues, new comparedat());

        return new JSONArray(jsonValues);
    }

    public static JSONArray sortDouble(JSONArray array, String field, boolean descending)
    {
        final String sort = field;
        final boolean desc = descending;

        class comparedat implements Comparator<JSONObject>
        {
            public int compare(JSONObject a, JSONObject b)
            {
                Double aval = desc ? getDouble(b, sort) : getDouble(a, sort);
                Double bval = desc ? getDouble(a, sort) : getDouble(b, sort);

                return Simple.compareTo(aval, bval);
            }
        }

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int inx = 0; inx < array.length(); inx++)
        {
            jsonValues.add(getObject(array, inx));
        }

        Collections.sort(jsonValues, new comparedat());

        return new JSONArray(jsonValues);
    }


    public static void sortJsonKeys(@NonNull JSONObject json)
    {
        //
        // Obtain array list with keys in object.
        //

        Iterator<String> keys = json.keys();
        List<String> sortedkeys = new ArrayList<>();

        while (keys.hasNext())
        {
            sortedkeys.add(keys.next());
        }

        //
        // Sort keys lexicographically.
        //

        class comparedat implements Comparator<String>
        {
            public int compare(String a, String b)
            {
                return a.compareTo(b);
            }
        }

        Collections.sort(sortedkeys, new comparedat());

        //
        // Reput keys in sorted order.
        //

        for (String key : sortedkeys)
        {
            Object obj = Json.get(json, key);
            Json.remove(json, key);
            Json.put(json, key, obj);

            if (obj instanceof JSONObject)
            {
                sortJsonKeys((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                sortJsonKeys((JSONArray) obj);
            }
        }
    }

    public static void sortJsonKeys(@NonNull JSONArray json)
    {
        for (int inx = 0; inx < json.length(); inx++)
        {
            Object obj = Json.get(json, inx);

            if (obj instanceof JSONObject)
            {
                sortJsonKeys((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                sortJsonKeys((JSONArray) obj);
            }
        }
    }

    public static void cleanJsonTempKeys(@NonNull JSONObject json)
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
                cleanJsonTempKeys((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                cleanJsonTempKeys((JSONArray) obj);
            }
        }
    }

    public static void cleanJsonTempKeys(@NonNull JSONArray json)
    {
        for (int inx = 0; inx < json.length(); inx++)
        {
            Object obj = Json.get(json, inx);

            if (obj instanceof JSONObject)
            {
                cleanJsonTempKeys((JSONObject) obj);
            }

            if (obj instanceof JSONArray)
            {
                cleanJsonTempKeys((JSONArray) obj);
            }
        }
    }

    public interface JsonMarshaller
    {
        JSONObject marshalJSON();

        Err unmarshalJSON(JSONObject json);
    }

    @Nullable
    public static JSONObject marshalJSON(Object object)
    {
        JSONObject json = new JSONObject();

        for (Field field : object.getClass().getDeclaredFields())
        {
            try
            {
                field.setAccessible(true);

                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) continue;
                if ((modifiers & Modifier.STATIC) == Modifier.STATIC) continue;

                String name = field.getName();
                String type = field.getType().getCanonicalName();

                Object ival = field.get(object);
                if (ival == null) continue;

                if (ival instanceof Json.JsonMarshaller)
                {
                    Json.put(json, name, ((Json.JsonMarshaller) ival).marshalJSON());
                    continue;
                }

                switch (type)
                {
                    case "short":
                    case "java.lang.Short":
                        Json.put(json, name, ival);
                        continue;

                    case "long":
                    case "java.lang.Long":
                        Json.put(json, name, ival);
                        continue;

                    case "float":
                    case "java.lang.Float":
                        Json.put(json, name, ival);
                        continue;

                    case "double":
                    case "java.lang.Double":
                        Json.put(json, name, ival);
                        continue;

                    case "byte":
                    case "java.lang.Byte":
                    case "int":
                    case "java.lang.Integer":
                    case "boolean":
                    case "java.lang.Boolean":
                        Json.put(json, name, ival);
                        continue;

                    case "java.lang.String":
                    case "org.json.JSONObject":
                    case "org.json.JSONArray":
                        Json.put(json, name, ival);
                        continue;

                    case "byte[]":
                        Json.put(json, name, Simple.encodeBase64((byte[]) ival));
                        continue;

                    case "java.lang.Byte[]":
                        Json.put(json, name, Simple.encodeBase64((Byte[]) ival));
                        continue;
                }

                Err.errp("unsupported field name=%s type=%s", name, type);

                return null;
            }
            catch (Exception ex)
            {
                Err.errp(ex);
                return null;
            }
        }

        return json;
    }

    @Nullable
    public static Err unmarshalJSON(Object object, JSONObject json)
    {
        if (json == null) return Err.errp();

        for (Field field : object.getClass().getDeclaredFields())
        {
            try
            {
                field.setAccessible(true);

                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) continue;
                if ((modifiers & Modifier.STATIC) == Modifier.STATIC) continue;

                String name = field.getName();
                String type = field.getType().getCanonicalName();

                Object jval = null;

                if (Json.has(json, name))
                {
                    switch (type)
                    {
                        case "short":
                        case "java.lang.Short":
                            jval = Json.getShort(json, name);
                            break;

                        case "long":
                        case "java.lang.Long":
                            jval = Json.getLong(json, name);
                            break;

                        case "float":
                        case "java.lang.Float":
                            jval = Json.getFloat(json, name);
                            break;

                        case "double":
                        case "java.lang.Double":
                            jval = Json.getDouble(json, name);
                            break;

                        case "byte":
                        case "java.lang.Byte":
                        case "int":
                        case "java.lang.Integer":
                        case "boolean":
                        case "java.lang.Boolean":
                            jval = Json.get(json, name);
                            break;

                        case "java.lang.String":
                        case "org.json.JSONObject":
                        case "org.json.JSONArray":
                            jval = Json.get(json, name);
                            break;

                        case "byte[]":
                        case "java.lang.Byte[]":
                            String base64 = Json.getString(json, name);
                            jval = Simple.decodeBase64(base64);
                            break;
                    }

                    if (jval == null)
                    {
                        Object ival = field.get(object);
                        if (ival == null) continue;

                        if (ival instanceof Json.JsonMarshaller)
                        {
                            JSONObject jobj = Json.getObject(json, name);

                            Err err = ((Json.JsonMarshaller) ival).unmarshalJSON(jobj);
                            if (err != null) return err;

                            continue;
                        }

                        continue;
                    }
                }

                try
                {
                    field.set(object, jval);
                }
                catch (Exception ex)
                {
                    //
                    // Someone changed the data type in between
                    // or supplied wrong data type.
                    //

                    return Err.errp(ex);
                }
            }
            catch (Exception ex)
            {
                return Err.errp(ex);
            }
        }

        return null;
    }
}
