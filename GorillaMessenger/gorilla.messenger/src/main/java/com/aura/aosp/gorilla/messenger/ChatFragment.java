package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUIIconView;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIRelativeLayout;
import com.aura.aosp.aura.gui.views.GUITextView;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.client.GorillaClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends GUILinearLayout
{
    private final static String ENDINDENT = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";

    private final static int[] userColors =
            {
                    0xffff0000,
                    0xff800000,
                    0xff808000,
                    0xff00ff00,
                    0xff008000,
                    0xff00ffff,
                    0xff008080,
                    0xff0000ff,
                    0xff000080,
                    0xffff00ff,
                    0xff800080
            };

    private static final int SENDUSERCOLOR = 0xff0000ff;

    private final static Map<String, Integer> users2Colors = new HashMap<>();

    private String messageUUID;
    private GUIFrameLayout bubbleBox;
    private GUITextView messageBox;
    private GUIIconView statusIcon;
    private JSONObject atom;

    boolean send;
    Long timeQueued;
    Long timeSend;
    Long timeReceived;
    Long timeRead;

    public ChatFragment(Context context)
    {
        super(context);

        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(Simple.MP, Simple.WC));
        setPaddingDip(GUIDefs.PADDING_SMALL);
    }

    @Nullable
    public JSONObject getAtom()
    {
        return atom;
    }

    @Nullable
    public String getMessageUUID()
    {
        return messageUUID;
    }

    public Boolean isSendFragment()
    {
        return send;
    }

    public void setStatusIcon(String status, Long timeStamp)
    {
        try
        {
            JSONObject load = atom.getJSONObject("load");

            if (status.equals("queued"))
            {
                statusIcon.setImageResource(R.drawable.ms_server_wait);
                timeQueued = timeStamp;
                load.put(status, timeStamp);
                makeTimeStatus();
            }

            if (status.equals("send"))
            {
                statusIcon.setImageResource(R.drawable.ms_server_recv);
                timeSend = timeStamp;
                load.put(status, timeStamp);
                makeTimeStatus();
            }

            if (status.equals("received"))
            {
                statusIcon.setImageResource(R.drawable.ms_client_recv);
                timeReceived = timeStamp;
                load.put(status, timeStamp);
                makeTimeStatus();
            }

            if (status.equals("read"))
            {
                statusIcon.setImageResource(R.drawable.ms_client_read);
                load.put(status, timeStamp);
                timeRead = timeStamp;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void makeTimeStatus()
    {
        getHandler().removeCallbacks(displayTimeStatus);
        getHandler().postDelayed(displayTimeStatus, 1000);
    }

    private final Runnable displayTimeStatus = new Runnable()
    {
        @Override
        public void run()
        {
            if (timeQueued == null) return;

            String timing = "";

            if (timeSend != null)
            {
                timing += "S:" + (timeSend - timeQueued) + " ms ";
            }

            if (timeReceived != null)
            {
                timing += "R:" + (timeReceived - timeQueued) + " ms ";
            }

            Toast.makeText(getContext(), timing, Toast.LENGTH_SHORT).show();
        }
    };

    public void setContent(boolean send, String username, JSONObject atom)
    {
        this.atom = atom;

        try
        {
            String uuid = atom.getString("uuid");
            Long time = atom.getLong("time");

            JSONObject load = atom.getJSONObject("load");
            String message = load.getString("message");

            setContent(send, username, uuid, time, null, message);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void setContent(boolean send, String username, String messageUUID, Long timeStamp, String attachment, String message)
    {
        if (username == null)
        {
            setContentInfo(message);
        }
        else
        {
            setContentMessage(send, username, messageUUID, timeStamp, attachment, message);
        }
    }

    private void setContentMessage(boolean send, String username, String messageUUID, Long timeStamp, String attachment, String message)
    {
        this.send = send;
        this.messageUUID = messageUUID;

        if (message != null) message += ENDINDENT;

        GUILinearLayout recvPart = new GUILinearLayout(getContext());
        recvPart.setOrientation(VERTICAL);
        recvPart.setGravity(Gravity.START);
        recvPart.setSizeDip(Simple.MP, send ? Simple.MP: Simple.WC);

        GUILinearLayout sendPart = new GUILinearLayout(getContext());
        sendPart.setOrientation(VERTICAL);
        sendPart.setGravity(Gravity.END);
        sendPart.setSizeDip(Simple.MP, send ? Simple.WC: Simple.MP);

        ((LayoutParams) recvPart.getLayoutParams()).weight = send ? 0.75f : 0.25f;
        ((LayoutParams) sendPart.getLayoutParams()).weight = send ? 0.25f : 0.75f;

        addView(recvPart);
        addView(sendPart);

        bubbleBox = new GUIFrameLayout(getContext());
        bubbleBox.setSizeDip(Simple.WC, Simple.WC);
        bubbleBox.setPaddingDip(GUIDefs.PADDING_TINY);
        bubbleBox.setRoundedCornersDip(GUIDefs.ROUNDED_SMALL, send ? 0xffccffcc : 0xffffffff, send ? 0xffccffcc : 0xffffffff);

        if (send)
        {
            sendPart.addView(bubbleBox);
        }
        else
        {
            recvPart.addView(bubbleBox);
        }

        GUILinearLayout contentBox = new GUILinearLayout(getContext());
        contentBox.setOrientation(VERTICAL);
        contentBox.setGravity(send ? Gravity.END : Gravity.START);
        contentBox.setSizeDip(Simple.WC, Simple.WC);

        bubbleBox.addView(contentBox);

        if (username != null)
        {
            Integer color = SENDUSERCOLOR;

            if (! send)
            {
                color = users2Colors.get(username);

                if (color == null)
                {
                    color = userColors[users2Colors.size() % userColors.length];

                    users2Colors.put(username, color);
                }
            }

            GUITextView userBox = new GUITextView(getContext());
            userBox.setSingleLine(true);
            userBox.setText(username);
            userBox.setTextColor(color);
            userBox.setSizeDip(Simple.WC, Simple.WC);
            userBox.setTextSizeDip(16);

            contentBox.addView(userBox);
        }

        if (attachment != null)
        {
            GUITextView attachmentBox = new GUITextView(getContext());
            attachmentBox.setSingleLine(true);
            attachmentBox.setText(attachment);
            attachmentBox.setBackgroundColor(0xccccccff);
            attachmentBox.setSizeDip(Simple.WC, Simple.WC);
            attachmentBox.setTextSizeDip(16);

            contentBox.addView(attachmentBox);
        }

        messageBox = new GUITextView(getContext());
        messageBox.setGravity(Gravity.START);
        messageBox.setSizeDip(Simple.WC, Simple.WC);
        messageBox.setTextSizeDip(22);
        messageBox.setPaddingDip(GUIDefs.PADDING_ZERO, GUIDefs.PADDING_ZERO, GUIDefs.PADDING_ZERO, GUIDefs.PADDING_TINY);

        FrameLayout.LayoutParams lptexttag = new FrameLayout.LayoutParams(Simple.WC, Simple.WC);
        lptexttag.gravity = Gravity.TOP + Gravity.END;

        contentBox.addView(messageBox, lptexttag);

        if (message != null)
        {
            messageBox.setText(message);
        } else
        {
            messageBox.setTextSizeDip(12);
        }

        GUILinearLayout timeFrame = new GUILinearLayout(getContext());
        timeFrame.setOrientation(HORIZONTAL);
        timeFrame.setSizeDip(Simple.WC, Simple.WC);

        FrameLayout.LayoutParams lptimetag = new FrameLayout.LayoutParams(Simple.WC, Simple.WC);
        lptimetag.gravity = Gravity.BOTTOM + Gravity.END;

        bubbleBox.addView(timeFrame, lptimetag);

        GUITextView timeBox = new GUITextView(getContext());
        timeBox.setSingleLine(true);
        timeBox.setTextSizeDip(12);
        timeBox.setMarginRightDip(1);

        timeFrame.addView(timeBox);

        if (timeStamp != null)
        {
            String datestring = Dates.getLocalDateAndTime(timeStamp);
            String timeTag = datestring.substring(8, 10) + ":" + datestring.substring(10, 12);

            timeBox.setText(timeTag);
        }

        statusIcon = new GUIIconView(getContext());
        statusIcon.setSizeDip(16,16);
        statusIcon.setPaddingDip(GUIDefs.PADDING_ZERO);

        if (send)
        {
            timeFrame.addView(statusIcon);
        }
        else
        {
            //GorillaClient.getInstance().sendPayload()
        }
    }

    public void setContentInfo(String message)
    {
        GUIRelativeLayout centerBox = new GUIRelativeLayout(getContext());
        centerBox.setGravity(Gravity.CENTER);
        centerBox.setSizeDip(Simple.MP, Simple.WC);

        addView(centerBox);

        bubbleBox = new GUIFrameLayout(getContext());

        bubbleBox.setRoundedCornersDip(GUIDefs.ROUNDED_SMALL, 0xffbbddff, 0xffbbddff);

        bubbleBox.setSizeDip(Simple.WC, Simple.WC);
        bubbleBox.setPaddingDip(GUIDefs.PADDING_NORMAL, GUIDefs.PADDING_TINY, GUIDefs.PADDING_NORMAL, GUIDefs.PADDING_TINY);

        centerBox.addView(bubbleBox);

        messageBox = new GUITextView(getContext());
        messageBox.setGravity(Gravity.CENTER_HORIZONTAL);
        messageBox.setSizeDip(Simple.WC, Simple.WC);
        messageBox.setTextSizeDip(22);

        bubbleBox.addView(messageBox);

        messageBox.setText(message);
    }

    public void addContent(String message)
    {
        if (messageBox != null)
        {
            String oldmess = messageBox.getText().toString();

            if (oldmess.endsWith(ENDINDENT))
            {
                oldmess = oldmess.substring(0, oldmess.length() - ENDINDENT.length());
            }

            String newmess = oldmess + (oldmess.equals("") ? "" : "\n") + ">>" + message + ENDINDENT;

            messageBox.setTextSizeDip(22);
            messageBox.setText(newmess);
        }
    }
}
