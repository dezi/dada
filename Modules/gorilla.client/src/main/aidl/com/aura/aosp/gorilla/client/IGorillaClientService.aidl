package com.aura.aosp.gorilla.client;

interface IGorillaClientService
{
    boolean initServerSecret(String apkname, String serverSecret, String checksum);
}
