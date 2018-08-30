package com.aura.aosp.gorilla.gomess;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;

import org.json.JSONObject;

@SuppressWarnings({"WeakerAccess", "unused"})
public class GomessNode
{
    public String addr;
    public int port;
    public int cons;
    public String city;
    public Double lat;
    public Double lon;

    @Override
    public String toString()
    {
        return "addr=" + addr
                + " port=" + port
                + " cons=" + cons
                + " city=" + city
                + " lat=" + lat
                + " lon=" + lon;
    }

    @NonNull
    public JSONObject marshall()
    {
        JSONObject jGomessNode = new JSONObject();

        Json.put(jGomessNode, "addr", addr);
        Json.put(jGomessNode, "port", port);
        Json.put(jGomessNode, "cons", cons);
        Json.put(jGomessNode, "city", city);
        Json.put(jGomessNode, "lat", lat);
        Json.put(jGomessNode, "lon", lon);

        return jGomessNode;
    }

    @Nullable
    public Err unMarshall(JSONObject jGomessNode)
    {
        if (jGomessNode == null) return Err.errp();

        addr = Json.getString(jGomessNode, "addr");
        port = Json.getInt(jGomessNode, "port");
        cons = Json.getInt(jGomessNode, "cons");
        city = Json.getString(jGomessNode, "city");
        lat = Json.getDouble(jGomessNode, "lat");
        lon = Json.getDouble(jGomessNode, "lon");

        if ((addr == null) || (port == 0))
        {
            return Err.err("invalid json object=%s", jGomessNode.toString());
        }

        return null;
    }
}
