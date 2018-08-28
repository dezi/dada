package com.aura.aosp.gui.views;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;

import com.aura.aosp.gui.base.GUIDefs;
import com.aura.aosp.aura.common.simple.Simple;

public class GUIButtonView extends GUITextView
{
    private boolean isDefaultButton;

    public GUIButtonView(Context context)
    {
        super(context);

        setSizeDip(Simple.WC, Simple.WC);
        setDefaultButton(false);
        setFocusable(true);
        setSingleLine(true);
        setFullWidth(true);
        setAllCaps(true);
        setGravity(Gravity.CENTER_HORIZONTAL + Gravity.CENTER_VERTICAL);
        setTextSizeDip(GUIDefs.FONTSIZE_BUTTONS);
        setPaddingDip(GUIDefs.PADDING_SMALL);
    }

    @Override
    public void setSingleLine(boolean singleLine)
    {
        super.setSingleLine(singleLine);

        //
        // For some reason setSingleLine fucks up
        // the setAllCaps setting. Damm it.
        //

        setAllCaps(true);
    }

    public void setDefaultButton(boolean set)
    {
        isDefaultButton = set;

        if (isDefaultButton)
        {
            setTextColor(Color.WHITE);
            setBackgroundColor(Color.BLACK);
            setRoundedCorners(GUIDefs.ROUNDED_ZERO, Color.BLACK, Color.BLACK);
        }
        else
        {
            setTextColor(Color.BLACK);
            setBackgroundColor(Color.WHITE);
            setRoundedCorners(GUIDefs.ROUNDED_ZERO, Color.WHITE, Color.BLACK);
        }
    }

    public void setFullWidth(boolean set)
    {
        ViewGroup.LayoutParams lp = getLayoutParams();
        setSizeDip(set ? Simple.MP : Simple.WC, (lp == null) ? Simple.WC : lp.height);
    }
}
