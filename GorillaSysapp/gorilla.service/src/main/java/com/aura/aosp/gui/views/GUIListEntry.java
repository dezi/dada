package com.aura.aosp.gui.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.aura.aosp.gui.base.GUIDefs;
import com.aura.aosp.aura.simple.Simple;

public class GUIListEntry extends GUILinearLayout
{
    public String idtag;
    public boolean isinuse;

    public GUIFrameLayout levelView;
    public GUIIconView iconView;
    public GUITextView headerViev;
    public GUITextView infoView;

    public GUIListEntry(Context context)
    {
        super(context);

        isinuse = true;

        setFocusable(true);
        setOrientation(HORIZONTAL);
        setPaddingDip(GUIDefs.PADDING_TINY);
        setBackgroundColor(GUIDefs.COLOR_LIGHT_TRANSPARENT);

        levelView = new GUIFrameLayout(context);
        levelView.setVisibility(GONE);
        addView(levelView);

        iconView = new GUIIconView(context);
        addView(iconView);

        GUILinearLayout entryCenter = new GUILinearLayout(context);
        entryCenter.setGravity(Gravity.START + Gravity.CENTER_VERTICAL);
        entryCenter.setSizeDip(Simple.MP, Simple.MP, 1.0f);

        addView(entryCenter);

        GUILinearLayout entryBox = new GUILinearLayout(context);
        entryBox.setOrientation(VERTICAL);
        entryBox.setSizeDip(Simple.MP, Simple.WC);

        entryCenter.addView(entryBox);

        headerViev = new GUITextView(context);
        headerViev.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        headerViev.setSingleLine(true);
        headerViev.setEllipsize(TextUtils.TruncateAt.END);

        entryBox.addView(headerViev);

        infoView = new GUITextView(context);
        infoView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);
        infoView.setSingleLine(true);
        infoView.setEllipsize(TextUtils.TruncateAt.END);

        entryBox.addView(infoView);
    }

    public void setLevel(int level)
    {
        if (level == 0)
        {
            levelView.setVisibility(GONE);
        }
        else
        {
            levelView.setVisibility(VISIBLE);
            levelView.setSizeDip(GUIDefs.PADDING_XLARGE, Simple.MP);
        }
    }

    private OnFocusChangeListener onFocusChangeListenerSaved;
    private OnFocusChangeListener onFocusChangeListenerCustom;

    private final OnFocusChangeListener onFocusChangeListenerBoth = new OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            if (onFocusChangeListenerSaved != null) onFocusChangeListenerSaved.onFocusChange(view, hasFocus);
            if (onFocusChangeListenerCustom != null) onFocusChangeListenerCustom.onFocusChange(view, hasFocus);
        }
    };

    public void setOnFocusChangeListenerCustom(OnFocusChangeListener onFocusChangeListener)
    {
        if (onFocusChangeListenerSaved == null)
        {
            onFocusChangeListenerSaved = getOnFocusChangeListener();
        }

        onFocusChangeListenerCustom = onFocusChangeListener;
        setOnFocusChangeListener(onFocusChangeListenerBoth);
    }
}

