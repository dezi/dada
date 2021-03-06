package com.aura.aosp.gorilla.gorilla;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Err;

import org.json.JSONObject;

public class PublicNode
{
    public String Addr;
    public Integer Port;

    public Err unMarshall(JSONObject jPublicNode)
    {
        if (jPublicNode == null) return Err.errp();

        Addr = Json.getString(jPublicNode, "addr");
        Port = Json.getInt(jPublicNode, "port");

        if ((Addr == null) || (Port == null))
        {
            return Err.err("invalid json object=%s", jPublicNode.toString());
        }

        return null;
    }

    @Override
    public String toString()
    {
        return "addr=" + Addr + " port=" + Port;
    }
}
