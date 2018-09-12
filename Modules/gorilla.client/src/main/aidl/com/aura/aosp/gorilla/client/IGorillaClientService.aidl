package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    void initServerSecret(String apkname, String serverSecret);

    void replyServerSecret(String apkname, String serverSecret, String checksum);

    boolean validateConnect(String apkname, String challenge);
}
