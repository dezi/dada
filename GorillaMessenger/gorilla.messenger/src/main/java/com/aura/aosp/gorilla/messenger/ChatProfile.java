package com.aura.aosp.gorilla.messenger;

public class ChatProfile
{
    public ChatActivity activity;

    public String remoteNick;
    public String remoteUserUUID;
    public String remoteDeviceUUID;

    public ChatProfile(ChatActivity activity, String remoteNick, String remoteUserUUID, String remoteDeviceUUID)
    {
        this.activity = activity;

        this.remoteNick = remoteNick;
        this.remoteUserUUID = remoteUserUUID;
        this.remoteDeviceUUID = remoteDeviceUUID;
    }
}
