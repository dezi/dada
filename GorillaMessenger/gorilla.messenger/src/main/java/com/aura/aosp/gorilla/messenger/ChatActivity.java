package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIEditText;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUIIconView;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIScrollView;
import com.aura.aosp.gorilla.client.GorillaClient;

import org.json.JSONObject;

public class ChatActivity extends AppCompatActivity
{
    private static final String LOGTAG = ChatActivity.class.getSimpleName();

    private GUIScrollView contentScroll;
    private GUILinearLayout chatContent;
    private GUIEditText editText;
    private GUIIconView sendButton;

    private ChatProfile chatProfile;
    private String remoteNick;
    private String remoteUserUUID;
    private String remoteDeviceUUID;

    private Boolean svlink;
    private Boolean uplink;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d(LOGTAG, "onCreate: started.....");

        createLayout();

        Intent intent = getIntent();
        if (intent == null) return;

        Bundle params = intent.getExtras();
        if (params == null) return;

        remoteNick = params.getString("nick");
        remoteUserUUID = params.getString("userUUID");
        remoteDeviceUUID = params.getString("deviceUUID");

        chatProfile = new ChatProfile(this, remoteNick, remoteUserUUID, remoteDeviceUUID);

        MainActivity.addChatProfile(chatProfile);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        MainActivity.delChatProfile(chatProfile);

        Log.d(LOGTAG, "onDestroy: ended.....");
    }

    private void createLayout()
    {
        GUIFrameLayout topFrame = new GUIFrameLayout(this);

        setContentView(topFrame);

        GUILinearLayout centerFrame = new GUILinearLayout(this);
        centerFrame.setSizeDip(Simple.MP, Simple.MP);
        centerFrame.setOrientation(LinearLayout.VERTICAL);

        topFrame.addView(centerFrame);

        contentScroll = new GUIScrollView(this);
        contentScroll.setSizeDip(Simple.MP, Simple.MP, 1.0f);
        contentScroll.setBackgroundColor(0x88880000);

        centerFrame.addView(contentScroll);

        chatContent = new GUILinearLayout(this);
        chatContent.setOrientation(LinearLayout.VERTICAL);

        contentScroll.addView(chatContent);

        GUILinearLayout bottomBox = new GUILinearLayout(this);
        bottomBox.setSizeDip(Simple.MP, Simple.WC);
        bottomBox.setOrientation(LinearLayout.HORIZONTAL);
        bottomBox.setPaddingDip(GUIDefs.PADDING_SMALL);
        bottomBox.setBackgroundColor(0x88008800);

        centerFrame.addView(bottomBox);

        editText = new GUIEditText(this);
        editText.setSizeDip(Simple.WC, Simple.WC, 1.0f);
        editText.setText("schniddel more lore ipsum");

        bottomBox.addView(editText);

        sendButton = new GUIIconView(this);
        sendButton.setImageResource(R.drawable.human_260);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = editText.getText().toString();

                JSONObject result = GorillaClient.getInstance().sendPayload(chatProfile.remoteUserUUID, chatProfile.remoteDeviceUUID, message);
                if (result == null) return;

                Long time = Json.getLong(result,"time");
                String uuid = Json.getString(result,"uuid");

                editText.setText("");

                ChatFragment cf = new ChatFragment(view.getContext());
                cf.setContent(true, uuid, Dates.getLocalDateAndTime(time), MainActivity.ownerIdent.getNick(), null, message);
                chatContent.addView(cf);

                scrollDown();
            }
        });

        bottomBox.addView(sendButton);
    }

    public void setStatus(Boolean svlink, Boolean uplink)
    {
        if (uplink != null) this.uplink = uplink;
        if (svlink != null) this.svlink = svlink;
    }

    public void updateTitle()
    {
        String newtitle = remoteNick;

        if (svlink) newtitle += " (system)";
        if (uplink) newtitle += " (online)";

        setTitle(newtitle);
    }

    public void dispatchMessage(JSONObject message)
    {
        Log.d(LOGTAG, "dispatchMessage: message=" + message);

        Long time = Json.getLong(message, "time");
        String uuid = Json.getString(message, "uuid");
        String text = Json.getString(message, "payload");

        ChatFragment cf = new ChatFragment(this);
        cf.setContent(false, uuid, Dates.getLocalDateAndTime(time), chatProfile.remoteNick, null, text);
        chatContent.addView(cf);

        scrollDown();
    }

    public void dispatchStatus(JSONObject result)
    {
        String uuid = Json.getString(result, "uuid");
        String status = Json.getString(result, "status");

        if ((uuid == null) || (status == null)) return;

        Log.d(LOGTAG, "dispatchStatus: uuid=" + uuid + " status=" + status + " childCount=" + chatContent.getChildCount());

        for (int cinx = 0; cinx < chatContent.getChildCount(); cinx++)
        {
            View child = chatContent.getChildAt(cinx);
            if (! (child instanceof ChatFragment)) continue;

            ChatFragment cf = (ChatFragment) child;

            Log.d(LOGTAG, "dispatchStatus: child uuid=" + cf.getMessageUUID());

            if (! uuid.equals(cf.getMessageUUID())) continue;

            cf.setStatusIcon(status);
        }
    }

    private void scrollDown()
    {
        contentScroll.post(new Runnable()
        {
            @Override
            public void run()
            {
                contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}

