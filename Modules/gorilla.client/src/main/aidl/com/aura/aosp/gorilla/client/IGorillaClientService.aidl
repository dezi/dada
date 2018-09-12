package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    boolean initServerSecret(String apkname, String serverSecret, String checksum);






    void replyServerSecret(String apkname, String serverSecret, String checksum);

    boolean validateConnect(String apkname, String challenge);
}
