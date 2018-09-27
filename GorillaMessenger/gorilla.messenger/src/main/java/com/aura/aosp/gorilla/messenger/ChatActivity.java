package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.support.annotation.Nullable;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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

        GorillaClient.getInstance().registerActionEvent(getPackageName(), remoteUserUUID);

        JSONArray recv = GorillaClient.getInstance().queryAtomsSharedBy(remoteUserUUID, "aura.chat.message", 0, 0);
        JSONArray send = GorillaClient.getInstance().queryAtomsSharedWith(remoteUserUUID, "aura.chat.message", 0, 0);

        Log.d(LOGTAG,"############ recv=" + Json.toPretty(recv));
        Log.d(LOGTAG,"############ send=" + Json.toPretty(send));

        JSONArray combined = new JSONArray();

        if (recv != null)
        {
            for (int inx = 0; inx < recv.length(); inx++)
            {
                JSONObject atom = Json.getObject(recv, inx);
                if (atom == null) continue;
                JSONObject load = Json.getObject(atom, "load");
                if (load == null) continue;

                Log.d(LOGTAG, "recv=" + Json.toPretty(atom));

                Long sort = getFirstKey(load, "received");
                if (sort == null) continue;

                Json.put(atom, "sort_", sort);
                Json.put(atom, "mode_", "recv");

                Json.put(combined, atom);
            }
        }

        if (send != null)
        {
            for (int inx = 0; inx < send.length(); inx++)
            {
                JSONObject atom = Json.getObject(send, inx);
                if (atom == null) continue;
                JSONObject load = Json.getObject(atom, "load");
                if (load == null) continue;

                Log.d(LOGTAG, "send=" + Json.toPretty(atom));

                Long sort = getFirstKey(load, "queued");
                if (sort == null) continue;

                Json.put(atom, "sort_", sort);
                Json.put(atom, "mode_", "send");

                Json.put(combined, atom);
            }
        }

        combined = Json.sortNumber(combined, "sort_", false);

        for (int inx = 0; inx < combined.length(); inx++)
        {
            JSONObject atom = Json.getObject(combined, inx);
            if (atom == null) continue;

            String mode = Json.getString(atom, "mode_");
            if (mode == null) continue;

            Json.remove(atom, "mode_");
            Json.remove(atom, "sort_");

            Log.d(LOGTAG, "####### atom=" + atom.toString());

            ChatFragment cf = new ChatFragment(this);
            cf.setContent(mode.equals("send"), chatProfile.remoteNick, atom);
            chatContent.addView(cf);
        }

        scrollDown();
    }

    @Nullable
    private Long getFirstKey(JSONObject load, String status)
    {
        JSONObject statusse = Json.getObject(load, status);
        if (statusse == null) return null;

        Iterator<String> keysIterator = statusse.keys();

        if (keysIterator.hasNext())
        {
            String key = keysIterator.next();
            return Json.getLong(statusse, key);
        }

        return null;
    }

    @Nullable
    private Long getLoadTime(JSONObject load)
    {
        Long sort = getFirstKey(load, "received");
        if (sort == null) getFirstKey(load, "queued");
        return sort;
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

                JSONObject atomLoad = new JSONObject();
                Json.put(atomLoad, "message", message);
                JSONObject substatus = new JSONObject();

                Json.put(substatus, MainActivity.getOwnerDeviceBase64(), time);
                Json.put(atomLoad, "queued", substatus);

                JSONObject atom = new JSONObject();

                Json.put(atom, "uuid", uuid);
                Json.put(atom, "time", time);
                Json.put(atom, "type", "aura.chat.message");
                Json.put(atom, "load", atomLoad);

                GorillaClient.getInstance().putAtomSharedWith(chatProfile.remoteUserUUID, atom);

                editText.setText("");

                ChatFragment cf = new ChatFragment(view.getContext());
                cf.setContent(true, MainActivity.ownerIdent.getNick(), atom);
                chatContent.addView(cf);

                scrollDown();

                dispatchResult(result);
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

        final Long time = Json.getLong(message, "time");
        final String uuid = Json.getString(message, "uuid");
        final String text = Json.getString(message, "payload");

        JSONObject atomLoad = new JSONObject();
        Json.put(atomLoad, "message", text);

        JSONObject received = new JSONObject();
        Json.put(received, MainActivity.getOwnerDeviceBase64(), System.currentTimeMillis());
        Json.put(atomLoad, "received", received);

        JSONObject atom = new JSONObject();

        Json.put(atom, "uuid", uuid);
        Json.put(atom, "time", time);
        Json.put(atom, "type", "aura.chat.message");
        Json.put(atom, "load", atomLoad);

        GorillaClient.getInstance().putAtomSharedBy(chatProfile.remoteUserUUID, atom);

        ChatFragment cf = new ChatFragment(this);
        cf.setContent(false, chatProfile.remoteNick, atom);
        chatContent.addView(cf);

        scrollDown();

        chatContent.getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                GorillaClient.getInstance().sendPayloadRead(remoteUserUUID, remoteDeviceUUID, uuid);
            }

        }, 1000);
    }

    public void dispatchResult(JSONObject result)
    {
        Long time = Json.getLong(result, "time");
        String uuid = Json.getString(result, "uuid");
        String status = Json.getString(result, "status");

        if ((uuid == null) || (status == null)) return;

        Log.d(LOGTAG, "dispatchResult: uuid=" + uuid + " status=" + status + " childCount=" + chatContent.getChildCount());

        for (int cinx = 0; cinx < chatContent.getChildCount(); cinx++)
        {
            View child = chatContent.getChildAt(cinx);
            if (! (child instanceof ChatFragment)) continue;

            ChatFragment cf = (ChatFragment) child;

            if (! cf.isSendFragment()) continue;
            if (!uuid.equals(cf.getMessageUUID())) continue;

            Log.d(LOGTAG, "dispatchResult: ###### child uuid=" + cf.getMessageUUID() + " status=" + status);

            cf.setStatusIcon(status, time);
            JSONObject atom = cf.getAtom();
            if (atom == null) continue;

            GorillaClient.getInstance().putAtomSharedWith(remoteUserUUID, atom);
        }
    }

    private void scrollDown()
    {
        contentScroll.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 500);
    }
}

