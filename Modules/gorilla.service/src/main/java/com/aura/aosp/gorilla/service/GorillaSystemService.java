package com.aura.aosp.gorilla.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Err;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.aura.common.univid.Owner;
import com.aura.aosp.aura.common.simple.Log;

import com.aura.aosp.aura.nlp.suggest.Suggest;
import com.aura.aosp.gorilla.client.IGorillaSystemService;

import com.aura.aosp.gorilla.goatom.GoatomStorage;
import com.aura.aosp.gorilla.goatoms.GorillaAtomContact;
import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gorilla.gopoor.GopoorRegister;
import com.aura.aosp.gorilla.gopoor.GopoorSuggest;
import com.aura.aosp.gorilla.goproto.GoprotoTicket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class GorillaSystemService extends IGorillaSystemService.Stub
{
    @Override
    public String returnYourSignature(String apkname)
    {
        GorillaSystem.startClientService(apkname);

        Log.d("impl apkname=%s serverSignature=%s", apkname, GorillaIntercon.getServerSignatureBase64(apkname));

        return GorillaIntercon.getServerSignatureBase64(apkname);
    }

    @Override
    public boolean validateConnect(@NonNull String apkname, @NonNull String checksum)
    {
        boolean svlink = GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname);

        Log.d("impl apkname=%s serverSignature=%s clientSignature=%s svlink=%b",
                apkname,
                GorillaIntercon.getServerSignatureBase64(apkname),
                GorillaIntercon.getClientSignatureBase64(apkname),
                svlink);

        if (!svlink) return false;

        GorillaIntercon.setServiceStatus(apkname, true);

        return true;
    }

    @Override
    public boolean getUplinkStatus(@NonNull String apkname, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum,apkname, apkname))
        {
            return false;
        }

        boolean status = GomessHandler.getInstance().isSessionConnected();

        Log.d("status=%b apkname=%s", status, apkname);

        return status;
    }

    @Override
    public String getOwnerUUID(@NonNull String apkname, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum,apkname, apkname))
        {
            return null;
        }

        return Owner.getOwnerUUIDBase64();
    }

    @Override
    @Nullable
    public String sendPayload(@NonNull String apkname, @NonNull String userUUID, @NonNull String deviceUUID, @NonNull String payload, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, userUUID, deviceUUID, payload))
        {
            return null;
        }

        JSONObject result = GomessHandler.getInstance().sendPayload(apkname, userUUID, deviceUUID, payload);

        return result.toString();
    }

    @Override
    public boolean sendPayloadRead(@NonNull String apkname, @NonNull String userUUID, @NonNull String deviceUUID, @NonNull String messageUUID, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, userUUID, deviceUUID, messageUUID))
        {
            return false;
        }

        return GomessHandler.getInstance().sendPayloadRead(apkname, userUUID, deviceUUID, messageUUID);
    }

    @Override
    @Nullable
    public String sendPayloadQuer(@NonNull String apkname, @NonNull String toapkname, @NonNull String userUUID, @NonNull String deviceUUID, @NonNull String payload, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, toapkname, userUUID, deviceUUID, payload))
        {
            return null;
        }

        JSONObject result = GomessHandler.getInstance().sendPayload(toapkname, userUUID, deviceUUID, payload);

        return result.toString();
    }

    @Override
    public boolean sendPayloadReadQuer(@NonNull String apkname, @NonNull String toapkname, @NonNull String userUUID, @NonNull String deviceUUID, @NonNull String messageUUID, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, toapkname, userUUID, deviceUUID, messageUUID))
        {
            return false;
        }

        return GomessHandler.getInstance().sendPayloadRead(toapkname, userUUID, deviceUUID, messageUUID);
    }

    @Override
    public boolean putAtom(@NonNull String apkname, @NonNull String atomJSON, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, atomJSON))
        {
            return false;
        }

        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtom(atom);
        return (err == null);
    }

    @Override
    public boolean putAtomSharedBy(@NonNull String apkname, @NonNull String userUUID, @NonNull String atomJSON, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, userUUID, atomJSON))
        {
            return false;
        }

        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtomSharedBy(userUUID, atom);
        return (err == null);
    }

    @Override
    public boolean putAtomSharedWith(@NonNull String apkname, @NonNull String userUUID, @NonNull String atomJSON, @NonNull String checksum)
    {
        if (!GorillaIntercon.validateSHASignatureBase64(checksum, apkname, apkname, userUUID, atomJSON))
        {
            return false;
        }

        JSONObject atom = Json.fromStringObject(atomJSON);
        if (atom == null) return false;

        Err err = GoatomStorage.putAtomSharedWith(userUUID, atom);
        return (err == null);
    }

    @Override
    @Nullable
    public String getAtom(@NonNull String apkname, @NonNull String atomUUID, @NonNull String checksum)
    {
        JSONObject atom = GoatomStorage.getAtom(atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    @Nullable
    public String getAtomSharedBy(String apkname, String userUUID, String atomUUID, String checksum)
    {
        JSONObject atom = GoatomStorage.getAtomSharedBy(userUUID, atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    @Nullable
    public String getAtomSharedWith(String apkname, String userUUID, String atomUUID, String checksum)
    {
        JSONObject atom = GoatomStorage.getAtomSharedWith(userUUID, atomUUID);
        if (atom == null) return null;

        return atom.toString();
    }

    @Override
    @Nullable
    public String queryAtoms(String apkname, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtoms(atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    @Nullable
    public String queryAtomsSharedBy(String apkname, String userUUID, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtomsSharedBy(userUUID, atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    @Nullable
    public String queryAtomsSharedWith(String apkname, String userUUID, String atomType, long timeFrom, long timeTo, String checksum)
    {
        JSONArray results = GoatomStorage.queryAtomsSharedWith(userUUID, atomType, timeFrom, timeTo);
        if (results == null) return null;

        return results.toString();
    }

    @Override
    public boolean registerActionEvent(String apkname, String actionDomain, String checksum)
    {
        Err err = GopoorRegister.registerActionEvent(actionDomain);
        return (err == null);
    }

    @Override
    public boolean registerActionEventDomain(String apkname, String actionDomain, String subAction, String checksum)
    {
        Err err = GopoorRegister.registerActionEvent(actionDomain, subAction);
        return (err == null);
    }

    @Override
    public boolean registerActionEventDomainContext(String apkname, String actionDomain, String subContext, String subAction, String checksum)
    {
        Err err = GopoorRegister.registerContextEvent(actionDomain, subContext, subAction);
        return (err == null);
    }

    /**
     * Request suggestions for possible actions on root level.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return JSON array string with GorillaAtomAction JSON objects.
     */
    @Override
    public String suggestActions(String apkname, String checksum)
    {
        JSONArray results = GopoorSuggest.suggestActions();
        if (results == null) return null;

        return results.toString();
    }

    /**
     * Request suggestions for possible actions on action domain level.
     *
     * @param apkname  the apk name of requesting app.
     * @param actionDomain action domain in reversed order.
     * @param checksum parameters checksum.
     * @return JSON array string with GorillaAtomAction JSON objects.
     */
    @Override
    public String suggestActionsDomain(String apkname, String actionDomain, String checksum)
    {
        JSONArray results = GopoorSuggest.suggestActions(actionDomain);
        if (results == null) return null;

        return results.toString();
    }

    /**
     * Request suggestions for possible actions on action domain with context level.
     *
     * @param apkname  the apk name of requesting app.
     * @param actionDomain action domain in reversed order.
     * @param subContext sub context in action domain.
     * @param checksum parameters checksum.
     * @return JSON array string with GorillaAtomAction JSON objects.
     */
    @Override
    public String suggestActionsDomainContext(String apkname, String actionDomain, String subContext, String checksum)
    {
        JSONArray results = GopoorSuggest.suggestContextActions(actionDomain, subContext);
        if (results == null) return null;

        return results.toString();
    }

    /**
     * Request from client service to indicate, that is is
     * now ready to receive any persisted payload. The server
     * will push all outstanding payloads via the client service
     * interface.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return true if request valid.
     */
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

    /**
     * Request all contacts of device owner.
     *
     * @param apkname  the apk name of requesting app.
     * @param checksum parameters checksum.
     * @return JSON array string with contacts UUIDs or null.
     */
    @Override
    public String requestContacts(String apkname, String checksum)
    {
        JSONArray uuidList = new JSONArray();
        List<Identity> contacts = Contacts.getAllContacts();

        for (Identity contact : contacts)
        {
            Json.put(uuidList, contact.getUserUUIDBase64());
        }

        return uuidList.toString();
    }

    /**
     * @param apkname     the apk name of requesting app.
     * @param contactUUID the contacts UUID.
     * @param checksum    parameters checksum.
     * @return JSON object string of type GorillaContact or null.
     */
    @Override
    public String requestContactData(String apkname, String contactUUID, String checksum)
    {
        Identity contact = Contacts.getContact(contactUUID);
        if (contact == null) return null;

        GorillaAtomContact contactAtom = new GorillaAtomContact();

        contactAtom.setUserUUIDBase64(contact.getUserUUIDBase64());
        contactAtom.setNick(contact.getNick());
        contactAtom.setFull(contact.getFull());
        contactAtom.setCountry(contact.getCountry());

        return contactAtom.toString();
    }

    /**
     * Request suggestion for text phrase synchronously.
     *
     * @param apkname  the apk name of requesting app.
     * @param phrase   the text phrase for suggestions.
     * @param checksum parameters checksum.
     * @return JSON object string of type GorillaSuggestions or null.
     */
    @Override
    public String requestPhraseSuggestionsSync(String apkname, String phrase, String checksum)
    {
        JSONObject result = Suggest.hintPhrase(Locale.getDefault().getLanguage(), phrase);
        String resultStr = Json.toString(result);

        Log.d("result=%s", resultStr);

        return resultStr;
    }

    /**
     * Request suggestion for text phrase via service callback.
     *
     * @param apkname  the apk name of requesting app.
     * @param phrase   the text phrase for suggestions.
     * @param checksum parameters checksum.
     * @return true if request is beeing processed.
     */
    @Override
    public boolean requestPhraseSuggestionsAsync(String apkname, String phrase, String checksum)
    {
        JSONObject result = Suggest.hintPhrase(Locale.getDefault().getLanguage(), phrase);
        Err err = GorillaSender.sendPhraseSuggestions(apkname, result);

        return (err == null);
    }
}
