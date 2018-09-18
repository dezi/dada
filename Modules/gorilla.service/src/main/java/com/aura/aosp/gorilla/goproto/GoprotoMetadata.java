package com.aura.aosp.gorilla.goproto;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Marshal;
import com.aura.aosp.aura.common.simple.Simple;

import org.json.JSONObject;

public class GoprotoMetadata implements Json.JsonMarshaller
{
    private long timeStamp;
    private int status;
    private int reserved1;
    private int reserved2;


    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public byte[] marshal()
    {
        byte[] bytes = new byte[GoprotoDefs.GorillaUUIDSize];

        System.arraycopy(Marshal.marshalLong(timeStamp), 0, bytes, 0, 8);
        System.arraycopy(Marshal.marshalShort((short) status), 0, bytes, 8, 2);
        System.arraycopy(Marshal.marshalShort((short) reserved1), 0, bytes, 10, 2);
        System.arraycopy(Marshal.marshalInt(reserved2), 0, bytes, 12, 4);

        return bytes;
    }

    public void unMarshal(byte[] bytes)
    {
        timeStamp = Marshal.unMarshalLong(Simple.sliceBytes(bytes, 0, 8));
        status = Marshal.unMarshalShort(Simple.sliceBytes(bytes, 8, 10));
        reserved1 = Marshal.unMarshalShort(Simple.sliceBytes(bytes, 10, 12));
        reserved2 = Marshal.unMarshalInt(Simple.sliceBytes(bytes, 12, 16));
    }

    @Override
    public JSONObject toJson()
    {
        return Json.toJson(this);
    }

    @Override
    public Err fromJson(JSONObject json)
    {
        return Json.fromJson(this, json);
    }
}
