package com.aura.aosp.aura.gui.skills;

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
