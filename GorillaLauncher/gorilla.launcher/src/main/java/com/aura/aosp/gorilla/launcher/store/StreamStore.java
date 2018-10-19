package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;

import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.InvisibleStreamItem;
import com.aura.aosp.gorilla.launcher.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class StreamStore {

    public static String EXTRA_CHAT_PARTNER_UUID = "com.aura.aosp.gorilla.launcher.chat.partner_uuid";

    private static final String LOGTAG = StreamStore.class.getSimpleName();
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
    public List<StreamItem> getItemsForAtomContext(String atomContext, User ownUser) {

        List<StreamItem> items = new ArrayList<>();

        items.add(emptyItem);

        switch (atomContext) {
            case "aura.uxtream.launcher":
            case "aura.uxtream.launcher.contacts":

                List<Identity> allContacts = Contacts.getAllContacts();

                for (Identity contactIdentity : allContacts) {

//                    if (contactIdentity.equals(ownUser.getIdentity())) {
//                        continue;
//                    }

                    User contactUser = new User(contactIdentity);

                    ContactStreamItem contactStreamItem = new ContactStreamItem(ownUser, contactUser);
                    contactStreamItem.setAbsoluteScore(1f);

                    items.add(contactStreamItem);
                }
        }

        items.add(emptyItem);

        return items;
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
