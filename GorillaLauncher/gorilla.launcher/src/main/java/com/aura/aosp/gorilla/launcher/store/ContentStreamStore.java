package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;

import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.ContentStreamItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class ContentStreamStore {

    public static String EXTRA_CHAT_PARTNER_UUID = "com.aura.aosp.gorilla.launcher.chat.partner_uuid";

    private static final String LOGTAG = ContentStreamStore.class.getSimpleName();
    private Context context;

    public ContentStreamStore(Context context) {
        this.setContext(context);
    }

    public static final ContentStreamItem emptyItem = new ContentStreamItem("transparent", "", R.drawable.contentstream_oval_transparent_24dp);

    /**
     * Get content stream items for given atom context
     *
     * @param atomContext
     * @return
     */
    public List<ContentStreamItem> getItemsForAtomContext(String atomContext) {

        List<ContentStreamItem> items = new ArrayList<>();

        items.add(emptyItem);

        switch (atomContext) {
            case "aura.uxtream.launcher":
            case "aura.uxtream.launcher.contacts":

                List<Identity> allContacts = Contacts.getAllContacts();

                for (Identity contact : allContacts) {

                    items.add(new ContentStreamItem(
                            "contact",
                            contact.getNick(),
                            R.drawable.contentstream_oval_24dp));

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

    /**
     * Get content stream for given
     *
     * @return
     */
    public final static List<ContentStreamItem> getDummyStreamData() {
        List<ContentStreamItem> items = new ArrayList<>();

        items.add(new ContentStreamItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "",
                "",
                0));

        items.add(new ContentStreamItem(
                "highlight",
                "Abi",
                "I need some really holistic...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Andreas",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "Here's your account data...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Caroline",
                "I need some really holistic...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Mr. Hoi",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nilie",
                "Here's your account data...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "How are you. Please send me some...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Ola",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Abi",
                "I need some really universal...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Matthias",
                "Was?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "Here's your license accoutn data...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "",
                "",
                0));

        items.add(new ContentStreamItem(
                "highlight",
                "Abi",
                "I need some really holistic...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Andreas",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "Here's your account data...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Caroline",
                "I need some really holistic...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Mr. Hoi",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nilie",
                "Here's your account data...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "How are you. Please send me some...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Ola",
                "Howdy, here are the files...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Abi",
                "I need some really universal...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new ContentStreamItem(
                "highlight",
                "Matthias",
                "Was?",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Laurie",
                "Here's your license accoutn data...",
                R.drawable.contentstream_oval_24dp));

        items.add(new ContentStreamItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        return items;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
