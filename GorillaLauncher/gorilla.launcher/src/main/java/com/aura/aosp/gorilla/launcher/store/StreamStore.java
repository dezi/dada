package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;

import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.model.StreamItem;
import com.aura.aosp.gorilla.launcher.model.StreamItemContact;
import com.aura.aosp.gorilla.launcher.model.StreamItemInvisible;
import com.aura.aosp.gorilla.launcher.model.StreamItemGeneric;

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

    public static final StreamItemInvisible emptyItem = new StreamItemInvisible();

    /**
     * Get content stream items for given atom context
     *
     * @param atomContext
     * @param ownIdentity
     * @return
     */
    public List<StreamItem> getItemsForAtomContext(String atomContext, Identity ownIdentity) {

        List<StreamItem> items = new ArrayList<>();

        items.add(emptyItem);

        switch (atomContext) {
            case "aura.uxtream.launcher":
            case "aura.uxtream.launcher.contacts":

                List<Identity> allContacts = Contacts.getAllContacts();

                for (Identity identity : allContacts) {

                    if (identity.equals(ownIdentity)) {
                        continue;
                    }

                    items.add(new StreamItemContact(ownIdentity, identity));

//                    Intent chatIntent = new Intent(atomContext)
//                    Intent intent = new Intent(context, DisplayMessageActivity.class);
////                    EditText editText = (EditText) findViewById(R.id.editText);
////                    String message = editText.getText().toString();
//                    intent.putExtra(EXTRA_CHAT_PARTNER_UUID, contact.getUserUUID());
//
//                    items.add(new ActionItem(
//                            contact.getNick(),
//                            FuncBaseView.FuncType.OVERLAY,
//                            R.drawable.ic_account_circle_black_24dp,
//                            intent,
//                            0.870f
//                    ));
                }
        }

        items.add(emptyItem);

        return items;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
