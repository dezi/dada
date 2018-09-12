package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    void sendServerSecret(String apkname, String serverSecret, String challenge);
}
