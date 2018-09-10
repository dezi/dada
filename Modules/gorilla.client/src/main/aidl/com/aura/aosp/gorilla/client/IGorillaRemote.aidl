package com.aura.aosp.gorilla.client;

interface IGorillaRemote
{
    void sendClientSecret(String apkname, String clientSecret);
    boolean validateConnect(String apkname, String challenge);
    String getOwnerUUID(String apkname, String checksum);
}
