package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Contacts;
import com.aura.aosp.aura.common.univid.Identity;

import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIListEntry;
import com.aura.aosp.aura.gui.views.GUIListView;
import com.aura.aosp.aura.gui.views.GUIScrollView;

import com.aura.aosp.gorilla.client.GorillaClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    public static Identity ownerIdent;

    public static List<ChatProfile> chatProfiles = new ArrayList<>();
    private static boolean svlink;
    private static boolean uplink;

    public static void addChatProfile(ChatProfile chatProfile)
    {
        chatProfile.activity.setStatus(svlink, uplink);
        chatProfile.activity.updateTitle();

        chatProfiles.add(chatProfile);
    }

    public static void delChatProfile(ChatProfile chatProfile)
    {
        chatProfiles.remove(chatProfile);
    }

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: started......");

        Simple.initialize(this.getApplication());

        createLayout();

        title = getTitle().toString();

        GorillaClient gc = GorillaClient.getInstance();

        gc.setOnStatusReceivedListener(new GorillaClient.OnStatusReceivedListener()
        {
            @Override
            public void onStatusReceived(JSONObject status)
            {
                Log.d(LOGTAG, "onStatusReceived: status=" + status.toString());

                svlink = Json.getBoolean(status, "svlink");
                uplink = Json.getBoolean(status, "uplink");

                updateTitle();

                for (ChatProfile chatProfile : chatProfiles)
                {
                    chatProfile.activity.setStatus(svlink, uplink);
                    chatProfile.activity.updateTitle();
                }
            }
        });

        gc.setOnOwnerReceivedListener(new GorillaClient.OnOwnerReceivedListener()
        {
            @Override
            public void onOwnerReceived(JSONObject owner)
            {
                Log.d(LOGTAG, "onOwnerReceived: owner=" + owner.toString());

                String ownerUUID = Json.getString(owner, "ownerUUID");

                ownerIdent = Contacts.getContact(ownerUUID);

                updateTitle();

                for (ChatProfile chatProfile : chatProfiles)
                {
                    chatProfile.activity.finish();
                }
            }
        });

        gc.setOnMessageReceivedListener(new GorillaClient.OnMessageReceivedListener()
        {
            @Override
            public void onMessageReceived(JSONObject message)
            {
                Log.d(LOGTAG, "onMessageReceived: message=" + message.toString());

                String remoteUserUUID = Json.getString(message, "sender");
                String remoteDeviceUUID = Json.getString(message, "device");

                for (ChatProfile chatProfile : chatProfiles)
                {
                    if (! chatProfile.remoteUserUUID.equals(remoteUserUUID)) continue;
                    if (! chatProfile.remoteDeviceUUID.equals(remoteDeviceUUID)) continue;

                    chatProfile.activity.dispatchMessage(message);
                    break;
                }
            }
        });

        gc.setOnResultReceivedListener(new GorillaClient.OnResultReceivedListener()
        {
            @Override
            public void onResultReceived(JSONObject result)
            {
                Log.d(LOGTAG, "onResultReceived: result=" + result.toString());
            }
        });

        gc.bindGorillaService(this);
    }

    private void updateTitle()
    {
        String newtitle = title;

        if (ownerIdent != null) newtitle += " " + ownerIdent.getNick();

        if (svlink) newtitle += " (system)";
        if (uplink) newtitle += " (online)";

        setTitle(newtitle);
    }

    private void createLayout()
    {
        GUIFrameLayout topFrame = new GUIFrameLayout(this);

        setContentView(topFrame);

        GUILinearLayout centerFrame = new GUILinearLayout(this);
        centerFrame.setSizeDip(Simple.MP, Simple.MP);
        centerFrame.setOrientation(LinearLayout.VERTICAL);
        centerFrame.setGravity(Gravity.START);

        topFrame.addView(centerFrame);

        GUIScrollView identitiesScroll = new GUIScrollView(this);
        identitiesScroll.setSizeDip(Simple.MP, Simple.MP, 1.0f);

        centerFrame.addView(identitiesScroll);

        GUIListView identitiesView = new GUIListView(this);
        identitiesView.setSizeDip(Simple.MP, Simple.MP, 1.0f);
        identitiesView.setBackgroundColor(0x88888888);

        identitiesScroll.addView(identitiesView);

        List<Identity> contacts = Contacts.getAllContacts();

        for (Identity identity : contacts)
        {
            String nick = identity.getNick();
            String info = identity.getUserUUIDBase64();

            GUIListEntry entry = identitiesView.findGUIListEntryOrCreate(identity.getUserUUIDBase64());

            entry.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (ownerIdent == null)
                    {
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp");

                        startActivity(launchIntent);
                    }
                    else
                    {
                        Identity identity = (Identity) view.getTag();

                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                        Bundle params = new Bundle();
                        params.putString("nick", identity.getNick());
                        params.putString("userUUID", identity.getUserUUIDBase64());
                        params.putString("deviceUUID", identity.getDeviceUUIDBase64());
                        intent.putExtras(params);

                        startActivity(intent);
                    }
                }
            });

            entry.setTag(identity);

            entry.iconView.setImageResource(R.drawable.human_260);
            entry.headerViev.setText(nick);
            entry.infoView.setText(info);
            entry.actionIcon.setImageResource(R.drawable.arrow_right);
        }
    }
}
