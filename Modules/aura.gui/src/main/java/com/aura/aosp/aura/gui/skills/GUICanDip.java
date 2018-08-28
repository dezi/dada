package com.aura.aosp.aura.gui.skills;

public interface GUICanDip
{
    void setSizeDip(int width, int height);
    void setSizeDip(int width, int height, float weight);

    void setPaddingDip(int pad);
    void setPaddingDip(int left, int top, int right, int bottom);

    void setMarginLeftDip(int margin);
    void setMarginTopDip(int margin);
    void setMarginRightDip(int margin);
    void setMarginBottomDip(int margin);
}
