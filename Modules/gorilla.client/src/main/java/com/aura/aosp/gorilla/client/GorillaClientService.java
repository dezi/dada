package com.aura.aosp.gorilla.client;

public class GorillaClientService extends IGorillaClientService.Stub
{
    @Override
    public void initServerSecret(String apkname, String serverSecret)
    {
    }

    @Override
    public void replyServerSecret(String apkname, String serverSecret, String checksum)
    {
    }

    @Override
    public boolean validateConnect(String apkname, String challenge)
    {
        return false;
    }
}
