package com.aura.aosp.gorilla.messenger;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIFrameLayout;
import com.aura.aosp.aura.gui.views.GUILinearLayout;
import com.aura.aosp.aura.gui.views.GUIRelativeLayout;
import com.aura.aosp.aura.gui.views.GUITextView;

import com.aura.aosp.aura.common.simple.Simple;

import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends GUILinearLayout
{
    private final static String LOGTAG = ChatFragment.class.getSimpleName();

    private final static String ENDINDENT = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";

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

    private GUIFrameLayout bubbleBox;
    private GUITextView messageBox;

    public ChatFragment(Context context)
    {
        super(context);

        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(Simple.MP, Simple.WC));
        setPaddingDip(GUIDefs.PADDING_SMALL);
    }

    public void setContent(boolean send, String datestring, String username, String attachment, String message)
    {
        if (username == null)
        {
            setContentInfo(message);
        }
        else
        {
            setContentMessage(send, datestring, username, attachment, message);
        }
    }

    private void setContentMessage(boolean send, String datestring, String username, String attachment, String message)
    {
        if (message != null) message += ENDINDENT;

        String timeTag = ((datestring == null) || (datestring.length() < 12)) ? null
                : datestring.substring(8, 10) + ":" + datestring.substring(10, 12);

        GUILinearLayout recvPart = new GUILinearLayout(getContext());
        recvPart.setOrientation(VERTICAL);
        recvPart.setGravity(Gravity.START);
        recvPart.setSizeDip(Simple.MP, Simple.WC);
        recvPart.setSizeDip(Simple.MP, Simple.WC);
        recvPart.setBackgroundColor(0x88008800);

        GUILinearLayout sendPart = new GUILinearLayout(getContext());
        sendPart.setOrientation(VERTICAL);
        sendPart.setGravity(Gravity.END);
        sendPart.setSizeDip(Simple.MP, Simple.WC);
        sendPart.setBackgroundColor(0x88000088);

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
        messageBox.setGravity(send ? Gravity.END : Gravity.START);
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

        if (timeTag != null)
        {
            GUITextView timeBox = new GUITextView(getContext());
            timeBox.setSingleLine(true);
            timeBox.setTextSizeDip(12);

            FrameLayout.LayoutParams lptimetag = new FrameLayout.LayoutParams(Simple.WC, Simple.WC);
            lptimetag.gravity = Gravity.BOTTOM + Gravity.END;

            bubbleBox.addView(timeBox, lptimetag);

            timeBox.setText(timeTag);
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
