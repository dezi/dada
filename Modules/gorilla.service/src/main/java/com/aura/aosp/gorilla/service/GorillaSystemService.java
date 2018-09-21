package com.aura.aosp.gorilla.service;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gorilla.gopmai.GopmaiStorage;
import com.aura.aosp.gorilla.gopmai.GopmaiSuggest;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONArray;
import org.json.JSONObject;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    @Override
    public String returnYourSecret(String apkname)
    {
        GorillaService.startClientService(apkname);

        Log.d("impl serverSecret=%s", GorillaIntercon.getServerSecretBase64(apkname));

        return GorillaIntercon.getServerSecretBase64(apkname);
    }

    @Override
    public boolean validateConnect(String apkname, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        boolean svlink = ((checksum != null) && checksum.equals(solution));

        Log.d("impl apkname=%s serverSecret=%s clientSecret=%s svlink=%b",
                apkname,
                GorillaIntercon.getServerSecretBase64(apkname),
                GorillaIntercon.getClientSecretBase64(apkname),
                svlink);

        if (!svlink) return false;

        GorillaIntercon.setServiceStatus(apkname, true);

        return true;
    }

    @Override
    public boolean getUplinkStatus(String apkname, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        if ((checksum == null) || !checksum.equals(solution))
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
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        if ((checksum == null) || !checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return null;
        }

        return Owner.getOwnerUUIDBase64();
    }

    @Override
    public boolean requestPersisted(String apkname, String checksum)
    {
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname);

        if ((checksum == null) || !checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        Log.d("sending all persisted tickets.");

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
        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, userUUID, deviceUUID, payload);

        if ((checksum == null) || !checksum.equals(solution))
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

        String solution = GorillaIntercon.createSHASignatureBase64(apkname, apkname, userUUID, deviceUUID, messageUUID);

        if ((checksum == null) || !checksum.equals(solution))
        {
            Log.e("checksum failed!");
            return false;
        }

        return GomessHandler.getInstance().sendPayloadRead(apkname, userUUID, deviceUUID, messageUUID);
    }

    @Override
    public boolean putAtom(String apkname, String userUUID, String atomJSON, String checksum)
    {
        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtom(userUUID, atom);
        return (err == null);
    }

    @Override
    public String getAtom(String apkname, String userUUID, String atomUUID, String checksum)
    {
        JSONObject atom = GoatomStorage.getAtom(userUUID, atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    public String queryAtoms(String apkname, String userUUID, String atomType, long timeFrom, long timeTo)
    {
        JSONArray results = GoatomStorage.queryAtoms(userUUID, atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public boolean pmaiRegisterActionEvent(String apkname, String actionDomain, String subAction)
    {
        Err err = GopmaiStorage.registerActionEvent(actionDomain, subAction);
        return (err == null);
    }

    @Override
    public String pmaiSuggestActions(String apkname)
    {
        JSONArray results = GopmaiSuggest.suggestActions();
        if (results == null) return null;

        return results.toString();
    }
}
