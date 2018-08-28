package com.aura.aosp.gui.views;

import android.content.Context;

import com.aura.aosp.gui.base.GUIDefs;
import com.aura.aosp.aura.common.simple.Simple;

public class GUIListSpacerView extends GUIRelativeLayout
{
    public GUIListSpacerView(Context context)
    {
        super(context);

        setSizeDip(Simple.MP, GUIDefs.PADDING_SMALL);
    }
}
