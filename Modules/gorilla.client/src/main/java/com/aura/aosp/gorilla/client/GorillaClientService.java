package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public String returnYourSecret(String apkname)
    {
        Log.d(LOGTAG,"returnYourSecret: impl"
                + " apkname=" + apkname
                + " clientSecret=" + GorillaCredentials.getClientSecretBase64());

        return GorillaCredentials.getClientSecretBase64();
    }

    @Override
    public boolean validateConnect(String apkname, String checksum)
    {
        String solution = GorillaCredentials.createSHASignatureBase64(apkname);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "validateConnect: impl"
                + " apkname=" + apkname
                + " serverSecret=" + GorillaCredentials.getServerSecretBase64()
                + " clientSecret=" + GorillaCredentials.getClientSecretBase64()
                + " svlink=" + svlink);

        if (!svlink) return false;

        GorillaCredentials.setServiceStatus(true);

        GorillaClient.getInstance().receiveStatus();
        GorillaClient.getInstance().getUplinkStatus();
        GorillaClient.getInstance().getOwnerUUID();

        return true;
    }

    @Override
    public boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum)
    {
        String solution = GorillaCredentials.createSHASignatureBase64(apkname, time, uuid, senderUUID, deviceUUID, payload);

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
        String solution = GorillaCredentials.createSHASignatureBase64(apkname, result);

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
        String solution = GorillaCredentials.createSHASignatureBase64(apkname, uplink);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOnlineStatus: uplink=" + uplink + " valid=" + valid);

        if (valid)
        {
            if (GorillaCredentials.setUplinkStatus(uplink))
            {
                GorillaClient.getInstance().receiveStatus();
            }
        }

        return valid;
    }

    @Override
    public boolean receiveOwnerUUID(String apkname, String ownerUUID, String checksum)
    {
        String solution = GorillaCredentials.createSHASignatureBase64(apkname, ownerUUID);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOwnerUUID: ownerUUID=" + ownerUUID + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receiveOwnerUUID(ownerUUID);
        }

        return valid;
    }
}
