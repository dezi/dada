package com.aura.aosp.gorilla.service;

import com.aura.aosp.gorilla.client.IGorillaRemote;

public class GorillaRemote extends IGorillaRemote.Stub
{
    public int addNumbers(int int1, int int2)
    {
        return int1 + int2;
    }
}
