package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    String returnYourSignature(String apkname);

    boolean validateConnect(String apkname, String checksum);

    boolean receiveOwnerUUID(String apkname, String ownerUUID, String checksum);

    boolean receiveUplinkStatus(String apkname, boolean uplink, String checksum);

    boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum);

    boolean receivePayloadResult(String apkname, String resultJSON, String checksum);

    boolean receivePhraseSuggestions(String apkname, String resultJSON, String checksum);
}
