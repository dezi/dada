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

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: started.....");

        Simple.initialize(this.getApplication());

        createLayout();

        GorillaClient gc = GorillaClient.getInstance();

        gc.setOnResultReceivedListener(new GorillaClient.OnResultReceivedListener()
        {
            @Override
            public void onResultReceived(JSONObject result)
            {
                Log.d(LOGTAG, "onResultReceived: result=" + result.toString());
            }
        });

        gc.setOnOwnerReceivedListener(new GorillaClient.OnOwnerReceivedListener()
        {
            @Override
            public void onOwnerReceived(JSONObject owner)
            {
                Log.d(LOGTAG, "onOwnerReceived: owner=" + owner.toString());

                String ownerUUID = Json.getString(owner, "ownerUUID");
                Identity ownerIdent = Contacts.getContact(ownerUUID);
                if (ownerIdent == null) return;

                Log.d(LOGTAG, "ownerIdent=" + ownerIdent.toString());

                String title = getTitle() + " " + ownerIdent.getNick();
                setTitle(title);
            }
        });

        gc.setOnMessageReceivedListener(new GorillaClient.OnMessageReceivedListener()
        {
            @Override
            public void onMessageReceived(JSONObject message)
            {
                Log.d(LOGTAG, "onOwnerReceived: message=" + message.toString());
            }
        });

        gc.wantOwner(this);

        //gc.sendPayload(this,"r0Z7g7cnTF6Mi5/NRyU4Yw==", "lfTBPb1qQ9akd3ltWLWxaw==", "tubu");
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
                    Identity identity = (Identity) view.getTag();

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                    Bundle params = new Bundle();
                    params.putString("nick", identity.getNick());
                    params.putString("userUUID", identity.getUserUUIDBase64());
                    params.putString("deviceUUID", identity.getDeviceUUIDBase64());
                    intent.putExtras(params);

                    startActivity(intent);
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
