package com.aura.aosp.gorilla.client;

interface IGorillaSystemService
{
    boolean initClientSecret(String apkname, String clientSecret, String checksum);

    boolean getOnlineStatus(String apkname, String checksum);

    String getOwnerUUID(String apkname, String checksum);

    String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum);
}
