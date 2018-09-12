package com.aura.aosp.gorilla.client;

import android.util.Log;

public class GorillaClientService extends IGorillaClientService.Stub
{
    private static final String LOGTAG = GorillaClientService.class.getSimpleName();

    @Override
    public boolean initServerSecret(String apkname, String serverSecret, String checksum)
    {
        GorillaIntercon.setServerSecret(apkname, serverSecret);

        String solution = GorillaHelpers.createSHASignatureBase64(
                GorillaIntercon.getClientSecret(apkname),
                apkname.getBytes(),
                serverSecret.getBytes()
        );

        boolean svlink = ((checksum != null) && checksum.equals(solution));
        GorillaIntercon.setServiceStatus(apkname, svlink);

        Log.d(LOGTAG, "initServerSecret: impl apkname=" + apkname + " serverSecret=" + serverSecret + " svlink=" + svlink);

        return svlink;
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
