package com.aura.aosp.aura.gui.skills;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

public class GUICanRestoreBackgroundDelegate implements GUICanRestoreBackground
{
    public View view;

    public boolean usedColor;
    public boolean usedDrawable;

    public int color = Color.TRANSPARENT;
    public Drawable drawable;

    public int colorSaved;
    public Drawable drawableSaved;

    public GUICanRestoreBackgroundDelegate(View view)
    {
        this.view = view;
    }

    @Override
    public void setBackgroundColor(int color)
    {
        usedColor = true;
        this.color = color;
    }

    @Override
    public int getBackgroundColor()
    {
        return color;
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        usedDrawable = true;
        this.drawable = drawable;
    }

    @Override
    public Drawable getBackground()
    {
        return drawable;
    }

    @Override
    public void saveBackground()
    {
        colorSaved = color;
        drawableSaved = drawable;
    }

    @Override
    public void restoreBackground()
    {
        if (usedColor) view.setBackgroundColor(colorSaved);
        if (usedDrawable) view.setBackground(drawableSaved);
    }
}
