package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private final static String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public boolean initServerSecret(String apkname, String serverSecret, String checksum)
    {
        GorillaIntercon.setServerSecret(apkname, serverSecret);

        String solution = GorillaHelpers.createSHASignatureBase64(
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
}
