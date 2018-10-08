package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Main surrounding "Launcher" view
 */
public class LauncherView extends ConstraintLayout {

    private TextView overlayTextView;

    public LauncherView(Context context) {
        super(context);
        init();
    }

    public LauncherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LauncherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        overlayTextView = findViewById(R.id.overlayText);
    }

    public void updateOverlayText(String text) {
//        String newtitle = title;
//
//        if (ownerIdent != null) newtitle += " " + ownerIdent.getNick();

//        if ((svlink != null) && svlink) newtitle += " (system)";
//        if ((uplink != null) && uplink) newtitle += " (online)";

        if (text != null) {
            overlayTextView.setText(text);
        }
    }
}
