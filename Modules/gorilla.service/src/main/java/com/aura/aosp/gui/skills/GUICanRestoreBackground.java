package com.aura.aosp.gui.skills;

import android.graphics.drawable.Drawable;

public interface GUICanRestoreBackground
{
    void setBackgroundColor(int color);
    void setBackground(Drawable drawable);

    int getBackgroundColor();
    Drawable getBackground();

    void saveBackground();
    void restoreBackground();
}
