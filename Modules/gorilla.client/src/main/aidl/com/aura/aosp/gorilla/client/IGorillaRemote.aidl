package com.aura.aosp.gorilla.client;

interface IGorillaRemote
{
    void sendClientSecret(String apkname, String clientSecret);
    boolean validateConnect(String apkname, String challenge);

    boolean getOnlineStatus(String apkname, String checksum);

    String getOwnerUUID(String apkname, String checksum);

    String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum);
}
