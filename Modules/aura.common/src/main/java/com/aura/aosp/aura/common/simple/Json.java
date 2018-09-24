package com.aura.aosp.aura.common.simple;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.io.File;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Json
{
    public static JSONObject getFileContent(File jsonFile)
    {
        if (jsonFile == null)
        {
            Err.errp();
            return null;
        }

        String jsonStr = Simple.getFileContent(jsonFile);

        if (jsonStr == null) return null;

        return fromStringObject(jsonStr);
    }

    public static Err putFileContent(File jsonFile, JSONObject content)
    {
        if ((jsonFile == null) || (content == null)) return Err.errp();

        return Simple.putFileContent(jsonFile, Json.defuck(Json.toPretty(content)));
    }

    @Nullable
    public static JSONObject fromStringObject(String jsonStr)
    {
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
            Err.errp();
            return null;
        }
    }

    public static JSONArray fromStringArray(String jsonstr)
    {
        if (jsonstr != null)
        {
            try
            {
                return new JSONArray(jsonstr);
            }
            catch (Exception ex)
            {
                Log.d(ex.toString());
            }
        }

        return new JSONArray();
    }

    public static JSONObject clone(JSONObject json)
    {
        try
        {
            return new JSONObject(json.toString());
        }
        catch (Exception ex)
        {
            Log.d(ex.toString());
        }

        return new JSONObject();
    }

    public static JSONArray clone(JSONArray json)
    {
        try
        {
            return new JSONArray(json.toString());
        }
        catch (Exception ex)
        {
            Log.d(ex.toString());
        }

        return new JSONArray();
    }

    public static void remove(JSONObject json, String key)
    {
        json.remove(key);
    }

    public static void remove(JSONArray json, int index)
    {
        json.remove(index);
    }

    public static boolean equals(JSONObject j1, String k1, String val)
    {
        if ((k1 == null) && (val == null)) return true;

        String s1 = getString(j1, k1);

        return ((s1 == null) && (val == null)) || ((s1 != null) && s1.equals(val));
    }

    public static boolean equals(JSONObject j1, String k1, JSONObject j2)
    {
        Object s1 = get(j1, k1);
        Object s2 = get(j2, k1);

        return ((s1 == null) && (s2 == null)) || ((s1 != null) && s1.equals(s2));
    }

    public static void copy(JSONObject dst, String dkey, JSONObject src, String skey)
    {
        Object tmp = get(src, skey);
        if (tmp != null) put(dst, dkey, tmp);
    }

    public static void copy(JSONObject dst, String key, JSONObject src)
    {
        Object tmp = get(src, key);
        if (tmp != null) put(dst, key, tmp);
    }

    public static void copy(JSONObject dst, JSONObject src)
    {
        Iterator<String> keysIterator = src.keys();

        while (keysIterator.hasNext())
        {
            String key = keysIterator.next();

            copy(dst, key, src);
        }
    }

    public static JSONArray append(JSONArray dst, JSONArray src)
    {
        if ((dst != null) && (src != null))
        {
            for (int inx = 0; inx < src.length(); inx++)
            {
                put(dst, get(src, inx));
            }
        }

        return dst;
    }

    public static void put(JSONObject json, String key, Object val)
    {
        try
        {
            json.put(key, val);
        }
        catch (Exception ex)
        {
            Log.d(ex.toString());
        }
    }

    public static void put(JSONArray json, Object val)
    {
        json.put(val);
    }

    public static boolean has(JSONObject json, String key)
    {
        return (json != null) && json.has(key);
    }

    @Nullable
    public static Object get(JSONObject json, String key)
    {
        try
        {
            return json.get(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Object get(JSONArray json, int index)
    {
        try
        {
            return json.get(index);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Float getFloat(JSONObject json, String key)
    {
        try
        {
            return (float) json.getDouble(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Double getDouble(JSONObject json, String key)
    {
        try
        {
            return json.getDouble(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Boolean getBoolean(JSONObject json, String key)
    {
        try
        {
            return json.getBoolean(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Byte getByte(JSONObject json, String key)
    {
        try
        {
            return (byte) (json.getInt(key) & 0xff);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Short getShort(JSONObject json, String key)
    {
        try
        {
            return (short) (json.getInt(key) & 0xffff);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Integer getInt(JSONObject json, String key)
    {
        try
        {
            return json.getInt(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Long getLong(JSONObject json, String key)
    {
        try
        {
            return json.getLong(key);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static String getString(JSONObject json, String key)
    {
        try
        {
            Object obj = json.get(key);

            if (obj instanceof String) return (String) obj;

            if (obj instanceof JSONArray)
            {
                JSONArray strings = json.getJSONArray(key);

                String result = "";

                for (int inx = 0; inx < strings.length(); inx++)
                {
                    result += getString(strings, inx);
                }

                return result;
            }

            return "";
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static Integer getInt(JSONArray json, int index)
    {
        try
        {
            return json.getInt(index);
        }
        catch (Exception ignore)
        {
            return null;
        }
    }

    @Nullable
    public static String getString(JSONArray json, int index)
    {
        try
        {
            return json.getString(index);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static Object getGeneric(JSONObject json, String key)
    {
        try
        {
            return json.get(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }


    @Nullable
    public static JSONArray getArray(JSONObject json, String key)
    {
        try
        {
            return json.getJSONArray(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static JSONObject getObject(JSONObject json, String key)
    {
        try
        {
            return json.getJSONObject(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static JSONObject getObject(JSONArray json, int index)
    {
        try
        {
            return json.getJSONObject(index);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static String toPretty(JSONObject jsonObject)
    {
        if (jsonObject != null)
        {
            try
            {
                return defuck(jsonObject.toString(2));
            }
            catch (Exception ignored)
            {
            }
        }

        return "NULL";
    }

    @Nullable
    public static String toPretty(JSONArray jsonArray)
    {
        if (jsonArray != null)
        {
            try
            {
                return defuck(jsonArray.toString(2));
            }
            catch (Exception ignored)
            {
            }
        }

        return "NULL";
    }

    public static Set<String> toSet(JSONArray jsonArray)
    {
        Set<String> set = new HashSet<>();

        if (jsonArray != null)
        {
            try
            {
                for (int inx = 0; inx < jsonArray.length(); inx++)
                {
                    set.add(jsonArray.getString(inx));
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return set;
    }

    public static String defuck(String json)
    {
        //
        // I hate slash escaping.
        //

        return (json == null) ? "{}" : json.replace("\\/", "/");
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

        boolean ok = false;

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
