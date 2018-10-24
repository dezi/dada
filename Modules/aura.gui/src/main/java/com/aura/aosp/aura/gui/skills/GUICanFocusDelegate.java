package com.aura.aosp.aura.gui.skills;

import android.support.annotation.Nullable;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.gui.base.GUIDefs;
import com.aura.aosp.aura.gui.views.GUIEditText;


public class GUICanFocusDelegate
{
    private final static String LOGTAG = GUICanFocusDelegate.class.getSimpleName();

    private static View focusedView;

    @Nullable
    public static View getFocusedView()
    {
        return focusedView;
    }

    @Nullable
    public static void setFocusedView(View view, boolean hasFocus)
    {
        if (hasFocus)
        {
            focusedView = view;
        }
        else
        {
            if (focusedView == view)
            {
                focusedView = null;
            }
        }
    }

    public final static View.OnFocusChangeListener genericOnFocusChangeListener = new View.OnFocusChangeListener()
    {
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            setFocusedView(view, hasFocus);

            GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;
            GUICanToast gt = view instanceof GUICanToast ? (GUICanToast) view : null;

            //GUIPlugin pi = findPlugin(view);

            if (gf == null) return;

            if (hasFocus)
            {
                Simple.hideSoftKeyBoard(view);

                //
                // Display focus frame around image.
                //

                gf.saveBackground();

                gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS);

                gf.setHasFocus(true);

                if (gt != null)
                {
                    GUICanToastDelegate.displayToast(gt.getToastFocus());
                }

                //if (pi != null) pi.onHighlightFrame(true);
            }
            else
            {
                //
                // Make neutral again.
                //

                gf.restoreBackground();

                gf.setHasFocus(false);

                if (gf.getHighlightable())
                {
                    if (gf.getHighlight())
                    {
                        gf.setHighlight(false);
                    }

                    if (Simple.isTV())
                    {
                        if (view instanceof GUIEditText)
                        {
                            GUIEditText et = (GUIEditText) view;

                            et.setEnabled(false);
                            et.setInputType(InputType.TYPE_NULL);
                        }
                    }
                }

                //if (pi != null) pi.onHighlightFrame(false);
            }
        }
    };

    public static void adjustHighlightState(View view)
    {
        GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;
        GUICanToast gt = view instanceof GUICanToast ? (GUICanToast) view : null;

        if ((gf != null) && gf.getHighlightable())
        {
            if (gf.getHighlight())
            {
                if (! gf.getHasFocus())
                {
                    gf.saveBackground();
                }

                gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS_HIGHLIGHT);

                if (gt != null)
                {
                    GUICanToastDelegate.displayToast(gt.getToastHighlight());
                }

                Log.d("adjustHighlightState: onHighlightStarted.");
            }
            else
            {
                if (gf.getHasFocus())
                {
                    gf.setRoundedCorners(0, gf.getBackgroundColor(), GUIDefs.COLOR_TV_FOCUS);

                    if (gt != null)
                    {
                        GUICanToastDelegate.displayToast(gt.getToastFocus());
                    }
                }
                else
                {
                    gf.restoreBackground();
                }

                Log.d("adjustHighlightState: onHighlightFinished.");
            }

            gf.onHighlightChanged(view, gf.getHighlight());
        }
    }

    public static void setupOnFocusChangeListener(View view, boolean focusable)
    {
        if (Simple.isTV())
        {
            if (view instanceof GUICanFocus)
            {
                if (focusable)
                {
                    int padneed = Simple.dipToPx(2);

                    int padleft = view.getPaddingLeft();
                    int padtop = view.getPaddingTop();
                    int padright = view.getPaddingRight();
                    int padbottom = view.getPaddingBottom();

                    if (padleft < padneed) padleft = padneed;
                    if (padtop < padneed) padtop = padneed;
                    if (padright < padneed) padright = padneed;
                    if (padbottom < padneed) padbottom = padneed;

                    view.setPadding(padleft, padtop, padright, padbottom);

                    view.setOnFocusChangeListener(genericOnFocusChangeListener);
                }
                else
                {
                    view.setOnFocusChangeListener(null);
                }
            }
        }
    }

    public static boolean onKeyDown(View view, int keyCode, KeyEvent event)
    {
        GUICanFocus gf = view instanceof GUICanFocus ? (GUICanFocus) view : null;

        if (Simple.isTV()
                && (gf != null)
                && gf.getHasFocus()
                && gf.getHighlightable()
                && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER))
        {
            gf.setHighlight(! gf.getHighlight());

            if (view instanceof GUIEditText)
            {
                GUIEditText et = (GUIEditText) view;

                if (gf.getHighlight())
                {
                    et.setEnabled(true);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else
                {
                    et.setEnabled(false);
                    et.setInputType(InputType.TYPE_NULL);
                }
            }

            return true;
        }

        return false;
    }

    /*
    @Nullable
    private static GUIPlugin findPlugin(View view)
    {
        while ((view != null) && !(view instanceof GUIPlugin))
        {
            if (view.getParent() instanceof View)
            {
                view = (View) view.getParent();
            }
            else
            {
                //
                // We have reached the top decor view
                // and did not find anythinmg so far.
                //

                view = null;
                break;
            }
        }

        return (GUIPlugin) view;
    }
    */
}
