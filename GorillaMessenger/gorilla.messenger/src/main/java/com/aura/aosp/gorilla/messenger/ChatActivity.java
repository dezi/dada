package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

        String remoteNick = params.getString("nick");
        String remoteUserUUID = params.getString("userUUID");
        String remoteDeviceUUID = params.getString("deviceUUID");

        chatProfile = new ChatProfile(this, remoteNick, remoteUserUUID, remoteDeviceUUID);

        MainActivity.addChatProfile(chatProfile);

        setTitle(remoteNick);

        //dummyMessages();
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

        bottomBox.addView(editText);

        sendButton = new GUIIconView(this);
        sendButton.setImageResource(R.drawable.human_260);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String message = editText.getText().toString();
                editText.setText("");

                ChatFragment cf = new ChatFragment(view.getContext());
                cf.setContent(true, "20181812123456", MainActivity.ownerIdent.getNick(), null, message);
                chatContent.addView(cf);

                scrollDown();

                GorillaClient.getInstance().sendPayload(chatProfile.remoteUserUUID, chatProfile.remoteDeviceUUID, message);
            }
        });

        bottomBox.addView(sendButton);
    }

    public void dispatchMessage(JSONObject message)
    {
        Log.d(LOGTAG, "dispatchMessage: message=" + message);

        String text = Json.getString(message, "payload");

        ChatFragment cf = new ChatFragment(this);
        cf.setContent(false, "20181812123456", chatProfile.remoteNick, null, text);
        chatContent.addView(cf);

        scrollDown();
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

