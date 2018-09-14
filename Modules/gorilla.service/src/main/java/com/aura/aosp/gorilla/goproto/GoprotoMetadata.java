package com.aura.aosp.gorilla.goproto;

import com.aura.aosp.aura.common.simple.Marshal;
import com.aura.aosp.aura.common.simple.Simple;

public class GoprotoMetadata
{
    private long timeStamp;
    private long reserved1;

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public byte[] marshal()
    {
        byte[] bytes = new byte[GoprotoDefs.GorillaUUIDSize];

        System.arraycopy(Marshal.marshalLong(timeStamp), 0, bytes, 0, 8);
        System.arraycopy(Marshal.marshalLong(reserved1), 0, bytes, 8, 8);

        return bytes;
    }

    public void unMarshal(byte[] bytes)
    {
        timeStamp = Marshal.unMarshalLong(Simple.sliceBytes(bytes, 0, 8));
        reserved1 = Marshal.unMarshalLong(Simple.sliceBytes(bytes, 8, 16));
    }
}
