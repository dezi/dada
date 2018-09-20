package com.aura.aosp.gorilla.service;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONObject;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    @Override
    public boolean initClientSecret(String apkname, String clientSecret, String checksum)
    {
        if (! GorillaIntercon.getServiceStatus(apkname))
        {
            GorillaIntercon.setClientSecret(apkname, clientSecret);

            GorillaService.startClientService(apkname);

            String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);

            String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, clientSecret);

            boolean svlink = ((checksum != null) && checksum.equals(solution));
            GorillaIntercon.setServiceStatus(apkname, svlink);

            Log.d("impl apkname=%s serverSecret=%s clientSecret=%s svlink=%b",
                    apkname,
                    GorillaIntercon.getServerSecretBase64(apkname),
                    GorillaIntercon.getClientSecretBase64(apkname),
                    svlink);
        }

        return GorillaIntercon.getServiceStatus(apkname);
    }

    @Override
    public boolean getUplinkStatus(String apkname, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        boolean status = GomessHandler.getInstance().isSessionConnected();

        Log.d("status=%b apkname=%s", status, apkname);

        return status;
    }

    @Override
    public String getOwnerUUID(String apkname, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        return Owner.getOwnerUUIDBase64();
    }

    @Override
    public boolean requestPersisted(String apkname, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        Log.d("sending all persisted tickets.");

        //
        // Todo: send all persisted tickets here.
        //

        while (true)
        {
            JSONObject json = GorillaPersist.unpersistNextTicketForLocalClientApp(apkname);
            if (json == null) break;

            Log.d("########ticket=%s", json.toString());


            GoprotoTicket ticket = new GoprotoTicket();
            Err err = ticket.unmarshalJSON(json);
            if (err != null) break;

            Log.d("##################STATTTTTTTTTTTUS=%d", ticket.getStatus());
            Log.d("##########################");
            ticket.dumpTicket();
            Log.d("##########################");

            err = GorillaSender.sendPayload(ticket);

            if (err != null)
            {
                Log.e("huhu err=%s", err.err);
                break;
            }

            Log.e("send no err");
        }

        return true;
    }

    @Override
    public String sendPayload(String apkname, String userUUID, String deviceUUID, String payload, String checksum)
    {
        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, userUUID, deviceUUID, payload);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        JSONObject result = GomessHandler.getInstance().sendPayload(apkname, userUUID, deviceUUID, payload);

        return result.toString();
    }

    @Override
    public boolean sendPayloadRead(String apkname, String userUUID, String deviceUUID, String messageUUID, String checksum)
    {

        Log.d("...");

        String serverSecret = GorillaIntercon.getServerSecretBase64(apkname);
        String clientSecret = GorillaIntercon.getClientSecretBase64(apkname);

        String solution = GorillaIntercon.createSHASignatureBase64neu(serverSecret, clientSecret, apkname, userUUID, deviceUUID, messageUUID);

        if ((checksum == null) || ! checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        return GomessHandler.getInstance().sendPayloadRead(apkname, userUUID, deviceUUID, messageUUID);
    }
}
