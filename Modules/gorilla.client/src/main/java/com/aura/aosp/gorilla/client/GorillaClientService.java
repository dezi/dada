package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public boolean initServerSecret(String apkname, String serverSecret, String checksum)
    {
        GorillaIntercon.setServerSecret(apkname, serverSecret);

        String solution = GorillaIntercon.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                apkname.getBytes(),
                serverSecret.getBytes()
        );

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        if (GorillaIntercon.setServiceStatus(apkname, svlink))
        {
            GorillaClient.getInstance().receiveStatus();
        }

        Log.d(LOGTAG, "initServerSecret: impl apkname=" + apkname + " serverSecret=" + serverSecret + " svlink=" + svlink);

        if (svlink)
        {
            GorillaClient.getInstance().getOnlineStatus();
            GorillaClient.getInstance().getOwnerUUID();
        }

        return svlink;
    }

    @Override
    public boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname,
                apkname,
                Long.toString(time),
                uuid,
                senderUUID,
                deviceUUID,
                payload
        );

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "#########receivePayload: payload=" + payload + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayload(time, uuid, senderUUID, deviceUUID, payload);
        }

        return valid;
    }

    @Override
    public boolean receivePayloadResult(String apkname, String result, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(
                GorillaIntercon.getServerSecret(apkname),
                GorillaIntercon.getClientSecret(apkname),
                apkname.getBytes(),
                result.getBytes()
        );

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "#########receivePayloadResult: result=" + result + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receivePayloadResult(result);
        }

        return valid;
    }
}
