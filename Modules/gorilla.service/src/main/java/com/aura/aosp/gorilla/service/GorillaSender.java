package com.aura.aosp.gorilla.service;

import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.crypter.UID;
import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.client.IGorillaClientService;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONObject;

import java.util.List;

public class GorillaSender
{
    private final static String sysApkName = "com.aura.aosp.gorilla.sysapp";

    public static void sendBroadCastOnlineStatus(boolean uplink)
    {
        List<String> apknames = GorillaIntercon.getAllApknames();

        for (String apkname : apknames)
        {
            IGorillaClientService remote = GorillaIntercon.getClientService(apkname);
            if (remote == null) continue;

            String checksum = GorillaIntercon.createSHASignatureBase64(apkname, sysApkName, uplink);

            try
            {
                boolean valid = remote.receiveUplinkStatus(sysApkName, uplink, checksum);
                if (! valid) Err.errp("invalid service apkname=%s", apkname);
            }
            catch (Exception ex)
            {
                Err.errp(ex);
            }
        }
    }

    public static void sendBroadCastOwnerUUID(String ownerUUID)
    {
        List<String> apknames = GorillaIntercon.getAllApknames();

        for (String apkname : apknames)
        {
            IGorillaClientService remote = GorillaIntercon.getClientService(apkname);
            if (remote == null) continue;

            String checksum = GorillaIntercon.createSHASignatureBase64(apkname, sysApkName, ownerUUID);

            try
            {
                boolean valid = remote.receiveOwnerUUID(sysApkName, ownerUUID, checksum);
                if (! valid) Err.errp("invalid service apkname=%s", apkname);
            }
            catch (Exception ex)
            {
                Err.errp(ex);
            }
        }
    }

    @Nullable
    private static Err persistTicketForClientApp(GoprotoTicket ticket)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        JSONObject json = ticket.marshalJSON();
        if (json == null) return Err.getLastErr();

        Long timeStamp = ticket.getTimeStamp();
        byte[] keyUUID = ticket.getMessageUUID();
        byte[] nonceUUId = UID.randomUUID();

        return GorillaPersist.persistTicketForLocalClientApp(apkname, timeStamp, keyUUID, nonceUUId, json);
    }

    @Nullable
    public static Err sendPayload(GoprotoTicket ticket)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        IGorillaClientService remote = GorillaIntercon.getClientService(apkname);

        if (remote == null)
        {
            Log.d("unknown/unconnected apkname=%s", apkname);

            GorillaSystem.startClientService(apkname);

            Err err = persistTicketForClientApp(ticket);
            if (err != null) return err;

            return null;
        }

        Long time = ticket.getTimeStamp();
        if (time == null)
        {
            return Err.err("missing time stamp.");
        }

        String uuid = Simple.encodeBase64(ticket.getMessageUUID());
        String senderUUID = Simple.encodeBase64(ticket.getSenderUserUUID());
        String deviceUUID = Simple.encodeBase64(ticket.getSenderDeviceUUID());
        String payload = new String(ticket.getPayload());

        String checksum = GorillaIntercon.createSHASignatureBase64(apkname, sysApkName, time, uuid, senderUUID, deviceUUID, payload);

        try
        {
            boolean valid = remote.receivePayload(sysApkName, time, uuid, senderUUID, deviceUUID, payload, checksum);

            if (! valid)
            {
                return Err.err("invalid service apkname=%s", apkname);
            }

            Log.d("paaaayyyyyyyyload=%s", payload);
        }
        catch (Exception ex)
        {
            return Err.err(ex);
        }

        return null;
    }

    @Nullable
    public static Err sendPayloadResult(GoprotoTicket ticket, JSONObject result)
    {
        String apkname = GorillaMapper.mapUUID2APK(Simple.encodeBase64(ticket.getAppUUID()));
        if (apkname == null) return Err.getLastErr();

        IGorillaClientService remote = GorillaIntercon.getClientService(apkname);

        if (remote == null)
        {
            Log.d("unknown/unconnected apkname=%s", apkname);

            GorillaSystem.startClientService(apkname);

            // Todo: persists result ticket.

            //Err err = persistTicketForClientApp(ticket);
            //if (err != null) return err;

            return null;
        }

        String resultStr = result.toString();

        String checksum = GorillaIntercon.createSHASignatureBase64(apkname, sysApkName, resultStr);

        try
        {
            boolean valid = remote.receivePayloadResult(sysApkName, resultStr, checksum);

            if (! valid)
            {
                return Err.err("invalid service apkname=%s", apkname);
            }
        }
        catch (Exception ex)
        {
            return Err.err(ex);
        }

        return null;
    }
}
