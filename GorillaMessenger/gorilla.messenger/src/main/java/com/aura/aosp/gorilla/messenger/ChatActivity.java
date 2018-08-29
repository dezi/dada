package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIEditText;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUIIconView;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIScrollView;

public class ChatActivity extends AppCompatActivity
{
    private GUIScrollView contentScroll;
    private GUILinearLayout chatContent;
    private GUIEditText editText;
    private GUIIconView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        createLayout();

        Intent intent = getIntent();
        if (intent == null) return;

        Bundle params = intent.getExtras();
        if (params == null) return;

        String nick = params.getString("nick");

        setTitle(nick);
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

        ChatFragment cf = new ChatFragment(this);
        cf.setContentInfo("Huhu");

        chatContent.addView(cf);

        cf = new ChatFragment(this);
        cf.setContent(true, "20181812123456", "dezi", null, "Hdkjsafhs fdsf sdf dsf dsf dsf dsf dsf ds");

        chatContent.addView(cf);

        cf = new ChatFragment(this);
        cf.setContent(false, "20181812125656", "patrick", null, "Huhu wie gehts");

        chatContent.addView(cf);

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

            }
        });

        bottomBox.addView(sendButton);
    }
}

