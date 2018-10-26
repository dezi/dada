package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.client.GorillaClient;
import com.aura.aosp.gorilla.launcher.model.GorillaHelper;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.InvisibleStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.MessageStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class StreamStore {

    public static String EXTRA_CHAT_PARTNER_UUID = "com.aura.aosp.gorilla.launcher.chat.partner_uuid";

    public static final String ATOMCONTEXT_UXSTREAM_START = "aura.uxstream.start";
    public static final String ATOMCONTEXT_UXSTREAM_MESSAGES = "aura.uxstream.messages";
    public static final String ATOMCONTEXT_UXSTREAM_CONTACTS = "aura.uxstream.contacts";

    private static final String LOGTAG = StreamStore.class.getSimpleName();

    private GorillaClient gorillaClient = GorillaClient.getInstance();
    private Context context;

    public StreamStore(Context context) {
        this.setContext(context);
    }

    /**
     * Get content stream items for given atom context
     *
     * @param atomContext
     * @param myUser
     * @return
     */
    public FilteredStream getItemsForAtomContext(String atomContext, User myUser) {

        FilteredStream filteredStream = new FilteredStream();
        List<Identity> allContacts;

        final InvisibleStreamItem emptyItem = new InvisibleStreamItem(myUser, myUser);

        allContacts = Contacts.getAllContacts();

        switch (atomContext) {

            case ATOMCONTEXT_UXSTREAM_START:
                // TODO: Query every kind of atom in a time frame, sort and convert to stream item type
                JSONArray launcherAtoms = gorillaClient.queryAtoms("aura.*", 0, 0);

                if (launcherAtoms != null) {
                    try {
                        for (int inx = 0; inx < launcherAtoms.length(); inx++) {
                            JSONObject launcherAtom = Json.getObject(launcherAtoms, inx);
                            if (launcherAtom == null) continue;
                            Log.d("launcherAtom type is <%s>", launcherAtom.get("type"));
                        }
                    } catch (JSONException e) {
                        Log.e("Cannot read JSON object: <%s>", e.getMessage());
                        e.printStackTrace();
                    }
                }

                break;

            case ATOMCONTEXT_UXSTREAM_MESSAGES:

                String atomType = GorillaHelper.ATOMTYPE_CHAT_MESSAGE;

                for (Identity contactIdentity : allContacts) {

                    String remoteUserUUID = contactIdentity.getUserUUIDBase64();
//                    String remoteDeviceUUID = contactIdentity.getDeviceUUIDBase64();

                    User contactUser = new User(contactIdentity);

                    // TODO: Use this fragment to write certain actions (e.g. on stream filtering to specific chat)
//                    String actionDomain = getContext().getPackageName();
//                    String subAction = "chat=" + remoteUserUUID;
//                    gorillaClient.registerActionEventDomain(actionDomain, subAction);

                    JSONArray recvMessages = gorillaClient.queryAtomsSharedBy(remoteUserUUID, atomType, 0, 0);
                    JSONArray sentMessages = gorillaClient.queryAtomsSharedWith(remoteUserUUID, atomType, 0, 0);

                    if (recvMessages != null)
                    {
                        for (int inx = 0; inx < recvMessages.length(); inx++)
                        {
                            GorillaMessage gorillaMessage = new GorillaMessage(Json.getObject(recvMessages, inx));
                            MessageStreamItem messageStreamItem = new MessageStreamItem(myUser, contactUser, gorillaMessage);
                            messageStreamItem.setSharedWithUser(myUser);
                            filteredStream.add(messageStreamItem);
//                            Log.d("recv=" + gorillaMessage.toPretty());
                        }
                    }

                    if (sentMessages != null)
                    {
                        for (int inx = 0; inx < sentMessages.length(); inx++)
                        {
                            GorillaMessage gorillaMessage = new GorillaMessage(Json.getObject(sentMessages, inx));
                            MessageStreamItem messageStreamItem = new MessageStreamItem(myUser, myUser, gorillaMessage);
                            messageStreamItem.setSharedWithUser(contactUser);
                            filteredStream.add(messageStreamItem);
//                            Log.d("send=" + gorillaMessage.toPretty());
                        }
                    }
                }

                filteredStream.sortyByCreateTime(true);

                break;

            case ATOMCONTEXT_UXSTREAM_CONTACTS:

                for (Identity contactIdentity : allContacts) {

//                    if (contactIdentity.getUserUUIDBase64().equals(myUser.getIdentity().getUserUUIDBase64())) {
//                        continue;
//                    }

                    User contactUser = new User(contactIdentity);

                    ContactStreamItem contactStreamItem = new ContactStreamItem(myUser, myUser, contactUser);
                    contactStreamItem.setAbsoluteScore(1f);

                    filteredStream.add(contactStreamItem);
                }

                break;
        }

//        filteredStream.add(emptyItem);

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
