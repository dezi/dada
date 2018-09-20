package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public String returnYourSecret(String apkname)
    {
        Log.d(LOGTAG,"returnYourSecret: impl clientSecret=" + GorillaIntercon.getClientSecretBase64(apkname));

        return GorillaIntercon.getClientSecretBase64(apkname);
    }

    @Override
    public boolean validateConnect(String apkname, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "validateConnect: impl"
                + " apkname=" + apkname
                + " serverSecret=" + serverSecret
                + " clientSecret=" + GorillaIntercon.getClientSecretBase64(apkname)
                + " svlink=" + svlink);

        if (!svlink) return false;

        GorillaIntercon.setServiceStatus(apkname, true);

        GorillaClient.getInstance().receiveStatus();
        GorillaClient.getInstance().getUplinkStatus();
        GorillaClient.getInstance().getOwnerUUID();
        GorillaClient.getInstance().startMainActivity();

        return true;
    }

    @Override
    public boolean initServerSecret(String apkname, String serverSecret, String checksum)
    {
        GorillaIntercon.setServerSecret(apkname, serverSecret);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, serverSecret);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "initServerSecret: impl"
                + " apkname=" + apkname
                + " serverSecret=" + serverSecret
                + " clientSecret=" + GorillaIntercon.getClientSecretBase64(apkname)
                + " svlink=" + svlink);

        if (!svlink) return false;

        GorillaIntercon.setServiceStatus(apkname, true);

        GorillaClient.getInstance().receiveStatus();

        GorillaClient.getInstance().getUplinkStatus();
        GorillaClient.getInstance().getOwnerUUID();
        GorillaClient.getInstance().startMainActivity();

        return true;
    }

    @Override
    public boolean receivePayload(String apkname, long time, String uuid, String senderUUID, String deviceUUID, String payload, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, time, uuid, senderUUID, deviceUUID, payload);

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
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, result);

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
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, uplink);

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
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, ownerUUID);

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "receiveOwnerUUID: ownerUUID=" + ownerUUID + " valid=" + valid);

        if (valid)
        {
            GorillaClient.getInstance().receiveOwnerUUID(ownerUUID);
        }

        return valid;
    }
}
