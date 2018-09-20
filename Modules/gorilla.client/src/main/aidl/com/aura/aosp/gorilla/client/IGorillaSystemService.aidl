package com.aura.aosp.gorilla.client;

interface IGorillaSystemService
{
    String returnYourSecret(String apkname);

    boolean validateConnect(String apkname, String checksum);

    boolean getUplinkStatus(String apkname, String checksum);

    String getOwnerUUID(String apkname, String checksum);

    boolean requestPersisted(String apkname, String checksum);

    String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum);

    boolean sendPayloadRead(String apkname, String userUUID, String deviceUUID, String messageUUID, String checksum);
}
