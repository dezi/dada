package com.aura.aosp.gorilla.sysapp;

import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.aura.aosp.aura.crypter.RND;
import com.aura.aosp.aura.simple.Simple;
import com.aura.aosp.aura.univid.Contacts;
import com.aura.aosp.aura.univid.Identity;
import com.aura.aosp.aura.univid.Owner;

import com.aura.aosp.gui.base.GUIDefs;
import com.aura.aosp.gui.views.GUIButtonView;
import com.aura.aosp.gui.views.GUIFrameLayout;
import com.aura.aosp.gui.views.GUILinearLayout;
import com.aura.aosp.gui.views.GUIListEntry;
import com.aura.aosp.gui.views.GUIListView;
import com.aura.aosp.gui.views.GUIScrollView;
import com.aura.aosp.gui.views.GUITextView;

import com.aura.aosp.gorilla.gomess.GomessHandler;
import com.aura.aosp.gorilla.R;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String LOGTAG = MainActivity.class.getSimpleName();

    private GUITextView identview;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate: activity started.");

        super.onCreate(savedInstanceState);

        createLayout();

        //sendMessage();
    }

    private void sendMessage()
    {
        Identity owner = Owner.getOwnerIdentity();
        if (owner == null) return;

        long time = System.currentTimeMillis();
        String uuid = RND.randomUUIDBase64();

        String apkname = getPackageName();
        String userUUID = owner.getUserUUIDBase64();
        String deviceUUID = owner.getDeviceUUIDBase64();
        String payload = "Huhu";

        JSONObject result = GomessHandler.getInstance().sendPayload(uuid, time, apkname, userUUID, deviceUUID, payload);
        Log.d(LOGTAG, "sendPayloadTest: result=" + result.toString());
    }

    private void createLayout()
    {
        GUIFrameLayout topFrame = new GUIFrameLayout(this);

        setContentView(topFrame);

        GUILinearLayout centerFrame = new GUILinearLayout(this);
        centerFrame.setOrientation(LinearLayout.VERTICAL);
        centerFrame.setGravity(Gravity.CENTER_HORIZONTAL);

        topFrame.addView(centerFrame);

        GUITextView titleView = new GUITextView(this);
        titleView.setText(R.string.select_identity);
        titleView.setSizeDip(Simple.WC, Simple.WC);
        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleView.setPaddingDip(GUIDefs.PADDING_NORMAL);
        titleView.setTextSizeDip(32);

        centerFrame.addView(titleView);

        GUIScrollView identitiesScroll = new GUIScrollView(this);
        identitiesScroll.setSizeDip(Simple.WC, Simple.MP, 1.0f);

        centerFrame.addView(identitiesScroll);

        GUIListView identitiesView = new GUIListView(this);
        identitiesView.setSizeDip(Simple.WC, Simple.MP, 1.0f);
        identitiesView.setBackgroundColor(0x88888888);

        identitiesScroll.addView(identitiesView);

        String ownerUUID = Owner.getOwnerUUIDBase64();

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
                    identview.setText(identity.getNick());

                    Owner.setOwnerUUIDBase64(identity.getUserUUIDBase64());
                    GomessHandler.getInstance().resetSession();
                }
            });

            entry.setTag(identity);

            entry.iconView.setImageResource(R.drawable.human_260);
            entry.headerViev.setText(nick);
            entry.infoView.setText(info);
        }

        identview = new GUITextView(this);
        identview.setSizeDip(Simple.WC, Simple.WC);
        identview.setPaddingDip(GUIDefs.PADDING_NORMAL);
        identview.setTextSizeDip(48);

        if (ownerUUID != null)
        {
            for (Identity identity : contacts)
            {
                if (identity.getUserUUIDBase64().equals(ownerUUID))
                {
                    identview.setText(identity.getNick());
                }
            }
        }

        centerFrame.addView(identview);

        GUIButtonView doneButton = new GUIButtonView(this);

        doneButton.setRoundedCorners(GUIDefs.ROUNDED_NORMAL, GUIDefs.COLOR_LIGHT_GRAY);
        doneButton.setText(R.string.done_button);
        doneButton.setSizeDip(Simple.WC, Simple.WC);
        doneButton.setMarginBottomDip(GUIDefs.PADDING_NORMAL);

        doneButton.setPaddingDip(
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_NORMAL,
                GUIDefs.PADDING_XLARGE, GUIDefs.PADDING_NORMAL);

        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MainActivity.this.finish();
            }
        });

        centerFrame.addView(doneButton);
    }
}
