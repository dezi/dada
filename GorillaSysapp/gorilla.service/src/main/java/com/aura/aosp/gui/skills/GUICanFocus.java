package com.aura.aosp.gui.skills;

import android.view.View;

public interface GUICanFocus extends GUICanRoundedCorners
{
    void setFocusable(boolean focusable);
    boolean getIsFocusable();

    void setHasFocus(boolean hasfocus);
    boolean getHasFocus();

    void setHighlight(boolean highlight);
    boolean getHighlight();

    void setHighlightable(boolean highlighttable);
    boolean getHighlightable();

    int getBackgroundColor();

    void onHighlightChanged(View view, boolean hashighlight);

    View.OnFocusChangeListener getOnFocusChangeListener();
    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
