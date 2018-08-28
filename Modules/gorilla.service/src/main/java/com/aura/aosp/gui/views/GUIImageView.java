package com.aura.aosp.gui.views;

import android.support.v7.widget.AppCompatImageView;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.content.Context;
import android.view.ViewGroup;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.aura.aosp.gui.skills.GUICanDip;
import com.aura.aosp.gui.skills.GUICanFocus;
import com.aura.aosp.gui.skills.GUICanFocusDelegate;
import com.aura.aosp.gui.skills.GUICanRestoreBackground;
import com.aura.aosp.gui.skills.GUICanRestoreBackgroundDelegate;
import com.aura.aosp.gui.skills.GUICanRoundedCorners;
import com.aura.aosp.gui.skills.GUICanRoundedCornersDelegate;

import com.aura.aosp.aura.simple.Simple;

public class GUIImageView extends AppCompatImageView implements
        GUICanDip,
        GUICanFocus,
        GUICanRoundedCorners,
        GUICanRestoreBackground
{
    private final static String LOGTAG = GUIImageView.class.getSimpleName();

    public GUIImageView(Context context)
    {
        super(context);

        initSkills();

        setFocusable(false);
    }

    //region Dip implementation.

    @Override
    public void setSizeDip(int width, int height)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        getLayoutParams().width = width > 0 ? Simple.dipToPx(width) : width;
        getLayoutParams().height = height > 0 ? Simple.dipToPx(height) : height;
    }

    @Override
    public void setSizeDip(int width, int height, float weight)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        getLayoutParams().width = width > 0 ? Simple.dipToPx(width) : width;
        getLayoutParams().height = height > 0 ? Simple.dipToPx(height) : height;

        ((LinearLayout.LayoutParams) getLayoutParams()).weight = weight;
    }

    @Override
    public void setPaddingDip(int pad)
    {
        setPadding(Simple.dipToPx(pad), Simple.dipToPx(pad), Simple.dipToPx(pad), Simple.dipToPx(pad));
    }

    @Override
    public void setPaddingDip(int left, int top, int right, int bottom)
    {
        setPadding(Simple.dipToPx(left), Simple.dipToPx(top), Simple.dipToPx(right), Simple.dipToPx(bottom));
    }

    @Override
    public void setMarginLeftDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).leftMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginTopDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).topMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginRightDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).rightMargin = Simple.dipToPx(margin);
    }

    @Override
    public void setMarginBottomDip(int margin)
    {
        if (getLayoutParams() == null)
        {
            setLayoutParams(new LinearLayout.LayoutParams(Simple.WC, Simple.WC));
        }

        ((ViewGroup.MarginLayoutParams) getLayoutParams()).bottomMargin = Simple.dipToPx(margin);
    }

    //endregion Dip implementation.

    //region Skills implementation.

    private GUICanRoundedCornersDelegate canRC;
    private GUICanRestoreBackgroundDelegate canRB;

    private void initSkills()
    {
        canRB = new GUICanRestoreBackgroundDelegate(this);
        canRC = new GUICanRoundedCornersDelegate(this);
    }

    @Override
    public void setBackgroundColor(int color)
    {
        super.setBackgroundColor(color);
        canRB.setBackgroundColor(color);
        canRC.setBackgroundColor(color);
    }

    @Override
    public int getBackgroundColor()
    {
        return canRB.getBackgroundColor();
    }

    @Override
    public void setBackground(Drawable drawable)
    {
        super.setBackground(drawable);
        canRB.setBackground(drawable);
    }

    @Override
    public Drawable getBackground()
    {
        return canRB.getBackground();
    }

    @Override
    public void setRoundedCorners(int radius, int color)
    {
        canRC.setRoundedCorners(radius, color);
    }

    @Override
    public void setRoundedCorners(int radius, int innerColor, int strokeColor)
    {
        canRC.setRoundedCorners(radius, innerColor, strokeColor);
    }

    @Override
    public void setRoundedCornersDip(int radiusdip, int color)
    {
        canRC.setRoundedCornersDip(radiusdip, color);
    }

    @Override
    public void setRoundedCornersDip(int radiusdip, int innerColor, int strokeColor)
    {
        canRC.setRoundedCornersDip(radiusdip, innerColor, strokeColor);
    }

    @Override
    public int getRadius()
    {
        return canRC.getRadius();
    }

    @Override
    public int getRadiusDip()
    {
        return canRC.getRadiusDip();
    }

    @Override
    public int getInnerColor()
    {
        return canRC.getInnerColor();
    }

    @Override
    public int getStrokeColor()
    {
        return canRC.getStrokeColor();
    }

    @Override
    public void saveBackground()
    {
        canRB.saveBackground();
        canRC.saveBackground();
    }

    @Override
    public void restoreBackground()
    {
        canRB.restoreBackground();
        canRC.restoreBackground();
    }

    //endregion Skills implementation.

    //region CanFocus implementation.

    private boolean focus;
    private boolean focusable;
    private boolean highlight;
    private boolean highlightable;

    @Override
    public void setFocusable(boolean focusable)
    {
        this.focusable = focusable;

        super.setFocusable(focusable);

        GUICanFocusDelegate.setupOnFocusChangeListener(this, focusable);
    }

    @Override
    public boolean getIsFocusable()
    {
        return focusable;
    }

    @Override
    public void setHighlight(boolean highlight)
    {
        this.highlight = highlight;

        GUICanFocusDelegate.adjustHighlightState(this);
    }

    @Override
    public boolean getHighlight()
    {
        return this.highlight;
    }

    @Override
    public void setHighlightable(boolean highlightable)
    {
        this.highlightable = highlightable;
    }

    @Override
    public boolean getHighlightable()
    {
        return this.highlightable;
    }

    @Override
    public void setHasFocus(boolean hasfocus)
    {
        this.focus = hasfocus;
    }

    @Override
    public boolean getHasFocus()
    {
        return this.focus;
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener)
    {
        super.setOnClickListener(onClickListener);
    }

    public void onHighlightChanged(View view, boolean highlight)
    {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return GUICanFocusDelegate.onKeyDown(this, keyCode, event) || super.onKeyDown(keyCode, event);
    }

    //endregion CanFocus implementation.

    @Override
    public void setImageResource(int resid)
    {
        setImageResource(resid, 0);
    }

    public void setImageResource(String base64)
    {
        if (base64 != null)
        {
            byte[] rawdata = Base64.decode(base64, Base64.DEFAULT);
            ByteArrayInputStream is = new ByteArrayInputStream(rawdata);

            setImageResource(is, 0);
        }
    }

    public void setImageResource(int resid, int color)
    {
        //
        // Fuck dat "+" + resid makes it read from
        // R.drawable. Otherwise nagging shit.
        //

        InputStream is = getResources().openRawResource(+resid);

        setImageResource(is, color);
    }

    public void setImageResource(InputStream rawstream, int color)
    {
        //
        // Fuck dat. Fucking ImageView takes some
        // wrong resolution. Looks like shit.
        //

        Bitmap bitmap = BitmapFactory.decodeStream(rawstream);

        if (getLayoutParams() != null)
        {
            int nettoWidth = getLayoutParams().width - getPaddingLeft() - getPaddingRight();
            int nettoHeight = getLayoutParams().height - getPaddingTop() - getPaddingBottom();

            int targetWidth = nettoWidth * 2;
            int targetHeight = nettoHeight * 2;

            if ((targetWidth > 0) && (targetHeight > 0))
            {
                Bitmap geil = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(geil);

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);

                Rect srcrect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                Rect dstrect = new Rect(0, 0, targetWidth, targetHeight);

                canvas.drawBitmap(bitmap, srcrect, dstrect, paint);

                if (color != 0)
                {
                    //
                    // Second draw. Changes every pixel which is white
                    // in the canvas to the desired color while
                    // black stayes black.
                    //

                    paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));

                    canvas.drawBitmap(bitmap, srcrect, dstrect, paint);
                }

                bitmap.recycle();

                setImageBitmap(geil);

                return;
            }
        }

        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

        if (color != 0)
        {
            drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        }

        setImageDrawable(drawable);
    }
}