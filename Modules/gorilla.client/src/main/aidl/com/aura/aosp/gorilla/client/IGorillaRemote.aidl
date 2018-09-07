package com.aura.aosp.gorilla.client;

interface IGorillaRemote
{
    void sendClientSecret(String apkname, String clientSecret);

    boolean validateConnect(String apkname, String challenge);

    int addNumbers(int int1, int int2);
}
