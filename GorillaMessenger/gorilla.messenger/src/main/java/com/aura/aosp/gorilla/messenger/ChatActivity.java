package com.aura.aosp.gorilla.messenger;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aura.aosp.aura.common.simple.Json;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIEditText;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUIIconView;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIRelativeLayout;
import com.aura.aosp.aura.gui.views.GUIScrollView;
import com.aura.aosp.aura.gui.views.GUITextView;
import com.aura.aosp.gorilla.atoms.GorillaMessage;
import com.aura.aosp.gorilla.atoms.GorillaPayloadResult;
import com.aura.aosp.gorilla.atoms.GorillaPhraseSuggestion;
import com.aura.aosp.gorilla.atoms.GorillaPhraseSuggestionHint;
import com.aura.aosp.gorilla.client.GorillaClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class ChatActivity extends AppCompatActivity
{
    private static final String LOGTAG = ChatActivity.class.getSimpleName();

    public static ChatActivity activeChat;

    private GUIScrollView contentScroll;
    private GUILinearLayout chatContent;
    private GUIEditText editText;
    private GUIIconView sendButton;
    private GUITextView[] suggestTexts = new GUITextView[ 5 ];

    private ChatProfile chatProfile;
    private String remoteNick;
    private String remoteUserUUID;
    private String remoteDeviceUUID;

    private Handler handler;
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

        handler = new Handler();

        remoteNick = params.getString("nick");
        remoteUserUUID = params.getString("userUUID");
        remoteDeviceUUID = params.getString("deviceUUID");

        chatProfile = new ChatProfile(this, remoteNick, remoteUserUUID, remoteDeviceUUID);

        MainActivity.addChatProfile(chatProfile);

        String actionDomain = getPackageName();
        String subAction = "chat=" + remoteUserUUID;

        GorillaClient.getInstance().registerActionEventDomain(actionDomain, subAction);

        JSONArray recv = GorillaClient.getInstance().queryAtomsSharedBy(remoteUserUUID, "aura.chat.message", 0, 0);
        JSONArray send = GorillaClient.getInstance().queryAtomsSharedWith(remoteUserUUID, "aura.chat.message", 0, 0);

        //Log.d(LOGTAG,"############ recv=" + Json.toPretty(recv));
        //Log.d(LOGTAG,"############ send=" + Json.toPretty(send));

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

                Long sort = getLoadTime(load, "received");
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

                Long sort = getLoadTime(load, "queued");
                if (sort == null) continue;

                Json.put(atom, "sort_", sort);
                Json.put(atom, "mode_", "send");

                Json.put(combined, atom);
            }
        }

        combined = Json.sortNumber(combined, "sort_", false);

        for (int inx = 0; inx < combined.length(); inx++)
        {
            final JSONObject atom = Json.getObject(combined, inx);
            if (atom == null) continue;

            final String uuid = Json.getString(atom, "uuid");
            if (uuid == null) continue;

            String mode = Json.getString(atom, "mode_");
            if (mode == null) continue;

            Json.remove(atom, "mode_");
            Json.remove(atom, "sort_");

            final GorillaMessage message = new GorillaMessage(atom);

            ChatFragment cf = new ChatFragment(this);
            chatContent.addView(cf);

            if (mode.equals("send"))
            {
                cf.setContent(true, EventManager.getOwnerNick(), message);
            }
            else
            {
                cf.setContent(false, chatProfile.remoteNick, message);

                Long readStatus = message.getStatusTime("read");

                if (readStatus == null)
                {
                    String ownerDeviceUUID = EventManager.getOwnerDeviceBase64();

                    if (ownerDeviceUUID != null)
                    {
                        message.setStatusTime("read", ownerDeviceUUID, System.currentTimeMillis());
                    }

                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            boolean ok = GorillaClient.getInstance().sendPayloadRead(remoteUserUUID, remoteDeviceUUID, uuid);

                            if (ok)
                            {
                                GorillaClient.getInstance().putAtomSharedBy(remoteUserUUID, message.getAtom());
                            }
                        }

                    }, 1000);
                }
            }
        }

        scrollDown();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        MainActivity.delChatProfile(chatProfile);

        Log.d(LOGTAG, "onDestroy: ended.....");
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.d(LOGTAG, "onResume: ...");

        activeChat = this;
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        Log.d(LOGTAG, "onPause: ...");
    }

    @Nullable
    public static Long getLoadTime(JSONObject load, String status)
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
        editText.setText("");

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                Log.d(LOGTAG, "onTextChanged: <" + s.toString() + ">");

                postSuggestRequest(s.toString(), 100);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });

        bottomBox.addView(editText);

        sendButton = new GUIIconView(this);
        sendButton.setImageResource(R.drawable.human_260);

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String messageText = editText.getText().toString();

                GorillaPayloadResult result = GorillaClient.getInstance().sendPayload(chatProfile.remoteUserUUID, chatProfile.remoteDeviceUUID, messageText);
                if (result == null) return;

                Long time = result.getTime();
                if (time == null) return;

                String uuid = result.getUUIDBase64();
                if (uuid == null) return;

                GorillaMessage message = new GorillaMessage();

                message.setUUID(uuid);
                message.setTime(time);
                message.setType("aura.chat.message");
                message.setMessageText(messageText);

                GorillaClient.getInstance().putAtomSharedWith(chatProfile.remoteUserUUID, message.getAtom());

                editText.setText("");

                ChatFragment cf = new ChatFragment(view.getContext());
                cf.setContent(true, EventManager.getOwnerNick(), message);
                chatContent.addView(cf);

                scrollDown();

                dispatchResult(result);
            }
        });

        bottomBox.addView(sendButton);

        GUIRelativeLayout suggestCenter = new GUIRelativeLayout(this);
        suggestCenter.setSizeDip(Simple.MP, Simple.WC);
        suggestCenter.setGravity(Gravity.CENTER_HORIZONTAL);

        centerFrame.addView(suggestCenter);

        GUILinearLayout suggestBox = new GUILinearLayout(this);
        suggestBox.setSizeDip(Simple.WC, Simple.WC);
        suggestBox.setOrientation(LinearLayout.HORIZONTAL);
        suggestBox.setPaddingDip(GUIDefs.PADDING_SMALL);

        suggestCenter.addView(suggestBox);

        for (int inx = 0; inx < suggestTexts.length; inx++)
        {
            GUITextView suggestText = new GUITextView(this);

            suggestText.setSizeDip(Simple.WC, Simple.WC);
            suggestText.setBackgroundColor(0xffffff00);
            suggestText.setTextSizeDip(24);
            suggestText.setPaddingDip(0, 4, 0, 4);
            suggestText.setMarginLeftDip(4);
            suggestText.setMarginRightDip(4);
            suggestText.setSingleLine();
            suggestText.setEllipsize(TextUtils.TruncateAt.END);

            suggestText.setOnClickListener(onSuggestClick);

            suggestBox.addView(suggestText);

            suggestTexts[ inx ] = suggestText;
        }
    }

    private final View.OnClickListener onSuggestClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            TextView textView = (TextView) view;
            String current = editText.getText().toString();

            if (current.endsWith(" "))
            {
                current += textView.getText();
            }
            else
            {
                int lastSpace = current.lastIndexOf(" ");

                if (lastSpace < 0)
                {
                    current = textView.getText().toString();
                }
                else
                {
                    current = current.substring(0, lastSpace) + " " + textView.getText().toString();
                }
            }

            current += " ";

            editText.setText(current);
            editText.setSelection(current.length());
        }
    };

    public void setStatus(Boolean svlink, Boolean uplink)
    {
        if (uplink != null) this.uplink = uplink;
        if (svlink != null) this.svlink = svlink;
    }

    public void updateTitle()
    {
        String newtitle = remoteNick;

        if ((svlink != null) && svlink) newtitle += " (system)";
        if ((uplink != null) && uplink) newtitle += " (online)";

        setTitle(newtitle);
    }

    public void dispatchMessage(GorillaMessage message)
    {
        Log.d(LOGTAG, "dispatchMessage: message=" + message.toString());

        final String uuid = message.getUUIDBase64();

        ChatFragment cf = new ChatFragment(this);
        cf.setContent(false, chatProfile.remoteNick, message);
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

    public void dispatchResult(GorillaPayloadResult result)
    {
        Long time = result.getTime();
        String uuid = result.getUUIDBase64();
        String status = result.getStatus();

        if ((time == null) || (uuid == null) || (status == null)) return;

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
            GorillaMessage message = cf.getMessage();
            if (message == null) continue;

            GorillaClient.getInstance().putAtomSharedWith(remoteUserUUID, message.getAtom());
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

    private String wantedSuggestPhrase;

    private void postSuggestRequest(String text, int delay)
    {
        wantedSuggestPhrase = text;

        handler.removeCallbacks(requestPhraseSuggestions);
        handler.postDelayed(requestPhraseSuggestions, delay);
    }

    private final Runnable requestPhraseSuggestions = new Runnable()
    {
        @Override
        public void run()
        {
            String phrase = wantedSuggestPhrase;

            Log.d(LOGTAG, "requestPhraseSuggestions: phrase=<" + phrase + ">");

            if ((phrase == null) || (phrase.isEmpty()))
            {
                for (int inx = 0; inx < suggestTexts.length; inx++)
                {
                    suggestTexts[inx].setText("");
                    suggestTexts[inx].setPaddingDip(0, 4, 0, 4);
                }

                return;
            }

            GorillaClient.getInstance().requestPhraseSuggestionsAsync(phrase);
        }
    };

    private GorillaPhraseSuggestion lastPhraseSuggestion;

    public void dispatchPhraseSuggestion(GorillaPhraseSuggestion phraseSuggestion)
    {
        lastPhraseSuggestion = phraseSuggestion;

        List<GorillaPhraseSuggestionHint> hints = phraseSuggestion.getHints();

        for (int inx = 0; inx < suggestTexts.length; inx++)
        {
            if ((hints != null) && (inx < hints.size()))
            {
                GorillaPhraseSuggestionHint hint = hints.get(inx);
                suggestTexts[inx].setText(hint.getHint());
                suggestTexts[inx].setPaddingDip(10, 4, 10, 4);

            }
            else
            {
                suggestTexts[inx].setText("");
                suggestTexts[inx].setPaddingDip(0, 4, 0, 4);
            }
       }
    }
}

