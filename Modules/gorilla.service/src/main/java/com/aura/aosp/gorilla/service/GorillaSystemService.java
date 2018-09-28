package com.aura.aosp.gorilla.service;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.gorilla.client.GorillaIntercon;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gorilla.gopoor.GopoorRegister;
import com.aura.aosp.gorilla.gopoor.GopoorSuggest;
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
    public boolean putAtom(String apkname, String atomJSON, String checksum)
    {
        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtom(atom);
        return (err == null);
    }

    @Override
    public boolean putAtomSharedBy(String apkname, String userUUID, String atomJSON, String checksum)
    {
        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtomSharedBy(userUUID, atom);
        return (err == null);
    }

    @Override
    public boolean putAtomSharedWith(String apkname, String userUUID, String atomJSON, String checksum)
    {
        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtomSharedWith(userUUID, atom);
        return (err == null);
    }

    @Override
    public String getAtom(String apkname, String atomUUID, String checksum)
    {
        return null;
    }

    @Override
    public String getAtomSharedBy(String apkname, String userUUID, String atomUUID, String checksum)
    {
        JSONObject atom = GoatomStorage.getAtom(userUUID, atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    public String getAtomSharedWith(String apkname, String userUUID, String atomUUID, String checksum)
    {
        JSONObject atom = GoatomStorage.getAtom(userUUID, atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    public String queryAtoms(String apkname, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtoms(atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public String queryAtomsSharedBy(String apkname, String userUUID, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtomsSharedBy(userUUID, atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public String queryAtomsSharedWith(String apkname, String userUUID, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtomsSharedWith(userUUID, atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public boolean registerActionEvent(String apkname, String actionDomain, String subAction, String checksum)
    {
        Err err = GopoorRegister.registerActionEvent(actionDomain, subAction);
        return (err == null);
    }

    @Override
    public String suggestActions(String apkname, String checksum)
    {
        JSONArray results = GopoorSuggest.suggestActions();
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public boolean registerContextEvent(String apkname, String actionDomain, String subContext, String subAction, String checksum)
    {
        Err err = GopoorRegister.registerContextEvent(actionDomain, subContext, subAction);
        return (err == null);
    }

    @Override
    public String suggestContextActions(String apkname, String actionDomain, String subContext, String checksum)
    {
        JSONArray results = GopoorSuggest.suggestContextActions(actionDomain, subContext);
        if (results == null) return null;

        return results.toString();
    }
}
