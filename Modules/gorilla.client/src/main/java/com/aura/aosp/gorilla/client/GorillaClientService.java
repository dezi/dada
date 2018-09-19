package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public boolean initServerSecret(String apkname, String serverSecret, String checksum)
    {
        GorillaIntercon.setServerSecret(apkname, serverSecret);

        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, serverSecret);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        if (GorillaIntercon.setServiceStatus(apkname, svlink))
        {
            GorillaClient.getInstance().receiveStatus();
        }

        Log.d(LOGTAG, "initServerSecret: impl apkname=" + apkname + " serverSecret=" + serverSecret + " svlink=" + svlink);

        if (svlink)
        {
            GorillaClient.getInstance().getUplinkStatus();
            GorillaClient.getInstance().getOwnerUUID();
        }

        return svlink;
    }

    @Override
    public boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, time, uuid, senderUUID, deviceUUID, payload);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receivePayload: payload=" + payload + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayload(time, uuid, senderUUID, deviceUUID, payload);
        }

        return valid;
    }

    @Override
    public boolean receivePayloadResult(String apkname, String result, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, result);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receivePayloadResult: result=" + result + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayloadResult(result);
        }

        return valid;
    }

    @Override
    public boolean receiveOnlineStatus(String apkname, boolean uplink, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, uplink);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOnlineStatus: uplink=" + uplink + " valid=" + valid);

        if (valid)
        {
            if (GorillaIntercon.setUplinkStatus(apkname, uplink))
            {
                GorillaClient.getInstance().receiveStatus();
            }
        }

        return valid;
    }

    @Override
    public boolean receiveOwnerUUID(String apkname, String ownerUUID, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, ownerUUID);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOwnerUUID: ownerUUID=" + ownerUUID + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receiveOwnerUUID(ownerUUID);
        }

        return valid;
    }
}
