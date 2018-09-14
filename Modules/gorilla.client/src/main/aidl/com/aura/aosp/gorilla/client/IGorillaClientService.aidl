package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    boolean initServerSecret(String apkname, String serverSecret, String checksum);

    boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum);

    boolean receivePayloadResult(String apkname, String result, String checksum);

    boolean receiveOnlineStatus(String apkname, boolean uplink, String checksum);

    boolean receiveOwnerUUID(String apkname, String ownerUUID, String checksum);
}
