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

        boolean valid = ((checksum != null) && checksum.equals(solution));

        Log.d(LOGTAG, "initServerSecret: impl apkname=" + apkname + " serverSecret=" + serverSecret + " valid=" + valid);

        return valid;
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
