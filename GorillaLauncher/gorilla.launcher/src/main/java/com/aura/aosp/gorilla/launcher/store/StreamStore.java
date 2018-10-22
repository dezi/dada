package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.model.GorillaHelper;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.MessageStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.InvisibleStreamItem;
import com.aura.aosp.gorilla.launcher.model.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class StreamStore {

    public static String EXTRA_CHAT_PARTNER_UUID = "com.aura.aosp.gorilla.launcher.chat.partner_uuid";

    private static final String LOGTAG = StreamStore.class.getSimpleName();
    private GorillaClient gorillaClient = GorillaClient.getInstance();
    private Context context;

    public StreamStore(Context context) {
        this.setContext(context);
    }

    public static final InvisibleStreamItem emptyItem = new InvisibleStreamItem();

    /**
     * Get content stream items for given atom context
     *
     * @param atomContext
     * @param ownUser
     * @return
     */
    public FilteredStream getItemsForAtomContext(String atomContext, User ownUser) {

        FilteredStream filteredStream = new FilteredStream();
        List<Identity> allContacts;

        allContacts = Contacts.getAllContacts();

        filteredStream.add(emptyItem);

        switch (atomContext) {

            case "aura.uxstream.launcher":
                // TODO: Query every kind of atom in a time frame, sort and convert to stream item type
                JSONArray launcherAtoms = gorillaClient.queryAtoms("aura.*", 0, 0);

                if (launcherAtoms != null) {
                    try {
                        for (int inx = 0; inx < launcherAtoms.length(); inx++) {
                            JSONObject launcherAtom = Json.getObject(launcherAtoms, inx);
                            if (launcherAtom == null) continue;
                            Log.d("#### launcherAtom type is <%s>", launcherAtom.get("type"));
                        }
                    } catch (JSONException e) {
                        Log.e("Cannot read JSON object: <%s>", e.getMessage());
                        e.printStackTrace();
                    }
                }

                break;

            case "aura.uxstream.launcher.messages":

                // TODO: Hier weiter - glatt ziehen!
                String atomType = GorillaHelper.atomTypes.get("chatMessage");

                for (Identity contactIdentity : allContacts) {

                    String remoteNick = contactIdentity.getNick();
                    String remoteUserUUID = contactIdentity.getUserUUIDBase64();
                    String remoteDeviceUUID = contactIdentity.getDeviceUUIDBase64();

//                    String actionDomain = getContext().getPackageName();
//                    String subAction = "chat=" + remoteUserUUID;
//                    gorillaClient.registerActionEventDomain(actionDomain, subAction);

                    JSONArray recv = gorillaClient.queryAtomsSharedBy(remoteUserUUID, atomType, 0, 0);
                    JSONArray send = gorillaClient.queryAtomsSharedWith(remoteUserUUID, atomType, 0, 0);

                    if (recv != null)
                    {
                        for (int inx = 0; inx < recv.length(); inx++)
                        {
                            GorillaMessage gorillaMessage = new GorillaMessage(Json.getObject(recv, inx));
                            filteredStream.add(new MessageStreamItem(gorillaMessage));

                            Log.d(LOGTAG, "recv=" + gorillaMessage.toPretty());
                        }
                    }

                    if (send != null)
                    {
                        for (int inx = 0; inx < send.length(); inx++)
                        {
                            GorillaMessage gorillaMessage = new GorillaMessage(Json.getObject(send, inx));
                            filteredStream.add(new MessageStreamItem(gorillaMessage));

                            Log.d(LOGTAG, "send=" + gorillaMessage.toPretty());
                        }
                    }
                }

//                combined = Json.sortNumber(combined, "sort_", false);
                filteredStream.sort(new Comparator<StreamItem>() {
                    @Override
                    public int compare(StreamItem o1, StreamItem o2) {
                        return Simple.compareTo(o1.getCreateTime(), o2.getCreateTime());
                    }
                });

                break;

            case "aura.uxstream.launcher.contacts":

                for (Identity contactIdentity : allContacts) {

//                    if (contactIdentity.equals(ownUser.getIdentity())) {
//                        continue;
//                    }

                    User contactUser = new User(contactIdentity);

                    ContactStreamItem contactStreamItem = new ContactStreamItem(ownUser, contactUser);
                    contactStreamItem.setAbsoluteScore(1f);

                    filteredStream.add(contactStreamItem);
                }

                break;
        }

        filteredStream.add(emptyItem);

        return filteredStream;
    }

    public void getSuggestions(String atomContext) {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
