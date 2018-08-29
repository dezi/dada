package com.aura.aosp.aura.gui.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.common.simple.Simple;

public class GUIListEntry extends GUILinearLayout
{
    public String idtag;
    public boolean isinuse;

    public GUIFrameLayout levelView;
    public GUIIconView iconView;
    public GUILinearLayout headerBox;
    public GUITextView headerViev;
    public GUITextView dateView;
    public GUITextView infoView;
    public GUIRelativeLayout actionView;
    public GUIImageView actionIcon;

    public GUIListEntry(Context context)
    {
        super(context);

        isinuse = true;

        setFocusable(true);
        setOrientation(HORIZONTAL);
        setPaddingDip(GUIDefs.PADDING_ZERO);
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

        headerBox = new GUILinearLayout(context);
        headerBox.setOrientation(HORIZONTAL);
        headerBox.setSizeDip(Simple.MP,Simple.WC);

        entryBox.addView(headerBox);

        headerViev = new GUITextView(context);
        headerViev.setTextSizeDip(GUIDefs.FONTSIZE_HEADERS);
        headerViev.setSingleLine(true);
        headerViev.setEllipsize(TextUtils.TruncateAt.END);
        headerViev.setSizeDip(Simple.MP,Simple.WC, 1.0f);

        headerBox.addView(headerViev);

        dateView = new GUITextView(context);
        dateView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);
        dateView.setTextColor(GUIDefs.COLOR_GRAY);
        dateView.setSingleLine(true);
        dateView.setSizeDip(Simple.WC,Simple.WC);
        dateView.setText("Montag");

        headerBox.addView(dateView);

        infoView = new GUITextView(context);
        infoView.setTextSizeDip(GUIDefs.FONTSIZE_INFOS);
        infoView.setSingleLine(true);
        infoView.setEllipsize(TextUtils.TruncateAt.END);

        entryBox.addView(infoView);

        actionView = new GUIRelativeLayout(context);
        actionView.setSizeDip(Simple.WC,Simple.MP);
        actionView.setGravity(Gravity.CENTER_VERTICAL);

        addView(actionView);

        actionIcon = new GUIImageView(context);
        actionIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        actionIcon.setSizeDip(30, Simple.MP);
        actionIcon.setPaddingDip(GUIDefs.ICON_PADD);

        actionView.addView(actionIcon);
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

