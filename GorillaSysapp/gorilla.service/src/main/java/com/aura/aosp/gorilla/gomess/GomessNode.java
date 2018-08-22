package com.aura.aosp.gorilla.gomess;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.simple.Err;
import com.aura.aosp.aura.simple.Json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GomessNode
{
    public String Addr;
    public int Port;
    public int Cons;
    public String City;
    public Double Lat;
    public Double Lon;

    @Override
    public  String toString()
    {
        return ""
                + "Addr=" + Addr
                + " Port=" + Port
                + " Cons=" + Cons
                + " City=" + City
                + " Lat=" + Lat
                + " Lon=" + Lon;
    }

    public JSONObject marshall()
    {
        JSONObject jGomessNode = new JSONObject();

        Json.put(jGomessNode, "addr", Addr);
        Json.put(jGomessNode, "port", Port);
        Json.put(jGomessNode, "cons", Cons);
        Json.put(jGomessNode, "city", City);
        Json.put(jGomessNode, "lat", Lat);
        Json.put(jGomessNode, "lon", Lon);

        return jGomessNode;
    }

    public Err unMarshall(JSONObject jGomessNode)
    {
        if (jGomessNode == null) return Err.errp();

        Addr = Json.getString(jGomessNode, "addr");
        Port = Json.getInt(jGomessNode, "port");
        Cons = Json.getInt(jGomessNode, "cons");
        City = Json.getString(jGomessNode, "city");
        Lat = Json.getDouble(jGomessNode, "lat");
        Lon = Json.getDouble(jGomessNode, "lon");

        if ((Addr == null) || (Port == 0))
        {
            return Err.err("invalid json object=%s", jGomessNode.toString());
        }

        return null;
    }
}
